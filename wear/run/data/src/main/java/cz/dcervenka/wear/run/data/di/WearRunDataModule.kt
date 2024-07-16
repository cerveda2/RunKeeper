package cz.dcervenka.wear.run.data.di

import cz.dcervenka.wear.run.data.HealthServicesExerciseTracker
import cz.dcervenka.wear.run.domain.ExerciseTracker
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val wearRunDataModule = module {
    singleOf(::HealthServicesExerciseTracker).bind<ExerciseTracker>()
}