package cz.dcervenka.core.presentation.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    primary = Green,
    background = Black,
    surface = DarkGray,
    secondary = White,
    tertiary = White,
    primaryContainer = Green30,
    onPrimary = Black,
    onBackground = White,
    onSurface = White,
    onSurfaceVariant = Gray,
)

@Composable
fun RunKeeperTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme (
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}