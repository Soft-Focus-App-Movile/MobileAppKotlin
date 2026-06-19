package com.softfocus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.softfocus.core.navigation.AppNavigation
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.ui.theme.SoftFocusMobileTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the manual DI module before any composition uses it (e.g. the call hub).
        TherapyPresentationModule.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            SoftFocusMobileTheme {
                AppNavigation()
            }
        }
    }
}