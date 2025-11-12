package com.softfocus.core.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
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
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.permissions.PermissionsScreen
import com.softfocus.core.permissions.shouldShowPermissions
import com.softfocus.features.ai.presentation.chat.AIChatScreen
import com.softfocus.features.ai.presentation.welcome.AIWelcomeScreen
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.home.presentation.general.GeneralHomeScreen
import com.softfocus.features.home.presentation.patient.PatientHomeScreen
import com.softfocus.features.home.presentation.psychologist.PsychologistHomeScreen
import com.softfocus.features.notifications.presentation.list.NotificationsScreen
import com.softfocus.features.notifications.presentation.preferences.NotificationPreferencesScreen
import com.softfocus.features.psychologist.presentation.di.PsychologistPresentationModule
import com.softfocus.features.search.presentation.detail.PsychologistDetailScreen
import com.softfocus.features.search.presentation.search.SearchPsychologistScreen
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.features.therapy.domain.models.PatientDirectory
import com.softfocus.features.tracking.presentation.screens.CheckInFormScreen
import com.softfocus.features.tracking.presentation.screens.DiaryScreen
import com.softfocus.features.tracking.presentation.screens.ProgressScreen
import com.softfocus.ui.components.navigation.GeneralBottomNav
import com.softfocus.ui.components.navigation.PatientBottomNav
import com.softfocus.ui.components.navigation.PsychologistBottomNav
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


/**
 * Shared navigation graph.
 * Contains routes shared by General, Patient, and Psychologist users (post-login):
 * - Home (with different screens per user type)
 * - Notifications
 * - AI Chat
 * - Tracking (Diary, Check-ins, Progress)
 */
fun NavGraphBuilder.sharedNavigation(
    navController: NavHostController,
    context: Context
) {
    composable(Route.Permissions.path) {
        PermissionsScreen(
            onPermissionsGranted = {
                navController.navigate(Route.Home.path) {
                    popUpTo(Route.Permissions.path) { inclusive = true }
                }
            }
        )
    }

    // Home Screen (different content per user type)
    composable(Route.Home.path) {
        val userSession = remember { UserSession(context) }
        val currentUser = userSession.getUser()

        when (currentUser?.userType) {
            UserType.PSYCHOLOGIST -> {
                val psychologistHomeViewModel = remember {
                    PsychologistPresentationModule.getPsychologistHomeViewModel(context)
                }
                Scaffold(
                    containerColor = Color.Transparent,
                    bottomBar = { PsychologistBottomNav(navController) }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        PsychologistHomeScreen(
                            psychologistHomeViewModel,
                            onNavigateToNotifications = {
                                navController.navigate(Route.Notifications.path)
                            },
                            onNavigateToPatientList = {
                                navController.navigate(Route.PsychologistPatientList.path)
                            },
                            onNavigateToPatientDetail = { patient ->
                                navController.navigate(
                                    Route.PsychologistPatientDetail.createRoute(
                                        patientId = patient.patientId,
                                        relationshipId = patient.id,
                                        startDate = patient.startDate
                                    )
                                )
                            }
                        )
                    }
                }
            }
            UserType.GENERAL, UserType.PATIENT -> {
                val homeViewModel = remember { TherapyPresentationModule.getHomeViewModel(context) }
                val isPatient = homeViewModel.isPatient.collectAsState()
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
                        bottomBar = {
                            if (isPatient.value) {
                                PatientBottomNav(navController)
                            } else {
                                GeneralBottomNav(navController)
                            }
                        }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            if (isPatient.value) {
                                PatientHomeScreen(
                                    navController,
                                    onNavigateToNotifications = {
                                        navController.navigate(Route.Notifications.path)
                                    }
                                )
                            } else {
                                GeneralHomeScreen(
                                    onNavigateToNotifications = {
                                        navController.navigate(Route.Notifications.path)
                                    },
                                    onNavigateToLibrary = {
                                        navController.navigate(Route.LibraryGeneralBrowse.path)
                                    },
                                    onNavigateToContentDetail = { contentId ->
                                        navController.navigate(Route.LibraryGeneralDetail.createRoute(contentId))
                                    },
                                    onNavigateToSearchPsychologist = {
                                        navController.navigate(Route.SearchPsychologist.path)
                                    },
                                    onNavigateToAIChat = {
                                        navController.navigate(Route.AIWelcome.path)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                GeneralHomeScreen()
            }
        }
    }


    // Notifications Screen
    composable(Route.Notifications.path) {
        val userSession = remember { UserSession(context) }
        val currentUser = userSession.getUser()
        val homeViewModel = remember { TherapyPresentationModule.getHomeViewModel(context) }
        val isPatient = homeViewModel.isPatient.collectAsState()
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
                bottomBar = {
                    when (currentUser?.userType) {
                        UserType.PSYCHOLOGIST -> PsychologistBottomNav(navController)
                        UserType.GENERAL, UserType.PATIENT -> {
                            if (isPatient.value) {
                                PatientBottomNav(navController)
                            } else {
                                GeneralBottomNav(navController)
                            }
                        }
                        else -> GeneralBottomNav(navController)
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    NotificationsScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }

    // Notification Preferences Screen
    composable(Route.NotificationPreferences.path) {
        val userSession = remember { UserSession(context) }
        val currentUser = userSession.getUser()
        val homeViewModel = remember { TherapyPresentationModule.getHomeViewModel(context) }
        val isPatient = homeViewModel.isPatient.collectAsState()
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
                bottomBar = {
                    when (currentUser?.userType) {
                        UserType.PSYCHOLOGIST -> PsychologistBottomNav(navController)
                        UserType.GENERAL, UserType.PATIENT -> {
                            if (isPatient.value) {
                                PatientBottomNav(navController)
                            } else {
                                GeneralBottomNav(navController)
                            }
                        }
                        else -> GeneralBottomNav(navController)
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    NotificationPreferencesScreen(
                        userType = currentUser?.userType ?: UserType.GENERAL,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }

    // AI Welcome Screen
    composable(Route.AIWelcome.path) {
        val homeViewModel = remember { TherapyPresentationModule.getHomeViewModel(context) }
        val isPatient = homeViewModel.isPatient.collectAsState()

        // Only General users (without psychologist) get bottom nav, Patients get X button
        if (!isPatient.value) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = { GeneralBottomNav(navController) }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    AIWelcomeScreen(
                        onSendMessage = { message ->
                            navController.navigate(Route.AIChat.createRoute(message))
                        },
                        onClose = { navController.popBackStack() },
                        onSessionClick = { sessionId ->
                            navController.navigate(Route.AIChat.createRoute(sessionId = sessionId))
                        },
                        onNavigateToEmotionDetection = { navController.navigate(Route.EmotionDetection.path) }
                    )
                }
            }
        } else {
            // Patients get X button without bottom nav
            AIWelcomeScreen(
                onSendMessage = { message ->
                    navController.navigate(Route.AIChat.createRoute(message))
                },
                onClose = { navController.popBackStack() },
                onSessionClick = { sessionId ->
                    navController.navigate(Route.AIChat.createRoute(sessionId = sessionId))
                },
                onNavigateToEmotionDetection = { navController.navigate(Route.EmotionDetection.path) }
            )
        }
    }

    // AI Chat Screen
    composable(
        route = Route.AIChat.path,
        arguments = listOf(
            navArgument("initialMessage") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("sessionId") {
                type = NavType.StringType
                nullable = true
            }
        )
    ) { backStackEntry ->
        val initialMessage = backStackEntry.arguments?.getString("initialMessage")
        val sessionId = backStackEntry.arguments?.getString("sessionId")
        val homeViewModel = remember { TherapyPresentationModule.getHomeViewModel(context) }
        val isPatient = homeViewModel.isPatient.collectAsState()

        val decodedMessage = if (initialMessage != null && initialMessage != "null") {
            URLDecoder.decode(initialMessage, StandardCharsets.UTF_8.toString())
        } else null

        // Only General users (without psychologist) get bottom nav, Patients get X button
        if (!isPatient.value) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = { GeneralBottomNav(navController) }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    AIChatScreen(
                        initialMessage = decodedMessage,
                        sessionId = if (sessionId != "null") sessionId else null,
                        onBackClick = { navController.popBackStack() },
                        onNavigateToEmotionDetection = { navController.navigate(Route.EmotionDetection.path) }
                    )
                }
            }
        } else {
            // Patients get X button without bottom nav
            AIChatScreen(
                initialMessage = decodedMessage,
                sessionId = if (sessionId != "null") sessionId else null,
                onBackClick = { navController.popBackStack() },
                onNavigateToEmotionDetection = { navController.navigate(Route.EmotionDetection.path) }
            )
        }
    }

    // Search Psychologist Screen
    composable(Route.SearchPsychologist.path) {
        SearchPsychologistScreen(
            onNavigateBack = { navController.popBackStack() },
            onPsychologistClick = { psychologistId ->
                navController.navigate(Route.PsychologistDetail.createRoute(psychologistId))
            }
        )
    }

    // Psychologist Detail Screen
    composable(
        route = Route.PsychologistDetail.path,
        arguments = listOf(
            navArgument("psychologistId") { type = NavType.StringType }
        )
    ) {
        PsychologistDetailScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // ==================== TRACKING ROUTES ====================

    // Diary Screen (Calendar view)
    composable(Route.Diary.path) {
        val homeViewModel = remember { TherapyPresentationModule.getHomeViewModel(context) }
        val isPatient = homeViewModel.isPatient.collectAsState()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (isPatient.value) {
                    PatientBottomNav(navController)
                } else {
                    GeneralBottomNav(navController)
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                DiaryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCheckIn = { navController.navigate(Route.CheckInForm.path) },
                    onNavigateToProgress = { navController.navigate(Route.Progress.path) }
                )
            }
        }
    }

    // Check-in Form Screen (Multi-step form)
    composable(Route.CheckInForm.path) {
        CheckInFormScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToDiary = {
                // Primero limpia el stack del formulario
                navController.popBackStack()
                // Luego navega al diario
                navController.navigate(Route.Diary.path) {
                    // Opcional: limpia el back stack para evitar volver al formulario
                    popUpTo(Route.Home.path) { inclusive = false }
                }
            }
        )
    }

    // Progress Screen (Charts and statistics)
    composable(Route.Progress.path) {
        val homeViewModel = remember { TherapyPresentationModule.getHomeViewModel(context) }
        val isPatient = homeViewModel.isPatient.collectAsState()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (isPatient.value) {
                    PatientBottomNav(navController)
                } else {
                    GeneralBottomNav(navController)
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                ProgressScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }

    composable(
        route = Route.LibraryGeneralDetail.path,
        arguments = listOf(
            navArgument("contentId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
        val homeViewModel = remember { TherapyPresentationModule.getHomeViewModel(context) }
        val isPatient = homeViewModel.isPatient.collectAsState()
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
                bottomBar = {
                    if (isPatient.value) {
                        PatientBottomNav(navController)
                    } else {
                        GeneralBottomNav(navController)
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    com.softfocus.features.library.presentation.general.detail.ContentDetailScreen(
                        contentId = contentId,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onRelatedContentClick = { content ->
                            navController.navigate(Route.LibraryGeneralDetail.createRoute(content.id))
                        }
                    )
                }
            }
        }
    }

    composable(Route.LibraryGeneralBrowse.path) {
        val homeViewModel = remember { TherapyPresentationModule.getHomeViewModel(context) }
        val isPatient = homeViewModel.isPatient.collectAsState()
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
                bottomBar = {
                    if (isPatient.value) {
                        PatientBottomNav(navController)
                    } else {
                        GeneralBottomNav(navController)
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    com.softfocus.features.library.presentation.general.browse.GeneralLibraryScreen(
                        onContentClick = { content ->
                            when (content.type) {
                                com.softfocus.features.library.domain.models.ContentType.Music -> {
                                    val spotifyUrl = content.spotifyUrl
                                    Log.d("SharedNavigation", "Intentando abrir Spotify: URL=$spotifyUrl")

                                    if (!spotifyUrl.isNullOrBlank()) {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUrl))
                                            context.startActivity(intent)
                                            Log.d("SharedNavigation", "✅ Spotify abierto exitosamente")
                                        } catch (e: ActivityNotFoundException) {
                                            Log.w("SharedNavigation", "❌ Spotify no instalado, abriendo en navegador")
                                            try {
                                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUrl))
                                                context.startActivity(webIntent)
                                            } catch (ex: Exception) {
                                                Log.e("SharedNavigation", "❌ Error al abrir navegador: ${ex.message}")
                                                Toast.makeText(context, "No se pudo abrir Spotify", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Log.e("SharedNavigation", "❌ Error inesperado: ${e.message}", e)
                                            Toast.makeText(context, "Error al abrir Spotify: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Log.w("SharedNavigation", "⚠️ URL de Spotify vacía para: ${content.title}")
                                        Toast.makeText(context, "Esta canción no tiene enlace de Spotify", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                com.softfocus.features.library.domain.models.ContentType.Video -> {
                                    val youtubeUrl = content.youtubeUrl
                                    Log.d("SharedNavigation", "Intentando abrir YouTube: URL=$youtubeUrl")

                                    if (!youtubeUrl.isNullOrBlank()) {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
                                            context.startActivity(intent)
                                            Log.d("SharedNavigation", "✅ YouTube abierto exitosamente")
                                        } catch (e: ActivityNotFoundException) {
                                            Log.w("SharedNavigation", "❌ YouTube no instalado, abriendo en navegador")
                                            try {
                                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
                                                context.startActivity(webIntent)
                                            } catch (ex: Exception) {
                                                Log.e("SharedNavigation", "❌ Error al abrir navegador: ${ex.message}")
                                                Toast.makeText(context, "No se pudo abrir YouTube", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Log.e("SharedNavigation", "❌ Error inesperado: ${e.message}", e)
                                            Toast.makeText(context, "Error al abrir YouTube: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Log.w("SharedNavigation", "⚠️ URL de YouTube vacía para: ${content.title}")
                                        Toast.makeText(context, "Este video no tiene enlace de YouTube", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                else -> {
                                    Log.d("SharedNavigation", "Navegando a detalle: ${content.title}")
                                    navController.navigate(Route.LibraryGeneralDetail.createRoute(content.id))
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // Emotion Detection Screen
    composable(Route.EmotionDetection.path) {
        com.softfocus.features.ai.presentation.emotion.EmotionDetectionScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

}
