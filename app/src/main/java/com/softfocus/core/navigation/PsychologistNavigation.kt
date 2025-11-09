package com.softfocus.core.navigation

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.compose.runtime.remember
import com.softfocus.features.profile.presentation.psychologist.PsychologistProfileScreen
import com.softfocus.features.profile.presentation.psychologist.EditPersonalInfoScreen
import com.softfocus.features.profile.presentation.psychologist.ProfessionalDataScreen
import com.softfocus.features.profile.presentation.psychologist.MyInvitationCodeScreen
import com.softfocus.features.psychologist.presentation.di.PsychologistPresentationModule
import com.softfocus.ui.components.navigation.PsychologistBottomNav
import com.softfocus.core.utils.SessionManager
import com.softfocus.features.crisis.presentation.psychologist.CrisisAlertsScreen
import com.softfocus.features.crisis.presentation.di.CrisisInjection


/**
 * Psychologist navigation graph.
 * Contains routes specific to PSYCHOLOGIST users.
 * - PsychologistProfile (main profile screen)
 * - PsychologistEditProfile (edit personal information)
 * - ProfessionalData (view professional data)
 * - InvitationCode (view and share invitation code)
 */
fun NavGraphBuilder.psychologistNavigation(
    navController: NavHostController,
    context: Context
) {

    // Future psychologist-specific routes will be added here
    // Example:
    // composable(Route.MyPatients.path) { ... }
    // composable(Route.PatientDetail.path) { ... }
    // composable(Route.SessionNotes.path) { ... }

    // Psychologist Profile Screen
    composable(Route.PsychologistProfile.path) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { PsychologistBottomNav(navController) }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                PsychologistProfileScreen(
                    onNavigateToEditProfile = {
                        navController.navigate(Route.PsychologistEditProfile.path)
                    },
                    onNavigateToInvitationCode = {
                        navController.navigate(Route.InvitationCode.path)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Route.NotificationPreferences.path)
                    },
                    onNavigateToPlan = {
                        navController.navigate(Route.PsychologistPlan.path)
                    },
                    onNavigateToStats = {
                        navController.navigate(Route.PsychologistStats.path)
                    },
                    onNavigateToProfessionalData = {
                        navController.navigate(Route.ProfessionalData.path)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
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

    // Psychologist Edit Profile Screen
    composable(Route.PsychologistEditProfile.path) {
        EditPersonalInfoScreen(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }

    // Professional Data Screen
    composable(Route.ProfessionalData.path) {
        ProfessionalDataScreen(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }


    // Invitation Code Screen
    composable(Route.InvitationCode.path) {
        val psychologistHomeViewModel = remember {
            PsychologistPresentationModule.getPsychologistHomeViewModel(context)
        }
        MyInvitationCodeScreen(
            onNavigateBack = {
                navController.popBackStack()
            },
            viewModel = psychologistHomeViewModel
        )
    }

    // Crisis Alerts Screen
    composable(Route.CrisisAlerts.path) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { PsychologistBottomNav(navController) }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                val crisisAlertsViewModel = remember {
                    CrisisInjection.getCrisisAlertsViewModel(context)
                }
                CrisisAlertsScreen(
                    viewModel = crisisAlertsViewModel,
                    onNavigateBack = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Home.path) { inclusive = true }
                        }
                    },
                    onViewPatientProfile = { patientId ->
                        // TODO: Navigate to patient profile
                    },
                    onSendMessage = { patientId ->
                        // TODO: Navigate to messaging
                    }
                )
            }
        }
    }

}
