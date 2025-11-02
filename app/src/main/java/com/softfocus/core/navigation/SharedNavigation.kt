package com.softfocus.core.navigation

import android.content.Context
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
import com.softfocus.features.ai.presentation.chat.AIChatScreen
import com.softfocus.features.ai.presentation.welcome.AIWelcomeScreen
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.home.presentation.general.GeneralHomeScreen
import com.softfocus.features.home.presentation.patient.PatientHomeScreen
import com.softfocus.features.home.presentation.psychologist.PsychologistHomeScreen
import com.softfocus.features.notifications.presentation.di.NotificationPresentationModule
import com.softfocus.features.notifications.presentation.list.NotificationsScreen
import com.softfocus.features.notifications.presentation.preferences.NotificationPreferencesScreen
import com.softfocus.features.profile.presentation.general.GeneralProfileScreen
import com.softfocus.features.psychologist.presentation.di.PsychologistPresentationModule
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.ui.components.navigation.GeneralBottomNav
import com.softfocus.ui.components.navigation.PatientBottomNav
import com.softfocus.ui.components.navigation.PsychologistBottomNav
import com.softfocus.core.utils.SessionManager
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * Shared navigation graph.
 * Contains routes shared by General, Patient, and Psychologist users (post-login):
 * - Home (with different screens per user type)
 * - Profile
 * - Notifications
 * - AI Chat
 */
fun NavGraphBuilder.sharedNavigation(
    navController: NavHostController,
    context: Context
) {
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
                        PsychologistHomeScreen(psychologistHomeViewModel)
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
                                PatientHomeScreen(navController)
                            } else {
                                GeneralHomeScreen()
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

    // Profile Screen
    composable(Route.Profile.path) {
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
                    GeneralProfileScreen(
                        onNavigateToConnect = {
                            navController.navigate(Route.ConnectPsychologist.path)
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
    }

    // Notifications Screen
    composable(Route.Notifications.path) {
        val viewModel = remember {
            NotificationPresentationModule.getNotificationsViewModel(context)
        }
        NotificationsScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToSettings = {
                navController.navigate(Route.NotificationPreferences.path)
            }
        )
    }

    // Notification Preferences Screen
    composable(Route.NotificationPreferences.path) {
        val viewModel = remember {
            NotificationPresentationModule.getNotificationPreferencesViewModel(context)
        }
        NotificationPreferencesScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navController.popBackStack()
            }
        )
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
                        }
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
                }
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
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        } else {
            // Patients get X button without bottom nav
            AIChatScreen(
                initialMessage = decodedMessage,
                sessionId = if (sessionId != "null") sessionId else null,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
