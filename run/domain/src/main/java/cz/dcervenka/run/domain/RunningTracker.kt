@file:OptIn(ExperimentalCoroutinesApi::class)

package cz.dcervenka.run.domain

import cz.dcervenka.core.connectivity.domain.messaging.MessagingAction
import cz.dcervenka.core.domain.Timer
import cz.dcervenka.core.domain.location.LocationTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Application scope is used instead of a viewModel scope because of the usage of foreground service
 * Because of this it is ensured that the app won't experience process death so the state of
 * this class is not lost.
 * All the singletons will survive even if we close the app manually
 */

class RunningTracker(
    private val locationObserver: LocationObserver,
    applicationScope: CoroutineScope,
    private val watchConnector: WatchConnector,
) {
    private val _runData = MutableStateFlow(RunData())
    val runData = _runData.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val isObservingLocation = MutableStateFlow(false)

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()

    val currentLocation = isObservingLocation
        .flatMapLatest { isObserving ->
            if (isObserving) {
                locationObserver.observeLocation(1000L)
            } else flowOf()
        }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            null
        )

    private val exerciseMetrics = isTracking
        .flatMapLatest { isTracking ->
            if (isTracking) {
                watchConnector.messagingActions
            } else flowOf()
        }
        .filterIsInstance<MessagingAction.WatchMetricsUpdate>()
        .map { it }
        .runningFold(initial = AccumulatedExerciseMetrics()) { currentMetrics, newMetrics ->
            AccumulatedExerciseMetrics(
                heartRates = currentMetrics.heartRates + newMetrics.heartRate,
                steps = currentMetrics.steps + newMetrics.steps,
            )
        }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            AccumulatedExerciseMetrics()
        )

    init {
        _isTracking
            .onEach { isTracking ->
                if (!isTracking) {
                    val newList = buildList {
                        addAll(runData.value.locations)
                        add(emptyList<LocationTimestamp>())
                    }.toList()
                    _runData.update {
                        it.copy(
                            locations = newList
                        )
                    }
                }
            }
            .flatMapLatest { isTracking ->
                if (isTracking) {
                    Timer.timeAndEmit()
                } else flowOf()
            }
            .onEach {
                _elapsedTime.value += it
            }
            .launchIn(applicationScope)

        currentLocation
            .filterNotNull()
            .combineTransform(_isTracking) { location, isTracking ->
                if (isTracking) {
                    emit(location)
                }
            }
            .zip(_elapsedTime) { location, elapsedTime ->
                LocationTimestamp(
                    location = location,
                    durationTimestamp = elapsedTime
                )
            }
            .combine(exerciseMetrics) { location, exerciseMetrics ->
                val currentLocations = runData.value.locations
                val lastLocationsList = if (currentLocations.isNotEmpty()) {
                    currentLocations.last() + location
                } else listOf(location)
                val newLocationsList = currentLocations.replaceLast(lastLocationsList.distinct())

                val distanceMeters = LocationDataCalculator.getTotalDistanceMeters(
                    locations = newLocationsList
                )
                val distanceKm = distanceMeters / 1000.0
                val currentDuration = location.durationTimestamp

                val avgSecondsPerKm = if (distanceKm == 0.0) 0 else
                    (currentDuration.inWholeSeconds / distanceKm).roundToInt()

                _runData.update {
                    RunData(
                        distanceMeters = distanceMeters,
                        pace = avgSecondsPerKm.seconds,
                        locations = newLocationsList,
                        heartRates = exerciseMetrics.heartRates,
                        steps = exerciseMetrics.steps
                    )
                }
            }
            .launchIn(applicationScope)

        elapsedTime
            .onEach {
                watchConnector.sendActionToWatch(MessagingAction.TimeUpdate(it))
            }
            .launchIn(applicationScope)

        runData
            .map { it.distanceMeters }
            .distinctUntilChanged()
            .onEach {
                watchConnector.sendActionToWatch(MessagingAction.DistanceUpdate(it))
            }
            .launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean) {
        this._isTracking.value = isTracking
    }

    fun startObservingLocation() {
        isObservingLocation.value = true
        watchConnector.setIsTrackable(true)
    }

    fun stopObservingLocation() {
        isObservingLocation.value = false
        watchConnector.setIsTrackable(false)
    }

    fun finishRun() {
        stopObservingLocation()
        setIsTracking(false)
        _elapsedTime.value = Duration.ZERO
        _runData.value = RunData()
    }
}

private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>> {
    if (this.isEmpty()) {
        return listOf(replacement)
    }
    return this.dropLast(1) + listOf(replacement)
}