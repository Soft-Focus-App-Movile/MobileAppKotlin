package com.softfocus.features.tracking.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.tracking.presentation.components.*
import com.softfocus.features.tracking.presentation.state.CheckInFormState
import com.softfocus.features.tracking.presentation.state.EmotionalCalendarFormState
import com.softfocus.features.tracking.presentation.viewmodel.TrackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInFormScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDiary: (() -> Unit)? = null,
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val formState by viewModel.checkInFormState.collectAsState()
    val emotionalCalendarFormState by viewModel.emotionalCalendarFormState.collectAsState()
    var currentStep by remember { mutableStateOf(0) }

    // Form data
    var moodLevel by remember { mutableStateOf(5) }
    var selectedMoodEmoji by remember { mutableStateOf("😐") }
    var moodDescription by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedSymptoms by remember { mutableStateOf<List<String>>(emptyList()) }
    var emotionalLevel by remember { mutableStateOf(5) }
    var energyLevel by remember { mutableStateOf(5) }
    var sleepHours by remember { mutableStateOf(7) }
    var notes by remember { mutableStateOf("") }

    val totalSteps = 6

    // Estados de éxito combinados
    LaunchedEffect(formState, emotionalCalendarFormState) {
        if (formState is CheckInFormState.Success &&
            emotionalCalendarFormState is EmotionalCalendarFormState.Success) {
            onNavigateToDiary?.invoke() ?: onNavigateBack()
            viewModel.resetCheckInFormState()
            viewModel.resetEmotionalCalendarFormState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6B8E7C)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF6B8E7C))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress indicator
                Text(
                    text = "${currentStep + 1}/$totalSteps",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Steps
                when (currentStep) {
                    0 -> MoodSelectionStep(
                        selectedMood = moodLevel,
                        onMoodSelected = { level ->
                            moodLevel = level
                            // Actualizar emoji según el nivel
                            selectedMoodEmoji = when (level) {
                                in 1..2 -> "😢"
                                in 3..4 -> "😕"
                                in 5..6 -> "😐"
                                in 7..8 -> "🙂"
                                else -> "😄"
                            }
                        },
                        onNext = { currentStep++ }
                    )
                    1 -> CategorySelectionStep(
                        title = "¿Qué hizo que hoy fuera así?",
                        categories = listOf("Trabajo", "Pareja", "Familia"),
                        selectedCategories = selectedCategories,
                        onCategoriesSelected = { selectedCategories = it },
                        onNext = { currentStep++ }
                    )
                    2 -> DetailsStep(
                        question = "Te gustaría dar más detalles de lo que pasó?",
                        notes = notes,
                        onNotesChanged = { notes = it },
                        onNext = { currentStep++ },
                        onSkip = { currentStep++ }
                    )
                    3 -> SymptomsSelectionStep(
                        symptoms = emptyList(), // Ya no se usa
                        selectedSymptoms = selectedSymptoms,
                        onSymptomsSelected = { selectedSymptoms = it },
                        onNext = { currentStep++ }
                    )
                    4 -> LevelsStep(
                        emotionalLevel = emotionalLevel,
                        onEmotionalLevelChanged = { emotionalLevel = it },
                        energyLevel = energyLevel,
                        onEnergyLevelChanged = { energyLevel = it },
                        sleepHours = sleepHours,
                        onSleepHoursChanged = { sleepHours = it },
                        onNext = { currentStep++ }
                    )
                    5 -> SummaryStep(
                        onSubmit = {
                            // Construir moodDescription basado en el nivel
                            moodDescription = when (moodLevel) {
                                in 1..2 -> "Me siento terrible"
                                in 3..4 -> "Me siento mal"
                                in 5..6 -> "Me siento regular"
                                in 7..8 -> "Me siento bien"
                                else -> "Me siento excelente"
                            }

                            // 1. Guardar CHECK-IN
                            viewModel.createCheckIn(
                                emotionalLevel = emotionalLevel,
                                energyLevel = energyLevel,
                                moodDescription = moodDescription,
                                sleepHours = sleepHours,
                                symptoms = selectedSymptoms,
                                notes = notes.ifBlank { null }
                            )

                            // 2. Guardar EMOTIONAL CALENDAR
                            val currentDate = java.time.LocalDateTime.now()
                                .format(java.time.format.DateTimeFormatter.ISO_DATE_TIME)

                            viewModel.createEmotionalCalendarEntry(
                                date = currentDate,
                                emotionalEmoji = selectedMoodEmoji,
                                moodLevel = moodLevel,
                                emotionalTags = selectedCategories
                            )
                        },
                        isLoading = formState is CheckInFormState.Loading ||
                                emotionalCalendarFormState is EmotionalCalendarFormState.Loading
                    )
                }

                // Navigation buttons
                if (currentStep > 0 && currentStep < totalSteps - 1) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { currentStep-- }) {
                            Text("Atrás", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}