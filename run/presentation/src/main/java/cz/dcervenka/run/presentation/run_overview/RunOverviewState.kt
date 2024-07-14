package cz.dcervenka.run.presentation.run_overview

import cz.dcervenka.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
    val runs: List<RunUi> = emptyList()
)
