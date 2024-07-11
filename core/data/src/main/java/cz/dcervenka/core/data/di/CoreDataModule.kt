package cz.dcervenka.core.data.di

import cz.dcervenka.core.data.auth.EncryptedSessionStorage
import cz.dcervenka.core.data.networking.HttpClientFactory
import cz.dcervenka.core.domain.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(get()).build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
}