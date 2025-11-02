package com.softfocus.core.navigation

import android.content.Context
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.softfocus.features.admin.presentation.di.AdminPresentationModule
import com.softfocus.features.admin.presentation.userlist.AdminUsersScreen
import com.softfocus.features.admin.presentation.verifypsychologist.VerifyPsychologistScreen
import com.softfocus.core.utils.SessionManager

/**
 * Admin navigation graph.
 * Contains routes specific to ADMIN users:
 * - User management
 * - Psychologist verification
 * - System administration
 */
fun NavGraphBuilder.adminNavigation(
    navController: NavHostController,
    context: Context
) {
    // Admin Users List Screen
    composable(Route.AdminUsers.path) {
        val viewModel = AdminPresentationModule.getAdminUsersViewModel(context)
        AdminUsersScreen(
            viewModel = viewModel,
            onNavigateToVerify = { userId ->
                navController.navigate("${Route.VerifyPsychologist.path}/$userId")
            },
            onLogout = {
                SessionManager.logout(context)
                navController.navigate(Route.Login.path) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }

    // Verify Psychologist Screen
    composable(
        route = "${Route.VerifyPsychologist.path}/{userId}",
        arguments = listOf(navArgument("userId") { type = NavType.StringType })
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId") ?: ""
        val viewModel = remember {
            AdminPresentationModule.getVerifyPsychologistViewModel(context)
        }
        VerifyPsychologistScreen(
            userId = userId,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // Future admin-specific routes can be added here
    // Example:
    // composable(Route.SystemSettings.path) { ... }
    // composable(Route.Analytics.path) { ... }
}
