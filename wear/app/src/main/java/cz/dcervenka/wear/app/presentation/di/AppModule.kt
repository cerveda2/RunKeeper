package cz.dcervenka.wear.app.presentation.di

import cz.dcervenka.wear.app.presentation.RunKeeperApp
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single {
        (androidApplication() as RunKeeperApp).applicationScope
    }
}