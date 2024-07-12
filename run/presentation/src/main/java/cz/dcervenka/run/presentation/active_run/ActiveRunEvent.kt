package cz.dcervenka.run.presentation.active_run

import cz.dcervenka.core.presentation.ui.UiText

sealed interface ActiveRunEvent {
    data class Error(val error: UiText) : ActiveRunEvent
    data object RunSaved : ActiveRunEvent
}