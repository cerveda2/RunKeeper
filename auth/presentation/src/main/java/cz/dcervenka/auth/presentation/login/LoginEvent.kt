package cz.dcervenka.auth.presentation.login

import cz.dcervenka.core.presentation.ui.UiText

sealed interface LoginEvent {
    data class Error(val error: UiText) : LoginEvent
    data object LoginSuccess : LoginEvent
}