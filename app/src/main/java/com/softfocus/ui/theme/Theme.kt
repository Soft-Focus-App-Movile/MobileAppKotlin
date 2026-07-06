package com.softfocus.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = M3Primary,                    // Purple67
    onPrimary = M3OnPrimary,                // White
    primaryContainer = M3PrimaryContainer,  // PurpleEA
    onPrimaryContainer = M3OnPrimaryContainer, // Purple4F

    secondary = Purple67,
    onSecondary = White,
    secondaryContainer = M3SecondaryContainer,  // PurpleE8
    onSecondaryContainer = M3OnSecondaryContainer, // Purple4A

    tertiary = MainYellow,                  // YellowEC
    onTertiary = Black,
    tertiaryContainer = YellowEB,
    onTertiaryContainer = Purple1D,

    error = RedE6,
    onError = White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = White,
    onBackground = M3OnSurface,             // Purple1D

    surface = White,
    onSurface = M3OnSurface,                // Purple1D
    surfaceVariant = M3SurfaceContainerHigh, // PurpleEC
    onSurfaceVariant = M3OnSurfaceVariant,  // Purple49

    outline = M3OutlineVariant,             // PurpleCA
    outlineVariant = GrayE0,

    scrim = Black,
    inverseSurface = Purple1D,
    inverseOnSurface = White,
    inversePrimary = PurpleEA,

    surfaceTint = M3Primary,
)

// Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = PurpleEA,                     // Lighter purple for dark mode
    onPrimary = Purple4F,
    primaryContainer = Purple67,
    onPrimaryContainer = PurpleEA,

    secondary = PurpleE8,
    onSecondary = Purple4A,
    secondaryContainer = Purple4A,
    onSecondaryContainer = PurpleE8,

    tertiary = YellowE8,                    // Lighter yellow for dark mode
    onTertiary = Yellow7E,
    tertiaryContainer = Yellow7E,
    onTertiaryContainer = YellowEB,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Purple1D,                  // Dark background
    onBackground = White,

    surface = Purple1D,
    onSurface = White,
    surfaceVariant = Purple49,
    onSurfaceVariant = PurpleCA,

    outline = Gray808,
    outlineVariant = Purple49,

    scrim = Black,
    inverseSurface = GrayE6,
    inverseOnSurface = Purple1D,
    inversePrimary = Purple67,

    surfaceTint = PurpleEA,
)

@Composable
fun SoftFocusMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Deshabilitado por defecto para usar nuestra paleta personalizada
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

    // Our semantic, theme-aware palette (used by screens via AppColors).
    val softFocusColors = if (darkTheme) DarkSoftFocusColors else LightSoftFocusColors

    // Update system bars
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = softFocusColors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalSoftFocusColors provides softFocusColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography
        ) {
            // Adaptive background so screens that don't paint their own (e.g. login) follow the theme.
            // This is what makes text legible in dark mode instead of white-on-white.
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = softFocusColors.background
            ) {
                content()
            }
        }
    }
}
