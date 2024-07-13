package cz.dcervenka.run.presentation.di

import cz.dcervenka.run.domain.RunningTracker
import cz.dcervenka.run.presentation.active_run.ActiveRunViewModel
import cz.dcervenka.run.presentation.run_overview.RunOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val runPresentationModule = module {
    singleOf(::RunningTracker)

    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}
