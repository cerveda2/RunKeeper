package cz.dcervenka.run.data.di

import cz.dcervenka.core.domain.run.SyncRunScheduler
import cz.dcervenka.run.data.CreateRunWorker
import cz.dcervenka.run.data.DeleteRunWorker
import cz.dcervenka.run.data.FetchRunsWorker
import cz.dcervenka.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)

    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}