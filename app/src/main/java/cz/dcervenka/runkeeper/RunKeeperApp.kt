package cz.dcervenka.runkeeper

import android.app.Application
import cz.dcervenka.auth.data.di.authDataModule
import cz.dcervenka.auth.presentation.di.authViewModelModule
import cz.dcervenka.core.data.di.coreDataModule
import cz.dcervenka.core.database.di.databaseModule
import cz.dcervenka.run.location.di.locationModule
import cz.dcervenka.run.network.di.networkModule
import cz.dcervenka.run.presentation.di.runPresentationModule
import cz.dcervenka.runkeeper.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RunKeeperApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

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
                coreDataModule,
                runPresentationModule,
                locationModule,
                databaseModule,
                networkModule,
            )
        }
    }
}