@file:OptIn(ExperimentalCoroutinesApi::class)

package cz.dcervenka.run.domain

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import cz.dcervenka.core.connectivity.domain.messaging.MessagingAction
import cz.dcervenka.core.domain.location.Location
import cz.dcervenka.core.domain.location.LocationWithAltitude
import cz.dcervenka.test.LocationObserverFake
import cz.dcervenka.test.PhoneToWatchConnectorFake
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.math.roundToInt

class RunningTrackerTest {

    private lateinit var runningTracker: RunningTracker
    private lateinit var locationObserverFake: LocationObserverFake
    private lateinit var watchConnectorFake: PhoneToWatchConnectorFake

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: CoroutineScope

    @BeforeEach
    fun setUp() {
        locationObserverFake = LocationObserverFake()
        watchConnectorFake = PhoneToWatchConnectorFake()

        testDispatcher = UnconfinedTestDispatcher()
        testScope = CoroutineScope(testDispatcher)

        Dispatchers.setMain(testDispatcher)

        runningTracker = RunningTracker(
            locationObserver = locationObserverFake,
            applicationScope = testScope,
            watchConnector = watchConnectorFake
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun testCombiningTestData() = runTest {
        runningTracker.runData.test {
            skipItems(1)

            runningTracker.startObservingLocation()
            runningTracker.setIsTracking(true)

            val location1 = LocationWithAltitude(Location(1.0, 1.0), 1.0)
            locationObserverFake.trackLocation(location1)
            val emission1 = awaitItem()

            val location2 = LocationWithAltitude(Location(2.0, 2.0), 2.0)
            locationObserverFake.trackLocation(location2)
            val emission2 = awaitItem()

            watchConnectorFake.sendFromWatchToPhone(MessagingAction.HeartRateUpdate(160))
            val emission3 = awaitItem()

            watchConnectorFake.sendFromWatchToPhone(MessagingAction.HeartRateUpdate(140))
            val emission4 = awaitItem()

            testScope.cancel()

            assertThat(emission1.locations[0][0].location).isEqualTo(location1)
            assertThat(emission2.locations[0][1].location).isEqualTo(location2)
            assertThat(emission3.heartRates).isEqualTo(listOf(160))
            assertThat(emission4.heartRates).isEqualTo(listOf(160, 140))

            val expectedDistance = 156.9 * 1000L
            val tolerance = 0.03
            val lowerBound = (expectedDistance * (1 - tolerance)).roundToInt()
            val upperBound = (expectedDistance * (1 + tolerance)).roundToInt()
            assertThat(emission4.distanceMeters).isBetween(lowerBound, upperBound)

            assertThat(emission4.locations.first()).hasSize(2)
        }
    }
}