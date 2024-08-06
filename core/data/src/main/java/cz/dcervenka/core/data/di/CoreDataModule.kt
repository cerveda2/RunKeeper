package cz.dcervenka.core.data.di

import cz.dcervenka.core.data.run.OfflineFirstRunRepository
import cz.dcervenka.core.domain.run.RunRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()
}