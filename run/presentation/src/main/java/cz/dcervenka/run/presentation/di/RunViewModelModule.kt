package cz.dcervenka.run.presentation.di

import cz.dcervenka.run.presentation.active_run.ActiveRunViewModel
import cz.dcervenka.run.presentation.run_overview.RunOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val runViewModelModule = module {
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}
