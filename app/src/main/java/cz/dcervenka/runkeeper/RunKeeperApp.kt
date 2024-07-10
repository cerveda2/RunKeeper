package cz.dcervenka.runkeeper

import android.app.Application
import cz.dcervenka.auth.data.di.authDataModule
import cz.dcervenka.auth.presentation.di.authViewModelModule
import cz.dcervenka.runkeeper.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RunKeeperApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RunKeeperApp)
            modules(
                authDataModule,
                authViewModelModule,
                appModule,
            )
        }
    }
}