package com.softfocus.features.tracking.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.tracking.presentation.components.CalendarDatePicker
import com.softfocus.features.tracking.presentation.components.EmotionalCalendarGrid
import com.softfocus.features.tracking.presentation.state.TrackingUiState
import com.softfocus.features.tracking.presentation.viewmodel.TrackingViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCheckIn: () -> Unit,
    onNavigateToProgress: () -> Unit,
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Calendario", "Progreso")

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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCheckIn,
                containerColor = Color(0xFF6B8E7C),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add check-in")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFFF5F5F5),
                contentColor = Color(0xFF6B8E7C)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            if (index == 1) onNavigateToProgress()
                        },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
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

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Calendar date picker
                        CalendarDatePicker(
                            selectedDate = LocalDate.now(),
                            onDateSelected = { date ->
                                // Load emotional calendar for selected month
                                viewModel.loadEmotionalCalendar(
                                    startDate = date.withDayOfMonth(1).toString(),
                                    endDate = date.withDayOfMonth(date.lengthOfMonth()).toString()
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Emotional calendar grid
                        data.emotionalCalendar?.let { calendar ->
                            EmotionalCalendarGrid(
                                entries = calendar.entries,
                                onDateClick = { entry ->
                                    // Handle date click - could navigate to detail or edit
                                }
                            )
                        }
                    }
                }
                is TrackingUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (uiState as TrackingUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is TrackingUiState.Initial -> {
                    // Initial state
                }
            }
        }
    }
}