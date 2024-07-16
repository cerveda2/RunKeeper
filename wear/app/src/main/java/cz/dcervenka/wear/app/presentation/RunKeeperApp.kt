package cz.dcervenka.wear.app.presentation

import android.app.Application
import cz.dcervenka.core.connectivity.data.di.coreConnectivityDataModule
import cz.dcervenka.wear.app.presentation.di.appModule
import cz.dcervenka.wear.run.data.di.wearRunDataModule
import cz.dcervenka.wear.run.presentation.di.wearRunPresentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RunKeeperApp: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@RunKeeperApp)
            modules(
                appModule,
                wearRunPresentationModule,
                wearRunDataModule,
                coreConnectivityDataModule,
            )
        }
    }
}