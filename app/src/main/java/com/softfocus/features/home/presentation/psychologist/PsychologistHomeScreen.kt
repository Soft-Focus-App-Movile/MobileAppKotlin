package com.softfocus.features.home.presentation.psychologist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.softfocus.features.home.presentation.psychologist.components.PatientsTracking
import com.softfocus.features.home.presentation.psychologist.components.StatsSection
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.psychologist.presentation.di.PsychologistPresentationModule
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.components.InvitationCard
import com.softfocus.ui.theme.Green65

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PsychologistHomeScreen(
    viewModel: PsychologistHomeViewModel,
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToPatientList: () -> Unit = {},
    onNavigateToPatientDetail: (String) -> Unit = {}
) {
    val invitationCode = viewModel.invitationCode.collectAsState()
    val patients by viewModel.patients.collectAsState()

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
            Spacer(modifier = Modifier.height(24.dp))

            // Saludo
            Text(
                text = "Hola $userName ,",
                style = CrimsonSemiBold,
                fontSize = 24.sp,
                color = Green65,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Cards
            StatsSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Título de código de invitación
            Text(
                text = "Mi código de invitación",
                style = CrimsonSemiBold,
                fontSize = 20.sp,
                color = Green65,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Invitation Card
            InvitationCard(
                code = invitationCode.value?.code ?: "Cargando...",
                onCopyClick = { viewModel.copyCodeToClipboard() },
                onShareClick = { viewModel.shareCode() },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Patients Tracking
            PatientsTracking(
                patients = patients,
                onPatientClick = { patientId ->
                    onNavigateToPatientDetail(patientId)
                },
                onViewAllClick = {
                    onNavigateToPatientList()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
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
