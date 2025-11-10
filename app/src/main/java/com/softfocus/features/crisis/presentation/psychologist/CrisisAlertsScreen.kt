package com.softfocus.features.crisis.presentation.psychologist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.softfocus.R
import com.softfocus.features.crisis.domain.models.CrisisAlert
import com.softfocus.features.crisis.domain.models.EmotionalContext
import com.softfocus.features.crisis.domain.models.Location
import com.softfocus.features.crisis.presentation.psychologist.components.PatientCrisisCard
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.presentation.shared.getDisplayName
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.White
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrisisAlertsScreen(
    viewModel: CrisisAlertsViewModel,
    onNavigateBack: () -> Unit,
    onViewPatientProfile: (String) -> Unit,
    onSendMessage: (String) -> Unit
) {
    val alertsState by viewModel.alertsState.collectAsState()
    val selectedSeverity by viewModel.selectedSeverity.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf(
        "Todas" to null,
        "Críticas" to "Critical",
        "Altas" to "High",
        "Moderadas" to "Moderate"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Alertas",
                        style = CrimsonSemiBold,
                        fontSize = 32.sp,
                        color = Green49,
                        textAlign = TextAlign.Center
                    )

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
                .background(White)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = Green65,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Green65
                    )
                },
                divider = { }
            ) {
                tabs.forEachIndexed { index, (title, severity) ->
                    val isSelected = selectedTabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = {
                            selectedTabIndex = index
                            viewModel.loadAlerts(severity)
                        },
                        text = {
                            Text(
                                text = title,
                                style = SourceSansRegular.copy(fontSize = 15.sp),
                                color = if (isSelected) Green65 else Black
                            )
                        }
                    )
                }
            }

            when (alertsState) {
                is CrisisAlertsState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Green49)
                    }
                }

                is CrisisAlertsState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_empty_state),
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay alertas de crisis",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                is CrisisAlertsState.Success -> {
                    val alerts = (alertsState as CrisisAlertsState.Success).alerts
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(alerts) { alert ->
                            PatientCrisisCard(
                                alert = alert,
                                onViewProfile = { onViewPatientProfile(alert.patientId) },
                                onSendMessage = { onSendMessage(alert.patientId) },
                                onUpdateStatus = { viewModel.updateAlertStatus(alert) }
                            )
                        }
                    }
                }

                is CrisisAlertsState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_error),
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = (alertsState as CrisisAlertsState.Error).message,
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.retry() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Green49
                                )
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CrisisAlertsScreenLoadingPreview() {
    val previewAlerts = listOf(
        CrisisAlert(
            id = "1",
            patientId = "patient123",
            patientName = "María González",
            patientPhotoUrl = null,
            psychologistId = "psych123",
            severity = "Critical",
            status = "Pending",
            triggerSource = "Manual",
            triggerReason = "Ansiedad severa",
            location = Location(-12.0464, -77.0428, "Lima, Perú"),
            emotionalContext = EmotionalContext("Ansiedad", "2025-11-09T10:30:00.000Z", "AI_Chat"),
            psychologistNotes = null,
            createdAt = "2025-11-09T10:30:00.000Z",
            attendedAt = null,
            resolvedAt = null
        ),
        CrisisAlert(
            id = "2",
            patientId = "patient456",
            patientName = "Juan Pérez",
            patientPhotoUrl = null,
            psychologistId = "psych123",
            severity = "High",
            status = "Attended",
            triggerSource = "AI_Chat",
            triggerReason = "Depresión detectada",
            location = Location(-12.0464, -77.0428, "Callao, Perú"),
            emotionalContext = EmotionalContext("Tristeza", "2025-11-09T09:15:00.000Z", "AI_Chat"),
            psychologistNotes = null,
            createdAt = "2025-11-09T09:15:00.000Z",
            attendedAt = "2025-11-09T10:00:00.000Z",
            resolvedAt = null
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(previewAlerts) { alert ->
                PatientCrisisCard(
                    alert = alert,
                    onViewProfile = {},
                    onSendMessage = {},
                    onUpdateStatus = {}
                )
            }
        }
    }
}
