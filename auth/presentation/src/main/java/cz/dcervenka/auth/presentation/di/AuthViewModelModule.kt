package cz.dcervenka.auth.presentation.di

import cz.dcervenka.auth.presentation.login.LoginViewModel
import cz.dcervenka.auth.presentation.register.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authViewModelModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::LoginViewModel)
}