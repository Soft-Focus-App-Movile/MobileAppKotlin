package com.softfocus.features.home.presentation.psychologist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.utils.LocationHelper
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.psychologist.presentation.di.PsychologistPresentationModule
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.components.InvitationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PsychologistHomeScreen(viewModel: PsychologistHomeViewModel) {
    val invitationCode = viewModel.invitationCode.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var locationText by remember { mutableStateOf("Lima, Peru") }

    // Obtener información del usuario
    val userSession = remember { UserSession(context) }
    val currentUser = remember { userSession.getUser() }
    val userName = remember {
        currentUser?.fullName?.split(" ")?.firstOrNull() ?: "Usuario"
    }

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
                    IconButton(onClick = { }) {
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
        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6B8E6F))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Hola $userName ,",
                    style = CrimsonSemiBold,
                    fontSize = 24.sp,
                    color = Gray828,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "👥",
                        title = "Pacientes Activos",
                        value = "2",
                        subtitle = "Ver el calendario activo"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "🔔",
                        title = "Alertas Pendientes",
                        value = "3",
                        subtitle = "3 alertas"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "⚠️",
                        title = "Sin registro Hoy",
                        value = "5",
                        subtitle = "Pacientes sin completar registro"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Mi código de invitación",
                    style = CrimsonSemiBold,
                    fontSize = 20.sp,
                    color = Gray828,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                InvitationCard(
                    code = invitationCode.value?.code ?: "Cargando...",
                    onCopyClick = { viewModel.copyCodeToClipboard() },
                    onShareClick = { viewModel.shareCode() },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pacientes con actividad reciente",
                        style = CrimsonSemiBold,
                        fontSize = 18.sp,
                        color = Gray828
                    )
                    TextButton(onClick = { }) {
                        Text(
                            text = "Ver todos",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                PatientActivityCard(
                    name = "Ana García",
                    status = "Completó registro",
                    time = "hace 4h"
                )

                PatientActivityCard(
                    name = "Luis Torres",
                    status = "No realizó registro",
                    time = "Hoy"
                )

                PatientActivityCard(
                    name = "María Lopes",
                    status = "Completó registro",
                    time = "Ayer"
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = icon,
                fontSize = 32.sp
            )
            Text(
                text = title,
                style = SourceSansRegular,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray828
            )
            Text(
                text = value,
                style = CrimsonSemiBold,
                fontSize = 32.sp,
                color = Color.Black
            )
            Text(
                text = subtitle,
                style = SourceSansRegular,
                fontSize = 9.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PsychologistHomeScreenPreview() {
    val context = LocalContext.current
    val viewModel = PsychologistPresentationModule.getPsychologistHomeViewModel(context)
    PsychologistHomeScreen(viewModel)
}

@Composable
fun PatientActivityCard(
    name: String,
    status: String,
    time: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = SourceSansRegular,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = status,
                    style = SourceSansRegular,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = time,
                    style = SourceSansRegular,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
