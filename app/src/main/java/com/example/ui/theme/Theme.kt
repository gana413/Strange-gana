package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MysticColorScheme =
  darkColorScheme(
    primary = MysticGold,
    onPrimary = MysticDarkBg,
    secondary = MysticOrange,
    onSecondary = MysticDarkBg,
    tertiary = TimeEmerald,
    onTertiary = MysticDarkBg,
    background = MysticDarkBg,
    onBackground = MysticTextPrimary,
    surface = MysticSurface,
    onSurface = MysticTextPrimary,
    surfaceVariant = MysticSurfaceBright,
    onSurfaceVariant = MysticTextSecondary
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = MysticColorScheme, typography = Typography, content = content)
}

