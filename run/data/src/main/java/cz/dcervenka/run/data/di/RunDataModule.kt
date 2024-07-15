package cz.dcervenka.run.data.di

import cz.dcervenka.run.data.CreateRunWorker
import cz.dcervenka.run.data.DeleteRunWorker
import cz.dcervenka.run.data.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)

}