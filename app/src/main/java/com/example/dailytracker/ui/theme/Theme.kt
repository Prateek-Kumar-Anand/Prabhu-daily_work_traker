package com.example.dailytracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = PrimaryBlue,
    secondary = ActivityPurple,
    secondaryContainer = ActivityPurpleLight,
    tertiary = SaleGreen,
    tertiaryContainer = SaleGreenLight,
    error = SpendingRed,
    errorContainer = SpendingRedLight,
    background = BackgroundGray,
    surface = CardWhite,
    surfaceVariant = BackgroundGray,
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextGray,
    outline = DividerGray
)

@Composable
fun DailyTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // The design is deliberately light-themed to match the source dashboard;
    // dark mode falls back to the same light scheme for consistency.
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
