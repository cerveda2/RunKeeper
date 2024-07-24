package cz.dcervenka.core.data.di

import cz.dcervenka.core.data.auth.EncryptedSessionStorage
import cz.dcervenka.core.data.networking.HttpClientFactory
import cz.dcervenka.core.data.run.OfflineFirstRunRepository
import cz.dcervenka.core.domain.SessionStorage
import cz.dcervenka.core.domain.run.RunRepository
import io.ktor.client.engine.cio.CIO
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(get()).build(CIO.create())
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()

    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()
}