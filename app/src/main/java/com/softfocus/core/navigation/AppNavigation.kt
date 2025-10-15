package com.softfocus.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.softfocus.features.auth.presentation.accountreview.AccountReviewScreen
import com.softfocus.features.auth.presentation.di.PresentationModule
import com.softfocus.features.auth.presentation.login.LoginScreen
import com.softfocus.features.auth.presentation.register.RegisterScreen
import com.softfocus.features.auth.presentation.splash.SplashScreen
import com.softfocus.features.home.presentation.HomeScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Route.Splash.path
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
                    navController.navigate(Route.Login.path) {
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
                    // Navigate based on user type and verification status
                    val user = viewModel.user.value
                    if (user != null) {
                        if (user.userType == com.softfocus.features.auth.domain.models.UserType.PSYCHOLOGIST && !user.isVerified) {
                            // Psychologists not verified go to review screen
                            navController.navigate(Route.AccountReview.path) {
                                popUpTo(Route.Login.path) { inclusive = true }
                            }
                        } else {
                            // Verified users and general users go to home
                            navController.navigate(Route.Home.path) {
                                popUpTo(Route.Login.path) { inclusive = true }
                            }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Route.Register.path)
                },
                onNavigateToRegisterWithOAuth = { email, fullName, tempToken ->
                    // URL encode the parameters
                    val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                    val encodedFullName = URLEncoder.encode(fullName, StandardCharsets.UTF_8.toString())
                    val encodedTempToken = URLEncoder.encode(tempToken, StandardCharsets.UTF_8.toString())
                    navController.navigate("${Route.Register.path}/$encodedEmail/$encodedFullName/$encodedTempToken")
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
                    // Navigate based on user type after registration
                    if (userType == com.softfocus.features.auth.domain.models.UserType.PSYCHOLOGIST) {
                        // Psychologists go to account review screen
                        navController.navigate(Route.AccountReview.path) {
                            popUpTo(Route.Register.path) { inclusive = true }
                        }
                    } else {
                        // General users need to login to get home
                        navController.navigate(Route.Login.path) {
                            popUpTo(Route.Register.path) { inclusive = true }
                        }
                    }
                },
                onAutoLogin = { user ->
                    // OAuth auto-login: Navigate based on user type and verification status
                    if (user.userType == com.softfocus.features.auth.domain.models.UserType.PSYCHOLOGIST && !user.isVerified) {
                        // Psychologists not verified go to review screen
                        navController.navigate(Route.AccountReview.path) {
                            popUpTo(Route.Register.path) { inclusive = true }
                        }
                    } else {
                        // Verified users and general users go to home
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Register.path) { inclusive = true }
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
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
                    // Navigate based on user type after registration
                    if (userType == com.softfocus.features.auth.domain.models.UserType.PSYCHOLOGIST) {
                        // Psychologists go to account review screen
                        navController.navigate(Route.AccountReview.path) {
                            popUpTo(Route.Register.path) { inclusive = true }
                        }
                    } else {
                        // General users need to login to get home
                        navController.navigate(Route.Login.path) {
                            popUpTo(Route.Register.path) { inclusive = true }
                        }
                    }
                },
                onAutoLogin = { user ->
                    // OAuth auto-login: Navigate based on user type and verification status
                    if (user.userType == com.softfocus.features.auth.domain.models.UserType.PSYCHOLOGIST && !user.isVerified) {
                        // Psychologists not verified go to review screen
                        navController.navigate(Route.AccountReview.path) {
                            popUpTo(Route.Register.path) { inclusive = true }
                        }
                    } else {
                        // Verified users and general users go to home
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Register.path) { inclusive = true }
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Account Review Screen (para psicólogos pendientes de verificación)
        composable(Route.AccountReview.path) {
            AccountReviewScreen()
        }

        // Home Screen
        composable(Route.Home.path) {
            HomeScreen()
        }
    }
}
