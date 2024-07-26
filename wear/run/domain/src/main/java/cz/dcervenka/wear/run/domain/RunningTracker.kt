@file:OptIn(ExperimentalCoroutinesApi::class)

package cz.dcervenka.wear.run.domain

import cz.dcervenka.core.connectivity.domain.messaging.MessagingAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration

class RunningTracker(
    private val watchToPhoneConnector: PhoneConnector,
    private val exerciseTracker: ExerciseTracker,
    applicationScope: CoroutineScope
) {
    private val _exerciseMetrics = MutableStateFlow(ExerciseMetrics(0, 0))
    val exerciseMetrics = _exerciseMetrics.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _isTrackable = MutableStateFlow(false)
    val isTrackable = _isTrackable.asStateFlow()

    val distanceMeters = watchToPhoneConnector
        .messagingActions
        .filterIsInstance<MessagingAction.DistanceUpdate>()
        .map { it.distanceMeters }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            0
        )

    val elapsedTime = watchToPhoneConnector
        .messagingActions
        .filterIsInstance<MessagingAction.TimeUpdate>()
        .map { it.elapsedDuration }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            Duration.ZERO
        )

    init {
        watchToPhoneConnector
            .messagingActions
            .onEach { action ->
                when (action) {
                    is MessagingAction.Trackable -> {
                        _isTrackable.value = true
                    }

                    is MessagingAction.Untrackable -> {
                        _isTrackable.value = false
                    }

                    else -> Unit
                }
            }
            .launchIn(applicationScope)

        watchToPhoneConnector
            .connectedNode
            .filterNotNull()
            .onEach {
                exerciseTracker.prepareExercise()
            }
            .launchIn(applicationScope)

        isTracking
            .flatMapLatest { isTracking ->
                if (isTracking) {
                    exerciseTracker.exerciseMetrics
                } else flowOf()
            }
            .onEach { metrics ->
                watchToPhoneConnector.sendActionToPhone(
                    MessagingAction.WatchMetricsUpdate(
                        heartRate = metrics.heartRate,
                        steps = metrics.steps
                    )
                )
                _exerciseMetrics.value = ExerciseMetrics(
                    heartRate = metrics.heartRate,
                    steps = metrics.steps
                )
            }
            .launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean) {
        _isTracking.value = isTracking
    }
}