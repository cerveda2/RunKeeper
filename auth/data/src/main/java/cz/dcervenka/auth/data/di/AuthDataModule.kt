package cz.dcervenka.auth.data.di

import cz.dcervenka.auth.data.AuthRepositoryImpl
import cz.dcervenka.auth.data.EmailPatternValidator
import cz.dcervenka.auth.domain.AuthRepository
import cz.dcervenka.auth.domain.PatternValidator
import cz.dcervenka.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}