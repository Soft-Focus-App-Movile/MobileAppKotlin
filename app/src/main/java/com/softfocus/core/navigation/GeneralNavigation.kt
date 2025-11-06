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
import androidx.navigation.compose.composable
import com.softfocus.features.profile.presentation.general.GeneralProfileScreen
import com.softfocus.features.profile.presentation.general.ConnectPsychologistScreen
import com.softfocus.features.profile.presentation.edit.EditProfileScreen
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.ui.components.navigation.GeneralBottomNav
import com.softfocus.ui.components.navigation.PatientBottomNav
import com.softfocus.core.utils.SessionManager

/**
 * General user navigation graph.
 * Contains routes specific to GENERAL users (users without a psychologist).
 * - GeneralProfile (profile screen for general users)
 * - EditProfile (edit profile information)
 * - ConnectPsychologist (to connect with a psychologist and become a Patient)
 */
fun NavGraphBuilder.generalNavigation(
    navController: NavHostController,
    context: Context
) {
    // General Profile Screen
    composable(Route.GeneralProfile.path) {
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
                bottomBar = { GeneralBottomNav(navController) }
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

    // Edit Profile Screen (shared by General users)
    composable(Route.EditProfile.path) {
        EditProfileScreen(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }

    // Connect with Psychologist Screen
    composable(Route.ConnectPsychologist.path) {
        val connectViewModel = remember { TherapyPresentationModule.getConnectPsychologistViewModel(context) }
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
                    ConnectPsychologistScreen(
                        viewModel = connectViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onConnectionSuccess = {
                            homeViewModel.refreshPatientStatus()
                            navController.navigate(Route.Home.path) {
                                popUpTo(Route.Home.path) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }

    // Library Detail Screen for General users
    composable(
        route = Route.LibraryGeneralDetail.path,
        arguments = listOf(
            androidx.navigation.navArgument("contentId") {
                type = androidx.navigation.NavType.StringType
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

    // Library Browse Screen for General users
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
                            navController.navigate(Route.LibraryGeneralDetail.createRoute(content.id))
                        }
                    )
                }
            }
        }
    }

    // Future general-specific routes can be added here
    // Example:
    // composable(Route.GeneralDashboard.path) { ... }
}
