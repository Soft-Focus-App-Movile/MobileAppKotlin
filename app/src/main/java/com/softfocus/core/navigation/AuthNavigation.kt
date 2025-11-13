package com.softfocus.core.navigation

import android.content.Context
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.softfocus.core.permissions.shouldShowPermissions
import com.softfocus.features.auth.presentation.accountreview.AccountReviewScreen
import com.softfocus.features.auth.presentation.di.PresentationModule
import com.softfocus.features.auth.presentation.forgotpassword.ForgotPasswordScreen
import com.softfocus.features.auth.presentation.login.LoginScreen
import com.softfocus.features.auth.presentation.register.RegisterScreen
import com.softfocus.features.auth.presentation.splash.SplashScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Authentication navigation graph.
 * Contains all pre-login routes: Splash, Login, Register, AccountReview
 */
fun NavGraphBuilder.authNavigation(
    navController: NavHostController,
    context: Context
) {
    // Splash Screen
    composable(Route.Splash.path) {
        SplashScreen(
            onNavigateToLogin = {
                navController.navigate(Route.Login.path) {
                    popUpTo(Route.Splash.path) { inclusive = true }
                }
            },
            onNavigateToHome = {
                val destination = if (shouldShowPermissions(context)) {
                    Route.Permissions.path
                } else {
                    Route.Home.path
                }
                navController.navigate(destination) {
                    popUpTo(Route.Splash.path) { inclusive = true }
                }
            },
            onNavigateToAdmin = {
                navController.navigate(Route.AdminUsers.path) {
                    popUpTo(Route.Splash.path) { inclusive = true }
                }
            }
        )
    }

    // Login Screen
    composable(Route.Login.path) {
        val viewModel = PresentationModule.getLoginViewModel(context)
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = {
                val destination = if (shouldShowPermissions(context)) {
                    Route.Permissions.path
                } else {
                    Route.Home.path
                }
                navController.navigate(destination) {
                    popUpTo(Route.Login.path) { inclusive = true }
                }
            },
            onAdminLoginSuccess = {
                // Navigate to Splash first to let AppNavigation detect user change
                // and register admin routes before navigating to AdminUsers
                navController.navigate(Route.Splash.path) {
                    popUpTo(Route.Login.path) { inclusive = true }
                }
            },
            onNavigateToRegister = {
                navController.navigate(Route.Register.path)
            },
            onNavigateToRegisterWithOAuth = { email, fullName, tempToken ->
                val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                val encodedFullName = URLEncoder.encode(fullName, StandardCharsets.UTF_8.toString())
                val encodedTempToken = URLEncoder.encode(tempToken, StandardCharsets.UTF_8.toString())
                navController.navigate("${Route.Register.path}/$encodedEmail/$encodedFullName/$encodedTempToken")
            },
            onNavigateToPendingVerification = {
                navController.navigate(Route.AccountReview.path) {
                    popUpTo(Route.Login.path) { inclusive = true }
                }
            },
            onNavigateToForgotPassword = {
                navController.navigate(Route.ForgotPassword.path)
            }
        )
    }

    // Forgot Password Screen
    composable(Route.ForgotPassword.path) {
        val viewModel = PresentationModule.getForgotPasswordViewModel(context)
        ForgotPasswordScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }

    // Register Screen (normal)
    composable(Route.Register.path) {
        val viewModel = PresentationModule.getRegisterViewModel(context)
        RegisterScreen(
            viewModel = viewModel,
            oauthEmail = null,
            oauthFullName = null,
            oauthTempToken = null,
            onRegisterSuccess = { userType ->
                if (userType == com.softfocus.features.auth.domain.models.UserType.PSYCHOLOGIST) {
                    navController.navigate(Route.AccountReview.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                } else {
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                }
            },
            onAutoLogin = { user ->
                if (user.userType == com.softfocus.features.auth.domain.models.UserType.PSYCHOLOGIST && !user.isVerified) {
                    navController.navigate(Route.AccountReview.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                } else {
                    val destination = if (shouldShowPermissions(context)) {
                        Route.Permissions.path
                    } else {
                        Route.Home.path
                    }
                    navController.navigate(destination) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                }
            },
            onNavigateToLogin = {
                navController.popBackStack()
            },
            onNavigateToPendingVerification = {
                navController.navigate(Route.AccountReview.path) {
                    popUpTo(Route.Register.path) { inclusive = true }
                }
            }
        )
    }

    // Register Screen (with OAuth data)
    composable(
        route = "${Route.Register.path}/{email}/{fullName}/{tempToken}",
        arguments = listOf(
            navArgument("email") { type = NavType.StringType },
            navArgument("fullName") { type = NavType.StringType },
            navArgument("tempToken") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val viewModel = PresentationModule.getRegisterViewModel(context)
        val email = URLDecoder.decode(backStackEntry.arguments?.getString("email") ?: "", StandardCharsets.UTF_8.toString())
        val fullName = URLDecoder.decode(backStackEntry.arguments?.getString("fullName") ?: "", StandardCharsets.UTF_8.toString())
        val tempToken = URLDecoder.decode(backStackEntry.arguments?.getString("tempToken") ?: "", StandardCharsets.UTF_8.toString())

        RegisterScreen(
            viewModel = viewModel,
            oauthEmail = email,
            oauthFullName = fullName,
            oauthTempToken = tempToken,
            onRegisterSuccess = { userType ->
                if (userType == com.softfocus.features.auth.domain.models.UserType.PSYCHOLOGIST) {
                    navController.navigate(Route.AccountReview.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                } else {
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                }
            },
            onAutoLogin = { user ->
                if (user.userType == com.softfocus.features.auth.domain.models.UserType.PSYCHOLOGIST && !user.isVerified) {
                    navController.navigate(Route.AccountReview.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                } else {
                    val destination = if (shouldShowPermissions(context)) {
                        Route.Permissions.path
                    } else {
                        Route.Home.path
                    }
                    navController.navigate(destination) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                }
            },
            onNavigateToLogin = {
                navController.popBackStack()
            },
            onNavigateToPendingVerification = {
                navController.navigate(Route.AccountReview.path) {
                    popUpTo(Route.Register.path) { inclusive = true }
                }
            }
        )
    }

    // Account Review Screen (for pending psychologists)
    composable(Route.AccountReview.path) {
        AccountReviewScreen()
    }
}
