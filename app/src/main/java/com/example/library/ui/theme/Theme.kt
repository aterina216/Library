package com.example.library.ui.theme

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.library.ui.components.circularReveal
import com.example.library.ui.theme.Typography
import kotlinx.coroutines.delay

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkAccent,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkOnPrimary,
    onSecondary = DarkOnBackground,
    onBackground = DarkOnBackground,
    onSurface = DarkOnBackground,

    surfaceVariant = DarkSilver,
    outline = Color(0xFF5A5670)
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightAccent,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightOnPrimary,
    onSecondary = LightOnBackground,
    onBackground = LightOnBackground,
    onSurface = LightOnBackground,

    // Опционально
    surfaceVariant = LightSilver,
    outline = Color(0xFFC7C2D6)
)

@Composable
fun LibraryTheme(
    themeMode: String,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isDarkTheme = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme() // Для "system" смотрим системную настройку
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDarkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        else -> {
            if (isDarkTheme) DarkColorScheme
            else LightColorScheme
        }
    }

    var isThemeRevealed by remember { mutableStateOf(true) }

    var previousTheme by remember { mutableStateOf<String?>(null) }

    val themeKey = remember(themeMode) {
        "theme_${themeMode}_${System.currentTimeMillis()}"
    }

    LaunchedEffect(themeKey) {
        /*Log.d("ANIMATION_DEBUG", "previousTheme: $previousTheme, themeMode: $themeMode")*/
        if (previousTheme != null && previousTheme != themeMode) {
            /*Log.d("ANIMATION_DEBUG", "Starting animation!")*/
            isThemeRevealed = false
            delay(500)
            isThemeRevealed = true
        }

        previousTheme = themeMode
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .circularReveal(
                isRevealed = isThemeRevealed,
                animationSpec = tween(durationMillis = 1000)
            )
    )
    {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }

    val Shapes = Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(24.dp)
    )

    val Typography = _root_ide_package_.androidx.compose.material3.Typography(
        displayLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        )
    )
}