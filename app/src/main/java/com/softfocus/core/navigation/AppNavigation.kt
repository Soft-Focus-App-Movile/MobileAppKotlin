package com.softfocus.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.softfocus.features.admin.presentation.di.AdminPresentationModule
import com.softfocus.features.admin.presentation.userlist.AdminUsersScreen
import com.softfocus.features.admin.presentation.verifypsychologist.VerifyPsychologistScreen
import com.softfocus.features.auth.presentation.accountreview.AccountReviewScreen
import com.softfocus.features.auth.presentation.di.PresentationModule
import com.softfocus.features.auth.presentation.login.LoginScreen
import com.softfocus.features.auth.presentation.register.RegisterScreen
import com.softfocus.features.auth.presentation.splash.SplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.ui.components.navigation.GeneralBottomNav
import com.softfocus.core.ui.components.navigation.PatientBottomNav
import com.softfocus.core.ui.components.navigation.PsychologistBottomNav
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.home.presentation.HomeScreen
import com.softfocus.features.home.presentation.general.GeneralHomeScreen
import com.softfocus.features.home.presentation.patient.PatientHomeScreen
import com.softfocus.features.home.presentation.psychologist.PsychologistHomeScreen
import com.softfocus.features.profile.presentation.general.GeneralProfileScreen
import com.softfocus.features.therapy.presentation.connect.ConnectPsychologistScreen
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.features.psychologist.presentation.di.PsychologistPresentationModule
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
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Login.path) { inclusive = true }
                    }
                },
                onAdminLoginSuccess = {
                    navController.navigate(Route.AdminUsers.path) {
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
                },
                onNavigateToPendingVerification = {
                    navController.navigate(Route.AccountReview.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                }
            )
        }

        // Account Review Screen (para psicólogos pendientes de verificación)
        composable(Route.AccountReview.path) {
            AccountReviewScreen()
        }

        composable(Route.Home.path) {
            val userSession = remember { UserSession(context) }
            val currentUser = userSession.getUser()

            when (currentUser?.userType) {
                UserType.PSYCHOLOGIST -> {
                    val psychologistHomeViewModel = remember {
                        PsychologistPresentationModule.getPsychologistHomeViewModel(context)
                    }
                    Scaffold(
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
                                    PatientHomeScreen()
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
                            }
                        )
                    }
                }
            }
        }

        composable(Route.ConnectPsychologist.path) {
            val connectViewModel = TherapyPresentationModule.getConnectPsychologistViewModel(context)
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

        composable(Route.AdminUsers.path) {
            val viewModel = AdminPresentationModule.getAdminUsersViewModel(context)
            AdminUsersScreen(
                viewModel = viewModel,
                onNavigateToVerify = { userId ->
                    navController.navigate("${Route.VerifyPsychologist.path}/$userId")
                },
                onLogout = {
                    AdminPresentationModule.setAuthToken("")
                    navController.navigate(Route.Login.path) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${Route.VerifyPsychologist.path}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val viewModel = androidx.compose.runtime.remember {
                AdminPresentationModule.getVerifyPsychologistViewModel(context)
            }
            VerifyPsychologistScreen(
                userId = userId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
