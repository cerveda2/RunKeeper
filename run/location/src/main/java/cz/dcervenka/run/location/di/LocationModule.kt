package cz.dcervenka.run.location.di

import cz.dcervenka.run.domain.LocationObserver
import cz.dcervenka.run.location.AndroidLocationObserver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val locationModule = module {
    singleOf(::AndroidLocationObserver).bind<LocationObserver>()
}