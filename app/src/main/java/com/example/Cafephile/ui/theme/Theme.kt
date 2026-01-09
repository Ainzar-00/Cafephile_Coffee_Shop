package com.example.f053.ui.theme

import android.app.Activity
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
    primary = Color(0xFF4A2C20),        // CoffeeDark
    onPrimary = Color.White,
    secondary = Color(0xFF8D6E63),      // CoffeeMedium
    tertiary = Color(0xFFD7CCC8),       // CoffeeAccent
    background = Color(0xFFF9F4EF),     // BackgroundCream
    surface = Color(0xFF8D6E63),
    onSecondary = Color(0xFFF9F4EF),
    onBackground = Color(0xFF4A2C20),
    onSurface = Color(0xFFF9F4EF)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A2C20),      // CoffeeDark
    onPrimary = Color.White,
    secondary = Color(0xFFD7CCC8),    // CoffeeAccent
    tertiary = Color(0xFFD4A574),     // Gold
    background = Color(0xFFF9F4EF),   // BackgroundCream
    surface = Color(0xFF8D6E63),      // CoffeeMedium
    onSecondary = Color(0xFF4A2C20),  // CoffeeDark
    onBackground = Color(0xFF4A2C20), // CoffeeDark
    onSurface = Color(0xFF4A2C20)     // CoffeeDark
)

@Composable
fun F053Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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