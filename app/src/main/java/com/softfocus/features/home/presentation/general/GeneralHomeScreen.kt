package com.softfocus.features.home.presentation.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import com.softfocus.R
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.utils.LocationHelper
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray787
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.GreenEC
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.SourceSansSemiBold
import com.softfocus.ui.theme.YellowCB9D
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.home.presentation.components.RecommendationsSection
import com.softfocus.features.notifications.presentation.list.NotificationsViewModel
import com.softfocus.features.library.presentation.di.libraryViewModel
import com.softfocus.features.home.presentation.components.WelcomeCard
import com.softfocus.features.home.presentation.components.TrackingHome
import com.softfocus.features.tracking.presentation.state.TrackingUiState
import com.softfocus.features.tracking.presentation.viewmodel.TrackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralHomeScreen(
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToContentDetail: (String) -> Unit = {},
    onNavigateToSearchPsychologist: () -> Unit = {},
    onNavigateToAIChat: () -> Unit = {},
    onNavigateToCheckInForm: () -> Unit = {},
    onNavigateToDiary: () -> Unit = {}, // NUEVO
    viewModel: GeneralHomeViewModel = libraryViewModel { GeneralHomeViewModel(it) },
    trackingViewModel: TrackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var locationText by remember { mutableStateOf("Lima, Peru") }

    // Obtener información del usuario
    val userSession = remember { UserSession(context) }
    val currentUser = remember { userSession.getUser() }
    val userName = remember {
        currentUser?.fullName?.split(" ")?.firstOrNull() ?: "Usuario"
    }
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val notificationsState by notificationsViewModel.state.collectAsState()

    // Estado de las recomendaciones
    val recommendationsState by viewModel.recommendationsState.collectAsState()

    // Estados del tracking
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
                    "No ubication located"
                }
            }
        }
    }

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
                            tint = Green49,
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
                    BadgedBox(
                        badge = {
                            if (notificationsState.unreadCount > 0) {
                                Badge(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (notificationsState.unreadCount > 99) "99+"
                                        else notificationsState.unreadCount.toString(),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onNavigateToNotifications) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_notification_bell),
                                contentDescription = "Notificaciones",
                                tint = Green49,
                                modifier = Modifier.size(25.dp)
                            )
                        }
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = {
                        Text(
                            text = "Necesitas ayuda profesional?",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                onNavigateToSearchPsychologist()
                            }
                        }
                        .clickable(onClick = onNavigateToSearchPsychologist),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.White,
                        disabledPlaceholderColor = Color.Gray,
                        disabledLeadingIconColor = Color.Unspecified,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    readOnly = true,
                    enabled = false
                )

                IconButton(
                    onClick = onNavigateToSearchPsychologist,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF6B8E6F))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter),
                        contentDescription = "Filtros",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Componente 1: Bienvenida y card de registro de ánimo
            WelcomeCard(
                userName = userName,
                onRegisterMoodClick = onNavigateToCheckInForm,
                hasTodayCheckIn = dashboard?.summary?.hasTodayCheckIn ?: false,
                todayEmotionalLevel = dashboard?.summary?.todayCheckIn?.emotionalLevel,
                totalCheckIns = dashboard?.summary?.totalCheckIns ?: 0
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Componente 2: Sección completa de recomendaciones
            RecommendationsSection(
                recommendationsState = recommendationsState,
                onNavigateToLibrary = onNavigateToLibrary,
                onContentClick = onNavigateToContentDetail,
                onRetry = { viewModel.retry() }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Componente 3: Tracking y ayuda
            TrackingHome(
                daysRegistered = dashboard?.summary?.totalCheckIns ?: 0,
                totalDays = 7,
                daysFeelingSad = if (dashboard?.summary?.averageEmotionalLevel != null &&
                    dashboard.summary.averageEmotionalLevel < 5) 3 else 0,
                averageEmotionalLevel = dashboard?.summary?.averageEmotionalLevel,
                insightMessage = dashboard?.insights?.messages?.firstOrNull(),
                secondButtonText = "Buscar Psicólogo",
                onAIChatClick = onNavigateToAIChat,
                onSecondButtonClick = onNavigateToSearchPsychologist,
                onCardClick = onNavigateToDiary // CAMBIAR: ahora lleva al calendario
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GeneralHomeScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Componente 1: Bienvenida
        WelcomeCard(
            userName = "Laura",
            onRegisterMoodClick = {}
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Título de recomendaciones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recomendaciones",
                style = CrimsonSemiBold,
                fontSize = 20.sp,
                color = Green65
            )
            TextButton(onClick = {}) {
                Text(
                    text = "ver todas",
                    style = SourceSansSemiBold,
                    fontSize = 14.sp,
                    color = Gray787
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Placeholder para recomendaciones
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Recomendaciones Carousel",
                style = SourceSansRegular,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Componente 3: Tracking
        TrackingHome(
            daysRegistered = 4,
            totalDays = 7,
            daysFeelingSad = 3,
            secondButtonText = "Buscar Psicólogo",
            onAIChatClick = {},
            onSecondButtonClick = {}
        )
    }
}