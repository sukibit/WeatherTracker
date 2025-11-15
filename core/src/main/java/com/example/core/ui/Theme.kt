package com.example.core.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary80,
    onPrimary = Color(0xFF000000),
    primaryContainer = Primary40,
    onPrimaryContainer = Primary80,
    secondary = Secondary80,
    onSecondary = Color(0xFF000000),
    secondaryContainer = Secondary40,
    onSecondaryContainer = Secondary80,
    tertiary = Tertiary80,
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Tertiary40,
    onTertiaryContainer = Tertiary80,
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF000000),
    errorContainer = ErrorDark,
    onErrorContainer = Color(0xFFFFBBBB),
    background = Color(0xFF0F1419),
    onBackground = Neutral95,
    surface = Color(0xFF1A1F26),
    onSurface = Neutral95,
    surfaceVariant = Neutral30,
    onSurfaceVariant = Neutral80,
    outline = Neutral60,
    outlineVariant = Neutral30,
    scrim = Color(0xFF000000),
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral10,
    inversePrimary = Primary40
)

private val LightColorScheme = lightColorScheme(
    primary = Primary40,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Primary80,
    onPrimaryContainer = Primary20,
    secondary = Secondary40,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Secondary80,
    onSecondaryContainer = Secondary20,
    tertiary = Tertiary40,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Tertiary80,
    onTertiaryContainer = Tertiary20,
    error = Error,
    onError = Color(0xFFFFFFFF),
    errorContainer = ErrorLight,
    onErrorContainer = ErrorDark,
    background = Color(0xFFFAFBFC),
    onBackground = Neutral10,
    surface = Color(0xFFFFFFFF),
    onSurface = Neutral10,
    surfaceVariant = Neutral90,
    onSurfaceVariant = Neutral40,
    outline = Neutral50,
    outlineVariant = Neutral70,
    scrim = Color(0xFF000000),
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    inversePrimary = Primary80
)

@Composable
fun WeatherTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}