package cz.dcervenka.core.connectivity.data.di

import cz.dcervenka.core.connectivity.data.WearNodeDiscovery
import cz.dcervenka.core.connectivity.data.messaging.WearMessagingClient
import cz.dcervenka.core.connectivity.domain.NodeDiscovery
import cz.dcervenka.core.connectivity.domain.messaging.MessagingClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreConnectivityDataModule = module {
    singleOf(::WearNodeDiscovery).bind<NodeDiscovery>()
    singleOf(::WearMessagingClient).bind<MessagingClient>()
}