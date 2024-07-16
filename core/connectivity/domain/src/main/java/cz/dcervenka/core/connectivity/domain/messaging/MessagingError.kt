package cz.dcervenka.core.connectivity.domain.messaging

import cz.dcervenka.core.domain.util.Error

enum class MessagingError : Error {
    CONNECTION_INTERRUPTED,
    DISCONNECTED,
    UNKNOWN
}