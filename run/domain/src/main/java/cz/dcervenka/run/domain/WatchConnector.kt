package cz.dcervenka.run.domain

import cz.dcervenka.core.connectivity.domain.DeviceNode
import cz.dcervenka.core.connectivity.domain.messaging.MessagingAction
import cz.dcervenka.core.connectivity.domain.messaging.MessagingError
import cz.dcervenka.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WatchConnector {
    val connectedDevice: StateFlow<DeviceNode?>
    val messagingActions: Flow<MessagingAction>

    suspend fun sendActionToWatch(action: MessagingAction): EmptyResult<MessagingError>
    fun setIsTrackable(isTrackable: Boolean)
}