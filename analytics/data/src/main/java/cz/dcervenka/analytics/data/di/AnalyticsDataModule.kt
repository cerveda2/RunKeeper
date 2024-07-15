package cz.dcervenka.analytics.data.di

import cz.dcervenka.analytics.data.RoomAnalyticsRepository
import cz.dcervenka.analytics.domain.AnalyticsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
}