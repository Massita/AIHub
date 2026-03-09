package com.massita.aihub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF3054D3),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF0E8E7D),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF7F8FC),
    onBackground = Color(0xFF171B2A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF171B2A)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90A7FF),
    onPrimary = Color(0xFF0E1738),
    secondary = Color(0xFF6FE5D2),
    onSecondary = Color(0xFF032520),
    background = Color(0xFF10131C),
    onBackground = Color(0xFFE7EBF8),
    surface = Color(0xFF171B26),
    onSurface = Color(0xFFE7EBF8)
)

@Composable
fun AiHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
