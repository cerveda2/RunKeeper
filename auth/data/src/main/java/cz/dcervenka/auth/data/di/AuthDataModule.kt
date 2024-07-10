package cz.dcervenka.auth.data.di

import cz.dcervenka.auth.data.EmailPatternValidator
import cz.dcervenka.auth.domain.PatternValidator
import cz.dcervenka.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)
}