package com.softfocus.features.tracking.presentation.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.tracking.presentation.components.DashboardInsightCard
import com.softfocus.features.tracking.presentation.components.DashboardStatCard
import com.softfocus.features.tracking.presentation.state.TrackingUiState
import com.softfocus.features.tracking.presentation.viewmodel.TrackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDays by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(selectedDays) {
        viewModel.loadDashboard(selectedDays)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Dashboard",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is TrackingUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6B8E7C))
                }
            }
            is TrackingUiState.Success -> {
                val data = (uiState as TrackingUiState.Success).data
                val dashboard = data.dashboard

                if (dashboard == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay datos de dashboard disponibles")
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(Color(0xFFF5F5F5))
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Filter chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedDays == null,
                                onClick = { selectedDays = null },
                                label = { Text("Todo") }
                            )
                            FilterChip(
                                selected = selectedDays == 7,
                                onClick = { selectedDays = 7 },
                                label = { Text("7 días") }
                            )
                            FilterChip(
                                selected = selectedDays == 30,
                                onClick = { selectedDays = 30 },
                                label = { Text("30 días") }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Summary section
                        Text(
                            text = "Resumen",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B8E7C)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DashboardStatCard(
                                title = "Check-ins",
                                value = dashboard.summary.totalCheckIns.toString(),
                                icon = "📝",
                                modifier = Modifier.weight(1f)
                            )
                            DashboardStatCard(
                                title = "Calendario",
                                value = dashboard.summary.totalEmotionalCalendarEntries.toString(),
                                icon = "📅",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Averages
                        DashboardStatCard(
                            title = "Nivel emocional promedio",
                            value = String.format("%.1f", dashboard.summary.averageEmotionalLevel),
                            icon = "😊",
                            subtitle = "de 10"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DashboardStatCard(
                            title = "Nivel de energía promedio",
                            value = String.format("%.1f", dashboard.summary.averageEnergyLevel),
                            icon = "⚡",
                            subtitle = "de 10"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DashboardStatCard(
                            title = "Estado de ánimo promedio",
                            value = String.format("%.1f", dashboard.summary.averageMoodLevel),
                            icon = "🎭",
                            subtitle = "de 10"
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Most common symptoms
                        if (dashboard.summary.mostCommonSymptoms.isNotEmpty()) {
                            Text(
                                text = "Síntomas más comunes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B8E7C)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    dashboard.summary.mostCommonSymptoms.forEach { symptom ->
                                        Text(
                                            text = "• $symptom",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Most used tags
                        if (dashboard.summary.mostUsedEmotionalTags.isNotEmpty()) {
                            Text(
                                text = "Etiquetas más usadas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B8E7C)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                dashboard.summary.mostUsedEmotionalTags.forEach { tag ->
                                    AssistChip(
                                        onClick = { },
                                        label = { Text(tag) },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = Color(0xFFE8F5E9),
                                            labelColor = Color(0xFF6B8E7C)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Insights section
                        if (dashboard.insights.messages.isNotEmpty()) {
                            Text(
                                text = "Recomendaciones",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B8E7C)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            dashboard.insights.messages.forEach { message ->
                                DashboardInsightCard(message = message)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        // Today's check-in
                        if (dashboard.summary.hasTodayCheckIn && dashboard.summary.todayCheckIn != null) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Check-in de hoy",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B8E7C)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFE8F5E9)
                                ),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = dashboard.summary.todayCheckIn.moodDescription,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Emocional: ${dashboard.summary.todayCheckIn.emotionalLevel}/10",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Energía: ${dashboard.summary.todayCheckIn.energyLevel}/10",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Sueño: ${dashboard.summary.todayCheckIn.sleepHours}h",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
            is TrackingUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (uiState as TrackingUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadDashboard(selectedDays) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6B8E7C)
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            is TrackingUiState.Initial -> {
                // Initial state
            }
        }
    }
}