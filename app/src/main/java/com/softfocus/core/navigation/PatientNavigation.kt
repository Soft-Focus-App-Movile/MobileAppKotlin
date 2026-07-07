package com.softfocus.core.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.softfocus.features.profile.presentation.patient.PatientProfileScreen
import com.softfocus.features.profile.presentation.edit.EditProfileScreen
import com.softfocus.ui.components.navigation.PatientBottomNav
import com.softfocus.ui.components.navigation.GeneralBottomNav
import com.softfocus.core.utils.SessionManager
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.features.therapy.presentation.patient.PsychologistChatScreen
import com.softfocus.features.therapy.presentation.patient.psychologistprofile.PsyChatProfileScreen
import com.softfocus.features.home.presentation.patient.PatientTasksScreen
import com.softfocus.ui.components.navigation.PsychologistBottomNav


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
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = { PatientBottomNav(navController) }
        ) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                PatientProfileScreen(
                    onNavigateToConnect = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Home.path) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToEditProfile = {
                        navController.navigate(Route.EditProfile.path)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Route.NotificationPreferences.path)
                    },
                    onNavigateToPrivacyPolicy = {
                        navController.navigate(Route.PrivacyPolicy.path)
                    },
                    onNavigateToHelpSupport = {
                        navController.navigate(Route.HelpSupport.path)
                    },
                    onNavigateToMyPlan = {
                        navController.navigate(Route.PatientPlan.path)
                    },
                    onLogout = {
                        SessionManager.logout(context)
                        navController.navigate(Route.Login.path) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    navController = navController
                )
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

    // Chat with Psychologist Screen

    composable(Route.PatientPsychologistChat.path) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { PatientBottomNav(navController) }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)){

                val patientPsychologistChatViewModel = remember {
                    TherapyPresentationModule.getPsychologistChatViewModel()
                }

                PsychologistChatScreen(
                    viewModel = patientPsychologistChatViewModel,
                    navController = navController
                )
            }
        }
    }

    // Chat Profile of Psychologist Screen

    composable(Route.PsychologistChatProfile.path) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { PatientBottomNav(navController) }
        ){ paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)){

                val psyChatProfileViewModel = remember {
                    TherapyPresentationModule.getPsyChatProfileViewModel()
                }

                PsyChatProfileScreen(
                    onBackClicked = { navController.popBackStack() },
                    onUnlinkClick = {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Home.path) { inclusive = true }
                        }
                    },
                    viewModel = psyChatProfileViewModel,
                    context = context
                )
            }
        }
    }

    // Patient Tasks Screen (mismas cards con las que el psicólogo ve las tareas de sus pacientes)
    composable(Route.PatientTasks.path) {
        PatientTasksScreen(
            onNavigateBack = { navController.popBackStack() },
            navController = navController
        )
    }

    // Library Screen (shared route; el nav depende de si el usuario tiene psicólogo)
    composable(Route.Library.path) {
        val homeViewModel = remember { TherapyPresentationModule.getHomeViewModel(context) }
        val isPatient = homeViewModel.isPatient.collectAsState()
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = {
                if (isPatient.value) {
                    PatientBottomNav(navController)
                } else {
                    GeneralBottomNav(navController)
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                com.softfocus.features.library.presentation.general.browse.GeneralLibraryScreen(
                    onContentClick = { content ->
                        when (content.type) {
                            com.softfocus.features.library.domain.models.ContentType.Movie -> {
                                navController.navigate(Route.LibraryGeneralDetail.createRoute(content.id))
                            }
                            com.softfocus.features.library.domain.models.ContentType.Music -> {
                                val spotifyUrl = content.spotifyUrl
                                if (!spotifyUrl.isNullOrBlank()) {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUrl))
                                        context.startActivity(intent)
                                    } catch (e: ActivityNotFoundException) {
                                        try {
                                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUrl))
                                            context.startActivity(webIntent)
                                        } catch (ex: Exception) {
                                            Toast.makeText(context, "No se pudo abrir Spotify", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Esta canción no tiene enlace de Spotify", Toast.LENGTH_SHORT).show()
                                }
                            }
                            com.softfocus.features.library.domain.models.ContentType.Video -> {
                                val youtubeUrl = content.youtubeUrl
                                if (!youtubeUrl.isNullOrBlank()) {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
                                        context.startActivity(intent)
                                    } catch (e: ActivityNotFoundException) {
                                        try {
                                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
                                            context.startActivity(webIntent)
                                        } catch (ex: Exception) {
                                            Toast.makeText(context, "No se pudo abrir YouTube", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Este video no tiene enlace de YouTube", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    composable(Route.PatientPlan.path) {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = { PatientBottomNav(navController) }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                com.softfocus.features.subscription.presentation.PatientPlanScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
