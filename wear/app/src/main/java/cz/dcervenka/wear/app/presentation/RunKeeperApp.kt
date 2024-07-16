package cz.dcervenka.wear.app.presentation

import android.app.Application
import cz.dcervenka.wear.run.presentation.di.runPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RunKeeperApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@RunKeeperApp)
            modules(
                runPresentationModule
            )
        }
    }
}