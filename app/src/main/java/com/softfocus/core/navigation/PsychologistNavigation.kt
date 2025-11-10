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
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.features.therapy.presentation.psychologist.patientlist.PatientListScreen


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

    composable(Route.PsychologistPatientList.path) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { PsychologistBottomNav(navController) }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {

                // 1. Aquí se "usa" la función del módulo para obtener el ViewModel
                val patientListViewModel = remember {
                    TherapyPresentationModule.getPatientListViewModel()
                }

                // 2. Se inyecta el ViewModel en la pantalla
                PatientListScreen(
                    viewModel = patientListViewModel,
                    onPatientClick = { patient ->
                        // 3. (Extra) Manejar la navegación al detalle del paciente
                        // Esta ruta ya existe en tu archivo Route.kt
                        navController.navigate(
                            Route.PsychologistPatientDetail.createRoute(
                                patientId = patient.patientId,
                                relationshipId = patient.id, // El ID del PatientDirectory es el RelationshipId
                                patientName = patient.patientName
                            )
                        )
                    }
                )
            }
        }
    }
    composable(Route.Library.path) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { PsychologistBottomNav(navController) }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                com.softfocus.features.library.presentation.general.browse.GeneralLibraryScreen(
                    onContentClick = { content ->
                        when (content.type) {
                            com.softfocus.features.library.domain.models.ContentType.Music -> {
                                val spotifyUrl = content.spotifyUrl
                                Log.d("PsychologistNavigation", "Intentando abrir Spotify: URL=$spotifyUrl")

                                if (!spotifyUrl.isNullOrBlank()) {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUrl))
                                        context.startActivity(intent)
                                        Log.d("PsychologistNavigation", "✅ Spotify abierto exitosamente")
                                    } catch (e: ActivityNotFoundException) {
                                        Log.w("PsychologistNavigation", "❌ Spotify no instalado, abriendo en navegador")
                                        try {
                                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUrl))
                                            context.startActivity(webIntent)
                                        } catch (ex: Exception) {
                                            Log.e("PsychologistNavigation", "❌ Error al abrir navegador: ${ex.message}")
                                            Toast.makeText(context, "No se pudo abrir Spotify", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("PsychologistNavigation", "❌ Error inesperado: ${e.message}", e)
                                        Toast.makeText(context, "Error al abrir Spotify: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Log.w("PsychologistNavigation", "⚠️ URL de Spotify vacía para: ${content.title}")
                                    Toast.makeText(context, "Esta canción no tiene enlace de Spotify", Toast.LENGTH_SHORT).show()
                                }
                            }

                            com.softfocus.features.library.domain.models.ContentType.Video -> {
                                val youtubeUrl = content.youtubeUrl
                                Log.d("PsychologistNavigation", "Intentando abrir YouTube: URL=$youtubeUrl")

                                if (!youtubeUrl.isNullOrBlank()) {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
                                        context.startActivity(intent)
                                        Log.d("PsychologistNavigation", "✅ YouTube abierto exitosamente")
                                    } catch (e: ActivityNotFoundException) {
                                        Log.w("PsychologistNavigation", "❌ YouTube no instalado, abriendo en navegador")
                                        try {
                                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
                                            context.startActivity(webIntent)
                                        } catch (ex: Exception) {
                                            Log.e("PsychologistNavigation", "❌ Error al abrir navegador: ${ex.message}")
                                            Toast.makeText(context, "No se pudo abrir YouTube", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("PsychologistNavigation", "❌ Error inesperado: ${e.message}", e)
                                        Toast.makeText(context, "Error al abrir YouTube: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Log.w("PsychologistNavigation", "⚠️ URL de YouTube vacía para: ${content.title}")
                                    Toast.makeText(context, "Este video no tiene enlace de YouTube", Toast.LENGTH_SHORT).show()
                                }
                            }

                            else -> {
                                Log.d("PsychologistNavigation", "Tipo de contenido no soportado: ${content.type}")
                                Toast.makeText(context, "Contenido no disponible para visualización", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
    }

}
