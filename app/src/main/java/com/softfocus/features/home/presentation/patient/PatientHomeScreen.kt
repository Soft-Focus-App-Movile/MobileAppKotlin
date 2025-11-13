package com.softfocus.features.home.presentation.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.R
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.utils.LocationHelper
import com.softfocus.features.home.presentation.components.RecommendationsSection
import com.softfocus.features.home.presentation.components.TrackingHome
import com.softfocus.features.home.presentation.components.WelcomeCard
import com.softfocus.features.crisis.presentation.components.CrisisButton
import com.softfocus.features.home.presentation.patient.components.TasksSection
import com.softfocus.features.home.presentation.patient.components.TherapistChatCard
import com.softfocus.features.tracking.presentation.state.TrackingUiState
import com.softfocus.features.tracking.presentation.viewmodel.TrackingViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.softfocus.core.navigation.Route
import com.softfocus.ui.components.DraggableAIButton
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.features.home.presentation.patient.di.patientHomeViewModel
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.SourceSansRegular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHomeScreen(
    navController: NavController,
    onNavigateToNotifications: () -> Unit = {},
    viewModel: PatientHomeViewModel = patientHomeViewModel(),
    trackingViewModel: TrackingViewModel = hiltViewModel() // AGREGAR
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var locationText by remember { mutableStateOf("Lima, Peru") }

    val userSession = remember { UserSession(context) }
    val currentUser = remember { userSession.getUser() }
    val userName = remember {
        currentUser?.fullName?.split(" ")?.firstOrNull() ?: "Usuario"
    }

    val recommendationsState by viewModel.recommendationsState.collectAsState()
    val therapistState by viewModel.therapistState.collectAsState()
    val assignmentsState by viewModel.assignmentsState.collectAsState()

    // AGREGAR: Estados del tracking
    val trackingUiState by trackingViewModel.uiState.collectAsState()
    val dashboard = (trackingUiState as? TrackingUiState.Success)?.data?.dashboard

    LaunchedEffect(Unit) {
        trackingViewModel.refreshData() // CAMBIAR AQUÍ

        scope.launch {
            if (LocationHelper.hasLocationPermission(context)) {
                val location = LocationHelper.getCurrentLocation(context)
                locationText = if (location != null) {
                    LocationHelper.getCityAndCountry(context, location)
                } else {
                    "Lima, Peru"
                }
            }
        }
    }



    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_location_pin),
                                contentDescription = null,
                                tint = Color(0xFF497654),
                                modifier = Modifier.size(23.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = locationText,
                                style = SourceSansRegular,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    },
                    actions = {
                        CrisisButton()
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onNavigateToNotifications) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_notification_bell),
                                contentDescription = "Notificaciones",
                                tint = Color(0xFF497654),
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                WelcomeCard(
                    userName = userName,
                    onRegisterMoodClick = {
                        navController.navigate(Route.CheckInForm.path)
                    },
                    // AGREGAR: Pasar datos del dashboard
                    hasTodayCheckIn = dashboard?.summary?.hasTodayCheckIn ?: false,
                    todayEmotionalLevel = dashboard?.summary?.todayCheckIn?.emotionalLevel,
                    totalCheckIns = dashboard?.summary?.totalCheckIns ?: 0
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Chat con terapeuta",
                    style = CrimsonSemiBold,
                    fontSize = 20.sp,
                    color = Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TherapistChatCard(
                    therapistState = therapistState,
                    onRetry = { viewModel.retryTherapist() },
                    onChatClick = {
                        // TODO: Navegar al chat con el terapeuta
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Tareas pendientes",
                    style = CrimsonSemiBold,
                    fontSize = 20.sp,
                    color = Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TasksSection(
                    assignmentsState = assignmentsState,
                    onTaskClick = { assignment ->
                        navController.navigate(Route.LibraryGeneralDetail.createRoute(assignment.content.id))
                    },
                    onRetry = { viewModel.retryAssignments() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                RecommendationsSection(
                    recommendationsState = recommendationsState,
                    onNavigateToLibrary = {
                        navController.navigate(Route.LibraryGeneralBrowse.path)
                    },
                    onContentClick = { contentId ->
                        navController.navigate(Route.LibraryGeneralDetail.createRoute(contentId))
                    },
                    onRetry = { viewModel.retry() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // MODIFICAR: Pasar datos reales del dashboard
                TrackingHome(
                    daysRegistered = dashboard?.summary?.totalCheckIns ?: 0,
                    totalDays = 7,
                    daysFeelingSad = if (dashboard?.summary?.averageEmotionalLevel != null &&
                        dashboard.summary.averageEmotionalLevel < 5) 3 else 0,
                    averageEmotionalLevel = dashboard?.summary?.averageEmotionalLevel,
                    insightMessage = dashboard?.insights?.messages?.firstOrNull(),
                    secondButtonText = "Buscar Psicólogo",
                    onAIChatClick = {
                        navController.navigate(Route.AIWelcome.path)
                    },
                    onSecondButtonClick = {
                        navController.navigate(Route.ConnectPsychologist.path)
                    },
                    onCardClick = {
                        navController.navigate(Route.Diary.path)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Botón flotante de IA arrastrable
        DraggableAIButton(
            onClick = {
                navController.navigate(Route.AIWelcome.path)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PatientHomeScreenPreview() {
    val navController = rememberNavController()
    PatientHomeScreen(navController)
}