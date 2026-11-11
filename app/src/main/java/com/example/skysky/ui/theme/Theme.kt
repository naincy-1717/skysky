package com.example.skysky.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dark Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = Color(0xFF03DAC5),
    background = Color(0xFF0B132B), // Deep midnight navy for night mode
    surface = Color(0xFF1C2541), // Dark slate blue for cards
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

// Light Theme Colors (Your existing pastel theme)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF000080), // Navy Blue
    secondary = Color(0xFF03DAC5),
    background = Color(0xFFBBE2ED), // Pastel Blue
    surface = Color(0xFFFFFFFF), // White for cards
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF2C3E50), // Navy text on light background
    onSurface = Color(0xFF2C3E50) // Navy text on white cards
)

@Composable
fun SkySkyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Automatically detects phone settings
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false to force our custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}