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
import com.softfocus.features.tracking.presentation.components.ActivityChart
import com.softfocus.features.tracking.presentation.components.EmptyProgressState
import com.softfocus.features.tracking.presentation.components.StatisticsCard
import com.softfocus.features.tracking.presentation.state.TrackingUiState
import com.softfocus.features.tracking.presentation.viewmodel.TrackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Recargar historial cuando se abre la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadCheckInHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Diario",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = 1,
                containerColor = Color(0xFFF5F5F5),
                contentColor = Color(0xFF6B8E7C)
            ) {
                Tab(
                    selected = false,
                    onClick = onNavigateBack,
                    text = { Text("Calendario") }
                )
                Tab(
                    selected = true,
                    onClick = { },
                    text = { Text("Progreso", fontWeight = FontWeight.Bold) }
                )
            }

            when (uiState) {
                is TrackingUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF6B8E7C))
                    }
                }
                is TrackingUiState.Success -> {
                    val data = (uiState as TrackingUiState.Success).data

                    if (data.isLoadingHistory) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF6B8E7C))
                        }
                    } else if (data.checkInHistory?.checkIns.isNullOrEmpty()) {
                        EmptyProgressState()
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Your Activity",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B8E7C)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Activity chart
                            data.checkInHistory?.let { history ->
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
                                        ActivityChart(checkIns = history.checkIns)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Statistics cards
                            data.checkInHistory?.let { history ->
                                Text(
                                    text = "Estadísticas",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B8E7C)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                StatisticsCard(
                                    title = "Total de registros",
                                    value = history.checkIns.size.toString(),
                                    icon = "📊"
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                val avgEmotional = history.checkIns
                                    .map { it.emotionalLevel }
                                    .average()
                                    .takeIf { !it.isNaN() } ?: 0.0

                                StatisticsCard(
                                    title = "Nivel emocional promedio",
                                    value = String.format("%.1f", avgEmotional),
                                    icon = "😊"
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                val avgEnergy = history.checkIns
                                    .map { it.energyLevel }
                                    .average()
                                    .takeIf { !it.isNaN() } ?: 0.0

                                StatisticsCard(
                                    title = "Nivel de energía promedio",
                                    value = String.format("%.1f", avgEnergy),
                                    icon = "⚡"
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                val avgSleep = history.checkIns
                                    .map { it.sleepHours }
                                    .average()
                                    .takeIf { !it.isNaN() } ?: 0.0

                                StatisticsCard(
                                    title = "Horas de sueño promedio",
                                    value = String.format("%.1f", avgSleep),
                                    icon = "😴"
                                )
                            }
                        }
                    }
                }
                is TrackingUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
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
                                onClick = { viewModel.loadCheckInHistory() },
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
}