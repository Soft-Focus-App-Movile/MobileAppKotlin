package com.softfocus.core.navigation

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.softfocus.features.profile.presentation.patient.PatientProfileScreen
import com.softfocus.features.profile.presentation.edit.EditProfileScreen
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.ui.components.navigation.PatientBottomNav
import com.softfocus.core.utils.SessionManager


/**
 * Patient navigation graph.
 * Contains routes specific to PATIENT users (users with a psychologist assigned).
 * - PatientProfile (profile screen for patients)
 * - EditProfile (edit profile information)
 *
 * Future patient-specific features:
 * - Therapy sessions list
 * - My psychologist profile
 * - Assigned exercises
 * - Session notes
 * - Progress tracking
 */
fun NavGraphBuilder.patientNavigation(
    navController: NavHostController,
    context: Context
) {
    // Patient Profile Screen
    composable(Route.PatientProfile.path) {
        val homeViewModel = remember { TherapyPresentationModule.getHomeViewModel(context) }
        val isLoading = homeViewModel.isLoading.collectAsState()

        if (isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6B8E6F))
            }
        } else {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = { PatientBottomNav(navController) }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    PatientProfileScreen(
                        onNavigateToConnect = {
                            navController.navigate(Route.ConnectPsychologist.path)
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onNavigateToEditProfile = {
                            navController.navigate(Route.EditProfile.path)
                        },
                        onLogout = {
                            SessionManager.logout(context)
                            navController.navigate(Route.Login.path) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }

    // Edit Profile Screen (shared with General users)
    composable(Route.EditProfile.path) {
        EditProfileScreen(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }

    // Future patient-specific routes will be added here
    // Example:
    // composable(Route.TherapySessions.path) { ... }
    // composable(Route.MyPsychologist.path) { ... }
}
