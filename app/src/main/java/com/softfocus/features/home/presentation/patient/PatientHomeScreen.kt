package com.softfocus.features.home.presentation.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.ui.res.painterResource
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
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.utils.LocationHelper
import com.softfocus.features.home.presentation.components.RecommendationsSection
import com.softfocus.features.home.presentation.components.WelcomeCard
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.softfocus.core.navigation.Route
import com.softfocus.ui.components.DraggableAIButton
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green29
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.Green65

import com.softfocus.ui.theme.Gray787
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.SourceSansSemiBold
import com.softfocus.features.home.presentation.patient.di.patientHomeViewModel
import com.softfocus.ui.theme.YellowCB9D

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHomeScreen(
    navController: NavController,
    onNavigateToNotifications: () -> Unit = {},
    viewModel: PatientHomeViewModel = patientHomeViewModel()
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

    LaunchedEffect(Unit) {
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
                        Surface(
                            shape = CircleShape,
                            color = Color.Red,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "SOS",
                                    style = SourceSansRegular,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
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
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Chat con terapeuta",
                style = CrimsonSemiBold,
                fontSize = 20.sp,
                color = Green65,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TherapistChatCard(
                therapistState = therapistState,
                onRetry = { viewModel.retryTherapist() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tareas pendientes",
                style = CrimsonSemiBold,
                fontSize = 20.sp,
                color = Green65,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TaskCard(
                title = "Tienes 2 tareas por completar",
                showButton = false
            )

            TaskCard(
                title = "Ejercicio de respiración 4-7-8",
                buttonText = "Ir",
                showButton = true
            )

            TaskCard(
                title = "Video bienestar guiada",
                buttonText = "Ver",
                showButton = true
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

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = YellowCB9D)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Has registrado 4 días\nesta semana",
                        style = SourceSansSemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Green49,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Llevas 3 días sintiéndote mal,",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Green29
                        )
                        Text(
                            text = "¿necesitas ayuda?",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Green29
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                navController.navigate(Route.AIWelcome.path)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YellowCB9D
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Hablar con IA",
                                style = SourceSansRegular,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Hablar con él",
                                style = SourceSansRegular,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

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

@Composable
fun TherapistChatCard(
    therapistState: TherapistState,
    onRetry: () -> Unit
) {
    val context = LocalContext.current

    when (therapistState) {
        is TherapistState.Loading -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = YellowCB9D)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Green49,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        is TherapistState.Success -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable { },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = YellowCB9D)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (therapistState.psychologist.profileImageUrl != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(therapistState.psychologist.profileImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = therapistState.psychologist.fullName.firstOrNull()?.toString() ?: "T",
                                style = SourceSansSemiBold,
                                fontSize = 18.sp,
                                color = Green49
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = therapistState.psychologist.fullName,
                            style = SourceSansSemiBold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Recuerda escribir tu caso, ofrezco de consultar ...",
                            style = SourceSansRegular,
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        is TherapistState.NoTherapist -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = YellowCB9D)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes un terapeuta asignado",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray787
                    )
                }
            }
        }
        is TherapistState.Error -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = YellowCB9D)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error al cargar terapeuta",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray787
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onRetry) {
                        Text(
                            text = "Reintentar",
                            style = SourceSansSemiBold,
                            fontSize = 14.sp,
                            color = Green49
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    title: String,
    buttonText: String = "",
    showButton: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = YellowCB9D)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircleOutline,
                    contentDescription = null,
                    tint = Green49,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = SourceSansRegular,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            if (showButton) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green49
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = buttonText,
                        style = SourceSansRegular,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PatientHomeScreenPreview() {
    val navController = rememberNavController()
    PatientHomeScreen(navController)
}
