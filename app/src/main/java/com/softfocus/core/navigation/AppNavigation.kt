package com.softfocus.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.auth.domain.models.UserType
import kotlinx.coroutines.delay

/**
 * Main navigation orchestrator for the SoftFocus app.
 *
 * This composable delegates navigation to specialized navigation graphs based on user type:
 * - AuthNavigation: Pre-login routes (Splash, Login, Register, AccountReview)
 * - SharedNavigation: Post-login routes shared by all user types (Home, Profile, Notifications, AI)
 * - GeneralNavigation: Routes specific to General users
 * - PatientNavigation: Routes specific to Patient users
 * - PsychologistNavigation: Routes specific to Psychologist users
 * - AdminNavigation: Routes specific to Admin users
 *
 * Benefits of this modular approach:
 * - Improved maintainability: Each navigation graph is in its own file (~50-150 lines)
 * - Better scalability: Easy to add new routes without bloating a single file
 * - Clear separation of concerns: Each user type has dedicated navigation logic
 * - Type safety: Routes are only registered for authorized user types
 */
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }

    // Observable state for current user - updates when SharedPreferences change
    var currentUser by remember { mutableStateOf(userSession.getUser()) }

    // Poll for user changes every 500ms to detect login/logout
    // This is necessary because SharedPreferences doesn't trigger recomposition automatically
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            val newUser = userSession.getUser()
            if (newUser?.id != currentUser?.id || newUser?.userType != currentUser?.userType) {
                currentUser = newUser
            }
        }
    }

    val userTypeKey = currentUser?.userType?.name ?: "none"

    // Use key() to force NavHost to rebuild when user type changes
    // This ensures the correct navigation routes are registered after login/logout
    key(userTypeKey) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Route.Splash.path
        ) {
            // Authentication routes (available to all, pre-login)
            authNavigation(navController, context)

            // Shared routes (available to General, Patient, Psychologist post-login)
            sharedNavigation(navController, context)

            // User-type specific routes
            when (currentUser?.userType) {
                UserType.GENERAL -> {
                    generalNavigation(navController, context)
                    // General users can become patients dynamically when assigned a psychologist
                    // so we need to register patient routes for them as well
                    patientNavigation(navController, context)
                }
                UserType.PATIENT -> {
                    generalNavigation(navController, context) // Patients also need general routes
                    patientNavigation(navController, context)
                }
                UserType.PSYCHOLOGIST -> {
                    psychologistNavigation(navController, context)
                }
                UserType.ADMIN -> {
                    adminNavigation(navController, context)
                }
                else -> {
                    // No additional routes for unauthenticated users
                }
            }
        }
    }
}
