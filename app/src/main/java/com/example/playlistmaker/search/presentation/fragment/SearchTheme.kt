package com.example.playlistmaker.search.presentation.fragment

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

private val LightSearchColors = lightColorScheme(
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1C1C),
    surface = Color(0xFFAEAFB4),
    onSurface = Color(0xFF000000),
    primary = Color(0xFF1A1B22),
    secondary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFE6E8EB),
    tertiary = Color(0xFF1A1B22),
    surfaceTint = Color(0xFFAEAFB4),
)

private val DarkSearchColors = darkColorScheme(
    background = Color(0xFF1A1B22),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    primary = Color(0xFFFFFFFF),
    secondary = Color(0xFF1A1B22),
    onPrimary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF1A1B22),
    surfaceTint = Color(0xFF1A1B22),
)

private val SearchTypography = Typography(
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
        fontSize = 16.sp
    )
)

@Composable
fun SearchTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkSearchColors else LightSearchColors,
        typography = SearchTypography,
        content = content
    )
}