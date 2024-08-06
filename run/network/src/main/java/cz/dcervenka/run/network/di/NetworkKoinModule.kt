package cz.dcervenka.run.network.di

import cz.dcervenka.core.domain.run.RemoteRunDataSource
import cz.dcervenka.run.network.FirebaseRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::FirebaseRunDataSource).bind<RemoteRunDataSource>()
}