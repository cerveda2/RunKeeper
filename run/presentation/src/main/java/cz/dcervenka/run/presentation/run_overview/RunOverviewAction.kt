package cz.dcervenka.run.presentation.run_overview

import cz.dcervenka.run.presentation.run_overview.model.RunUi

sealed interface RunOverviewAction {
    data object OnStartClick : RunOverviewAction
    data object OnLogoutClick : RunOverviewAction
    data object OnAnalyticsClick : RunOverviewAction
    data class DeleteRun(val runUi: RunUi) : RunOverviewAction
}