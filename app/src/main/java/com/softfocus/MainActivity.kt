package com.softfocus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.softfocus.core.navigation.AppNavigation
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.ui.theme.SoftFocusMobileTheme
import com.softfocus.ui.theme.ThemeManager
import com.softfocus.ui.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the manual DI module before any composition uses it (e.g. the call hub).
        TherapyPresentationModule.init(applicationContext)
        // Load the user's saved theme preference (dark / light / follow-system).
        ThemeManager.load(applicationContext)
        enableEdgeToEdge()
        setContent {
            val darkTheme = when (ThemeManager.mode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            SoftFocusMobileTheme(darkTheme = darkTheme) {
                AppNavigation()
            }
        }
    }
}