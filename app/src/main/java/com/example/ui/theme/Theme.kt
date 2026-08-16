package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MuseDarkColorScheme = darkColorScheme(
    primary = MuseCyan,
    onPrimary = Color(0xFF001F28),
    primaryContainer = Color(0xFF004D5E),
    onPrimaryContainer = Color(0xFFB8F0FF),

    secondary = MuseGreen,
    onSecondary = Color(0xFF002111),
    secondaryContainer = Color(0xFF00522E),
    onSecondaryContainer = Color(0xFF8CFABF),

    tertiary = MuseRed,
    onTertiary = Color(0xFF3E0008),
    tertiaryContainer = Color(0xFF680014),
    onTertiaryContainer = Color(0xFFFFD9DC),

    background = MuseDarkBg,
    onBackground = TextPrimary,

    surface = MuseDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = MuseDarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = MuseBorder,
    outlineVariant = Color(0xFF1E2B46)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = MuseDarkColorScheme,
        typography = Typography,
        content = content
    )
}

