package cz.dcervenka.wear.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cz.dcervenka.core.presentation.designsystem_wear.RunKeeperTheme
import cz.dcervenka.wear.run.presentation.TrackerScreenRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            RunKeeperTheme {
                TrackerScreenRoot()
            }
        }
    }
}
