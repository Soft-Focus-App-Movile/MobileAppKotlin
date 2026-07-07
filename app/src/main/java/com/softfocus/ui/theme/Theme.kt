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

// Light Color Scheme — paleta verde de la app (antes usaba el morado por defecto de M3)
private val LightColorScheme = lightColorScheme(
    primary = Green49,                      // verde de marca
    onPrimary = White,
    primaryContainer = GreenEB2,            // verde claro
    onPrimaryContainer = Green29,           // verde oscuro

    secondary = Green65,
    onSecondary = White,
    secondaryContainer = GreenDD,
    onSecondaryContainer = Green29,

    tertiary = MainYellow,                  // YellowEC
    onTertiary = Black,
    tertiaryContainer = YellowEB,
    onTertiaryContainer = Green13,

    error = RedE6,
    onError = White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = White,
    onBackground = Gray222,                 // casi negro neutro (sin tinte morado)

    surface = White,
    onSurface = Gray222,
    surfaceVariant = GreenF2,               // verde-gris claro
    onSurfaceVariant = Green65,

    outline = GreenB8,                      // bordes/lineas verde-gris (antes morado)
    outlineVariant = GrayE0,

    scrim = Black,
    inverseSurface = Gray222,
    inverseOnSurface = White,
    inversePrimary = GreenA3,

    surfaceTint = Green49,
)

// Dark Color Scheme — verde para modo oscuro (antes morado)
private val DarkColorScheme = darkColorScheme(
    primary = GreenA3,                      // verde más claro para modo oscuro
    onPrimary = Green13,
    primaryContainer = Green37,
    onPrimaryContainer = GreenE7,

    secondary = GreenAB,
    onSecondary = Green13,
    secondaryContainer = Green37,
    onSecondaryContainer = GreenE7,

    tertiary = YellowE8,                    // Lighter yellow for dark mode
    onTertiary = Yellow7E,
    tertiaryContainer = Yellow7E,
    onTertiaryContainer = YellowEB,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF121212),         // fondo oscuro (igual que AppColors)
    onBackground = White,

    surface = Color(0xFF1E1E1E),
    onSurface = White,
    surfaceVariant = Green29,               // verde oscuro
    onSurfaceVariant = GreenB8,

    outline = Gray808,
    outlineVariant = Green37,

    scrim = Black,
    inverseSurface = GrayE6,
    inverseOnSurface = Gray222,
    inversePrimary = Green49,

    surfaceTint = GreenA3,
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
