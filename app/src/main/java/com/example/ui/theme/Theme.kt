package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val StudioDarkColorScheme = darkColorScheme(
    primary = StudioCyan,
    onPrimary = StudioObsidianDark,
    primaryContainer = StudioVioletDeep,
    onPrimaryContainer = StudioTextWhite,
    secondary = StudioViolet,
    onSecondary = StudioTextWhite,
    tertiary = StudioCoral,
    onTertiary = StudioTextWhite,
    background = StudioObsidianDark,
    onBackground = StudioTextWhite,
    surface = StudioSurfaceDark,
    onSurface = StudioTextWhite,
    surfaceVariant = StudioCardDark,
    onSurfaceVariant = StudioTextMuted,
    outline = StudioBorderDark
)

private val StudioLightColorScheme = lightColorScheme(
    primary = StudioVioletDeep,
    onPrimary = StudioTextWhite,
    secondary = StudioCyanDark,
    onSecondary = StudioTextWhite,
    tertiary = StudioCoral,
    onTertiary = StudioTextWhite,
    background = StudioBackgroundLight,
    onBackground = StudioTextDark,
    surface = StudioSurfaceLight,
    onSurface = StudioTextDark,
    surfaceVariant = StudioCardLight,
    onSurfaceVariant = StudioTextSubdued,
    outline = StudioBorderLight
)

@Composable
fun LuminaTheme(
    darkTheme: Boolean = true, // Default to professional creative dark workspace
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) StudioDarkColorScheme else StudioLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    LuminaTheme(darkTheme = true, content = content)
}
