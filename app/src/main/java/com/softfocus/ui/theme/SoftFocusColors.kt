package com.softfocus.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic, theme-aware colors for the app. Instead of hardcoding `White`, `Color.Black`,
 * `GreenF8`, etc. in every screen, screens should use `AppColors.background`, `AppColors.textPrimary`,
 * and so on. To tweak the light or dark look, change the values in [LightSoftFocusColors] /
 * [DarkSoftFocusColors] here — in one place.
 *
 * Usage in a Composable:
 *   val c = AppColors          // or directly AppColors.background
 *   Modifier.background(c.background)
 *   Text(text = name, color = c.textPrimary)
 */
@Immutable
data class SoftFocusColors(
    val isDark: Boolean,

    val background: Color,        // full-screen background
    val surface: Color,           // white cards / panels / bottom nav
    val cardNeutral: Color,       // neutral off-white cards (e.g. patient list)
    val surfaceVariant: Color,    // soft (greenish) cards, chips, highlighted areas
    val inputBackground: Color,   // text field / input backgrounds

    val textPrimary: Color,       // titles, names, main body text (was black/dark)
    val textSecondary: Color,     // subtitles, hints, metadata (was gray)
    val textOnSurface: Color,     // text placed on `surface`

    val accent: Color,            // brand green used for headings/icons
    val outline: Color,           // borders / dividers

    val chatBackground: Color,    // chat screen background
    val chatBubbleMine: Color,    // outgoing message bubble
    val chatBubbleOther: Color,   // incoming message bubble
    val chatBubbleText: Color     // text inside chat bubbles
)

/**
 * Light palette — values here are EXACTLY the app's original light colors, so light mode looks
 * identical. Only the dark palette below introduces new colors.
 */
val LightSoftFocusColors = SoftFocusColors(
    isDark = false,
    background = White,
    surface = White,
    cardNeutral = Color(0xFFF7F7F3), // original neutral card color
    surfaceVariant = GreenF2,        // soft light-green card
    inputBackground = White,
    textPrimary = Black,             // original primary text (Color.Black)
    textSecondary = Color(0xFF8B8B8B), // original gray subtitle
    textOnSurface = Black,
    accent = Green49,
    outline = GrayE0,
    chatBackground = GreenF8,
    chatBubbleMine = GreenE7,
    chatBubbleOther = White,
    chatBubbleText = Black
)

/** Dark palette — readable dark equivalents of the light roles. */
val DarkSoftFocusColors = SoftFocusColors(
    isDark = true,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    cardNeutral = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF26301A), // dark green-tinted card
    inputBackground = Color(0xFF2A2A2A),
    textPrimary = White,
    textSecondary = Color(0xFFB7B7B7),
    textOnSurface = White,
    accent = Color(0xFFA8C686),         // lighter green so it reads on dark
    outline = Color(0xFF3A3A3A),
    chatBackground = Color(0xFF14210A),
    chatBubbleMine = Color(0xFF3A4A2A),
    chatBubbleOther = Color(0xFF2A2A2A),
    chatBubbleText = White
)

val LocalSoftFocusColors = staticCompositionLocalOf { LightSoftFocusColors }

/** Convenience accessor: `AppColors.background`, `AppColors.textPrimary`, … inside Composables. */
val AppColors: SoftFocusColors
    @Composable
    @ReadOnlyComposable
    get() = LocalSoftFocusColors.current
