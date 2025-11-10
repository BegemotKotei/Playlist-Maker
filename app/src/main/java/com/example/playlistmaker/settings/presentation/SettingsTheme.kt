package com.example.playlistmaker.settings.presentation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

private val LightSettingsColors = lightColorScheme(
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1C1C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1B22),
    onSecondary = Color(0xFF3772E7),
    secondary = Color(0xFF9FBBF3),
    primary = Color(0xFFE6E8EB),
    onPrimary = Color(0xFFAEAFB4),
    tertiary = Color(0xFFAEAFB4),
)

private val DarkSettingsColors = darkColorScheme(
    background = Color(0xFF1A1B22),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF3772E7),
    secondary = Color(0xFF9FBBF3),
    primary = Color(0xFFE6E8EB),
    onPrimary = Color(0xFFAEAFB4),
    tertiary = Color(0xFFFFFFFF),
)

private val SettingsTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
        fontSize = 16.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        fontSize = 22.sp
    )
)

@Composable
fun SettingsTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkSettingsColors else LightSettingsColors,
        typography = SettingsTypography,
        content = content
    )
}