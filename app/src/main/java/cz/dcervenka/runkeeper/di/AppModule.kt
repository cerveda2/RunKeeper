package cz.dcervenka.runkeeper.di

import cz.dcervenka.runkeeper.MainViewModel
import cz.dcervenka.runkeeper.RunKeeperApp
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<CoroutineScope> {
        (androidApplication() as RunKeeperApp).applicationScope
    }

    viewModelOf(::MainViewModel)
}