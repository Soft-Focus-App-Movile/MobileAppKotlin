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
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.softfocus.features.profile.presentation.patient.PatientProfileScreen
import com.softfocus.features.profile.presentation.edit.EditProfileScreen
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
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
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
                    onNavigateToNotifications = { // ← AGREGAR ESTAS 3 LÍNEAS
                        navController.navigate(Route.NotificationPreferences.path)
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

    // Edit Profile Screen (shared with General users)
    composable(Route.EditProfile.path) {
        EditProfileScreen(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }

    // Library Screen for Patient
    composable(Route.Library.path) {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = { PatientBottomNav(navController) }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                com.softfocus.features.library.presentation.general.browse.GeneralLibraryScreen(
                    onContentClick = { content ->
                        // Navegar a ContentDetailScreen para Movies y Places
                        // Abrir directamente Spotify/YouTube para Music y Videos
                        when (content.type) {
                            com.softfocus.features.library.domain.models.ContentType.Movie -> {
                                // Navegar a la pantalla de detalle
                                navController.navigate(Route.LibraryGeneralDetail.createRoute(content.id))
                            }
                            com.softfocus.features.library.domain.models.ContentType.Weather -> {
                                // Weather solo muestra información, no navega
                                Toast.makeText(context, "Información del clima en tu ubicación actual", Toast.LENGTH_SHORT).show()
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
}
