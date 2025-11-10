package com.softfocus.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.softfocus.features.tracking.presentation.screens.*

const val DIARY_ROUTE = "diary"
const val CHECK_IN_FORM_ROUTE = "check_in_form"
const val PROGRESS_ROUTE = "progress"

fun NavGraphBuilder.trackingNavGraph(navController: NavController) {
    composable(DIARY_ROUTE) {
        DiaryScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToCheckIn = { navController.navigate(CHECK_IN_FORM_ROUTE) },
            onNavigateToProgress = { navController.navigate(PROGRESS_ROUTE) }
        )
    }

    composable(CHECK_IN_FORM_ROUTE) {
        CheckInFormScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(PROGRESS_ROUTE) {
        ProgressScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}