package com.softfocus.features.tracking.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.tracking.domain.model.EmotionalCalendarEntry
import com.softfocus.features.tracking.presentation.components.CalendarDatePicker
import com.softfocus.features.tracking.presentation.components.EmotionalCalendarGrid
import com.softfocus.features.tracking.presentation.components.getMoodImageResource
import com.softfocus.features.tracking.presentation.state.DeleteTodayEntriesState
import com.softfocus.features.tracking.presentation.state.TrackingUiState
import com.softfocus.features.tracking.presentation.viewmodel.TrackingViewModel
import com.softfocus.ui.theme.AppColors
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
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
    val todayEntries by viewModel.todayEntries.collectAsState()
    val deleteTodayEntriesState by viewModel.deleteTodayEntriesState.collectAsState()
    val trackingData = (uiState as? TrackingUiState.Success)?.data
    val hasCompletedToday = trackingData?.todayCheckIn?.hasCompletedToday == true ||
        trackingData?.todayCheckIn?.checkIn != null ||
        trackingData?.dashboard?.summary?.hasTodayCheckIn == true ||
        trackingData?.dashboard?.summary?.todayCheckIn != null
    var selectedTab by remember { mutableStateOf(0) }
    var showQuickMoodSheet by remember { mutableStateOf(false) }
    var showClearEntriesDialog by remember { mutableStateOf(false) }
    val quickEntriesCount = todayEntries.count { it.entryType == "spontaneous" }
    val tabs = listOf("Calendario", "Progreso")

    LaunchedEffect(Unit) {
        viewModel.refreshData()
        viewModel.loadTodayEmotionalEntries()
    }

    LaunchedEffect(deleteTodayEntriesState) {
        if (deleteTodayEntriesState is DeleteTodayEntriesState.Success) {
            showClearEntriesDialog = false
            viewModel.resetDeleteTodayEntriesState()
        }
    }

    if (showQuickMoodSheet) {
        QuickMoodEntrySheet(
            onDismiss = {
                showQuickMoodSheet = false
                viewModel.refreshTodayEntries()
            },
            onSubmit = { _, _, _, _ ->
                // Handled by ViewModel (createQuickEmotionalEntry)
            }
        )
    }

    if (showClearEntriesDialog) {
        AlertDialog(
            onDismissRequest = {
                if (deleteTodayEntriesState !is DeleteTodayEntriesState.Loading) {
                    showClearEntriesDialog = false
                    viewModel.resetDeleteTodayEntriesState()
                }
            },
            title = { Text("Limpiar entradas rápidas") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Se eliminarán las $quickEntriesCount entradas rápidas de hoy. Tu check-in completo no se borrará.")
                    if (deleteTodayEntriesState is DeleteTodayEntriesState.Error) {
                        Text(
                            text = (deleteTodayEntriesState as DeleteTodayEntriesState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteTodayQuickEntries() },
                    enabled = deleteTodayEntriesState !is DeleteTodayEntriesState.Loading
                ) {
                    if (deleteTodayEntriesState is DeleteTodayEntriesState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Limpiar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearEntriesDialog = false
                        viewModel.resetDeleteTodayEntriesState()
                    },
                    enabled = deleteTodayEntriesState !is DeleteTodayEntriesState.Loading
                ) {
                    Text("Cancelar")
                }
            }
        )
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
                    containerColor = AppColors.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (hasCompletedToday) {
                        showQuickMoodSheet = true
                    } else {
                        onNavigateToCheckIn()
                    }
                },
                containerColor = Color(0xFF6B8E7C),
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = if (hasCompletedToday) "Quick mood" else "Add check-in"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppColors.background)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = AppColors.background,
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
                            .verticalScroll(rememberScrollState())
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
                                onDateClick = { date, _ ->
                                    if (date == LocalDate.now()) {
                                        showQuickMoodSheet = true
                                    }
                                }
                            )
                        }

                        // Today's entries (24h diary experiment: múltiples entradas por día)
                        if (todayEntries.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Entradas de hoy",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.textPrimary
                                )
                                if (quickEntriesCount > 0) {
                                    TextButton(
                                        onClick = {
                                            viewModel.resetDeleteTodayEntriesState()
                                            showClearEntriesDialog = true
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Limpiar entradas rápidas",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Limpiar")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            todayEntries.forEach { entry ->
                                EmotionalEntryCard(entry = entry)
                            }
                            // Espacio extra para no quedar detrás de los FABs
                            Spacer(modifier = Modifier.height(120.dp))
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

@Composable
fun EmotionalEntryCard(entry: EmotionalCalendarEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = getMoodImageResource(entry.moodLevel)),
                contentDescription = "Nivel emocional ${entry.moodLevel}",
                modifier = Modifier.size(44.dp)
            )
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Nivel: ${entry.moodLevel}/10",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (entry.entryType == "scheduled") "programada" else "espontánea",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B8E7C)
                    )
                }
                Text(
                    text = "Hora: ${formatEntryTime(entry.timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.textSecondary
                )
                if (entry.content.isNotEmpty()) {
                    Text(
                        text = entry.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.textSecondary
                    )
                }
            }
        }
    }
}

private fun formatEntryTime(timestamp: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        try {
            // Timestamps UTC con offset/Z (ej: 2026-07-02T14:30:45Z)
            Instant.parse(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(formatter)
        } catch (e: Exception) {
            // Timestamps sin offset (ej: 2026-07-02T14:30:45.123)
            LocalDateTime.parse(timestamp.substringBefore("Z")).format(formatter)
        }
    } catch (e: Exception) {
        timestamp.substringAfter("T").take(5)
    }
}
