package com.softfocus.features.tracking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.common.result.Result
import com.softfocus.features.tracking.domain.usecase.*
import com.softfocus.features.tracking.presentation.state.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val createCheckInUseCase: CreateCheckInUseCase,
    private val getCheckInsUseCase: GetCheckInsUseCase,
    private val getTodayCheckInUseCase: GetTodayCheckInUseCase,
    private val createEmotionalCalendarEntryUseCase: CreateEmotionalCalendarEntryUseCase,
    private val getEmotionalCalendarUseCase: GetEmotionalCalendarUseCase,
    private val getEmotionalCalendarByDateUseCase: GetEmotionalCalendarByDateUseCase,
    private val getTrackingDashboardUseCase: GetTrackingDashboardUseCase,
    private val createQuickEmotionalEntryUseCase: CreateQuickEmotionalEntryUseCase,
    private val getTodayEmotionalEntriesUseCase: GetTodayEmotionalEntriesUseCase,
    private val deleteTodayEmotionalEntriesUseCase: DeleteTodayEmotionalEntriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrackingUiState>(TrackingUiState.Initial)
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()

    private val _checkInFormState = MutableStateFlow<CheckInFormState>(CheckInFormState.Idle)
    val checkInFormState: StateFlow<CheckInFormState> = _checkInFormState.asStateFlow()

    private val _emotionalCalendarFormState = MutableStateFlow<EmotionalCalendarFormState>(EmotionalCalendarFormState.Idle)
    val emotionalCalendarFormState: StateFlow<EmotionalCalendarFormState> = _emotionalCalendarFormState.asStateFlow()

    private val _todayEntries = MutableStateFlow<List<com.softfocus.features.tracking.domain.model.EmotionalCalendarEntry>>(emptyList())
    val todayEntries: StateFlow<List<com.softfocus.features.tracking.domain.model.EmotionalCalendarEntry>> = _todayEntries.asStateFlow()

    private val _quickMoodState = MutableStateFlow<QuickMoodState>(QuickMoodState.Idle)
    val quickMoodState: StateFlow<QuickMoodState> = _quickMoodState.asStateFlow()

    private val _deleteTodayEntriesState = MutableStateFlow<DeleteTodayEntriesState>(DeleteTodayEntriesState.Idle)
    val deleteTodayEntriesState: StateFlow<DeleteTodayEntriesState> = _deleteTodayEntriesState.asStateFlow()

    init {
        loadInitialData()
    }

    // MODIFICAR: Hacer loadInitialData más completa
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = TrackingUiState.Loading

            try {
                val todayCheckIn = getTodayCheckInUseCase()
                val emotionalCalendar = getEmotionalCalendarUseCase()
                val checkInHistory = getCheckInsUseCase()
                val dashboard = getTrackingDashboardUseCase(days = 7) // AGREGAR

                if (todayCheckIn is Result.Success &&
                    emotionalCalendar is Result.Success &&
                    checkInHistory is Result.Success &&
                    dashboard is Result.Success) {
                    _uiState.value = TrackingUiState.Success(
                        TrackingData(
                            todayCheckIn = todayCheckIn.data,
                            emotionalCalendar = emotionalCalendar.data,
                            checkInHistory = checkInHistory.data,
                            dashboard = dashboard.data // AGREGAR
                        )
                    )
                } else {
                    // Intentar cargar datos parciales
                    _uiState.value = TrackingUiState.Success(
                        TrackingData(
                            todayCheckIn = (todayCheckIn as? Result.Success)?.data,
                            emotionalCalendar = (emotionalCalendar as? Result.Success)?.data,
                            checkInHistory = (checkInHistory as? Result.Success)?.data,
                            dashboard = (dashboard as? Result.Success)?.data // AGREGAR
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = TrackingUiState.Error(e.message ?: "Error loading data")
            }
        }
    }

    // AGREGAR: Función pública para refrescar todos los datos
    fun refreshData() {
        loadInitialData()
    }

    fun createCheckIn(
        emotionalLevel: Int,
        energyLevel: Int,
        moodDescription: String,
        sleepHours: Int,
        symptoms: List<String>,
        notes: String?
    ) {
        viewModelScope.launch {
            _checkInFormState.value = CheckInFormState.Loading

            when (val result = createCheckInUseCase(
                emotionalLevel = emotionalLevel,
                energyLevel = energyLevel,
                moodDescription = moodDescription,
                sleepHours = sleepHours,
                symptoms = symptoms,
                notes = notes
            )) {
                is Result.Success -> {
                    _checkInFormState.value = CheckInFormState.Success
                    // Recargar datos después de crear check-in
                    loadTodayCheckIn()
                    loadCheckInHistory()
                    loadDashboard(days = 7) // AGREGAR
                }
                is Result.Error -> {
                    _checkInFormState.value = CheckInFormState.Error(result.message)
                }
            }
        }
    }

    fun createEmotionalCalendarEntry(
        timestamp: String,
        emotionalEmoji: String,
        moodLevel: Int,
        emotionalTags: List<String>,
        content: String = "",
        sessionDurationSeconds: Int = 0,
        entryType: String = "spontaneous"
    ) {
        viewModelScope.launch {
            _emotionalCalendarFormState.value = EmotionalCalendarFormState.Loading

            when (val result = createEmotionalCalendarEntryUseCase(
                timestamp = timestamp,
                emotionalEmoji = emotionalEmoji,
                moodLevel = moodLevel,
                emotionalTags = emotionalTags,
                content = content,
                sessionDurationSeconds = sessionDurationSeconds,
                entryType = entryType
            )) {
                is Result.Success -> {
                    _emotionalCalendarFormState.value = EmotionalCalendarFormState.Success
                    // Recargar calendario después de crear entrada
                    loadEmotionalCalendar()
                    loadTodayEmotionalEntries()
                }
                is Result.Error -> {
                    _emotionalCalendarFormState.value = EmotionalCalendarFormState.Error(result.message)
                }
            }
        }
    }

    fun submitDailyCheckIn(
        emotionalLevel: Int,
        energyLevel: Int,
        moodDescription: String,
        sleepHours: Int,
        symptoms: List<String>,
        notes: String?,
        timestamp: String,
        emotionalEmoji: String,
        moodLevel: Int,
        emotionalTags: List<String>,
        content: String = "",
        sessionDurationSeconds: Int = 0
    ) {
        if (_checkInFormState.value is CheckInFormState.Loading ||
            _emotionalCalendarFormState.value is EmotionalCalendarFormState.Loading
        ) return

        _checkInFormState.value = CheckInFormState.Loading
        _emotionalCalendarFormState.value = EmotionalCalendarFormState.Loading

        viewModelScope.launch {
            when (val todayResult = getTodayCheckInUseCase()) {
                is Result.Success -> {
                    updateTodayCheckIn(todayResult.data)
                    if (todayResult.data.hasCompletedToday || todayResult.data.checkIn != null) {
                        _checkInFormState.value =
                            CheckInFormState.Error("Ya completaste el check-in diario de hoy.")
                        _emotionalCalendarFormState.value = EmotionalCalendarFormState.Idle
                        loadTodayEmotionalEntries()
                        loadEmotionalCalendar()
                        loadDashboard(days = 7)
                        return@launch
                    }
                }
                is Result.Error -> {
                    _checkInFormState.value =
                        CheckInFormState.Error("No se pudo verificar si ya hiciste el check-in de hoy.")
                    _emotionalCalendarFormState.value = EmotionalCalendarFormState.Idle
                    return@launch
                }
            }

            when (val checkInResult = createCheckInUseCase(
                emotionalLevel = emotionalLevel,
                energyLevel = energyLevel,
                moodDescription = moodDescription,
                sleepHours = sleepHours,
                symptoms = symptoms,
                notes = notes
            )) {
                is Result.Success -> {
                    _checkInFormState.value = CheckInFormState.Success
                    loadTodayCheckIn()
                    loadCheckInHistory()
                    loadDashboard(days = 7)
                }
                is Result.Error -> {
                    _checkInFormState.value = CheckInFormState.Error(checkInResult.message)
                    _emotionalCalendarFormState.value = EmotionalCalendarFormState.Idle
                    loadTodayCheckIn()
                    loadDashboard(days = 7)
                    return@launch
                }
            }

            when (val calendarResult = createEmotionalCalendarEntryUseCase(
                timestamp = timestamp,
                emotionalEmoji = emotionalEmoji,
                moodLevel = moodLevel,
                emotionalTags = emotionalTags,
                content = content,
                sessionDurationSeconds = sessionDurationSeconds,
                entryType = "scheduled"
            )) {
                is Result.Success -> {
                    _emotionalCalendarFormState.value = EmotionalCalendarFormState.Success
                    loadEmotionalCalendar()
                    loadTodayEmotionalEntries()
                }
                is Result.Error -> {
                    _emotionalCalendarFormState.value =
                        EmotionalCalendarFormState.Error(calendarResult.message)
                    loadEmotionalCalendar()
                    loadTodayEmotionalEntries()
                }
            }
        }
    }

    fun createQuickEmotionalEntry(
        emoji: String,
        level: Int,
        content: String = "",
        durationSeconds: Int = 0
    ) {
        if (_quickMoodState.value is QuickMoodState.Loading) return
        _quickMoodState.value = QuickMoodState.Loading

        viewModelScope.launch {
            when (val result = createQuickEmotionalEntryUseCase(
                emotionalEmoji = emoji,
                moodLevel = level,
                content = content,
                sessionDurationSeconds = durationSeconds
            )) {
                is Result.Success -> {
                    _quickMoodState.value = QuickMoodState.Success(result.data)
                    loadTodayEmotionalEntries()
                    loadEmotionalCalendar()
                }
                is Result.Error -> {
                    _quickMoodState.value = QuickMoodState.Error(result.message)
                }
            }
        }
    }

    fun loadTodayEmotionalEntries() {
        viewModelScope.launch {
            when (val result = getTodayEmotionalEntriesUseCase()) {
                is Result.Success -> {
                    _todayEntries.value = result.data
                }
                is Result.Error -> {
                    // Handle error silently
                }
            }
        }
    }

    fun refreshTodayEntries() {
        loadTodayEmotionalEntries()
    }

    fun deleteTodayQuickEntries() {
        if (_deleteTodayEntriesState.value is DeleteTodayEntriesState.Loading) return
        _deleteTodayEntriesState.value = DeleteTodayEntriesState.Loading

        viewModelScope.launch {
            when (val result = deleteTodayEmotionalEntriesUseCase(entryType = "spontaneous")) {
                is Result.Success -> {
                    _deleteTodayEntriesState.value =
                        DeleteTodayEntriesState.Success(result.data.deletedCount)
                    loadTodayCheckIn()
                    loadTodayEmotionalEntries()
                    loadEmotionalCalendar()
                    loadDashboard(days = 7)
                }
                is Result.Error -> {
                    _deleteTodayEntriesState.value = DeleteTodayEntriesState.Error(result.message)
                }
            }
        }
    }

    fun resetDeleteTodayEntriesState() {
        _deleteTodayEntriesState.value = DeleteTodayEntriesState.Idle
    }

    fun resetQuickMoodState() {
        _quickMoodState.value = QuickMoodState.Idle
    }

    fun loadTodayCheckIn() {
        viewModelScope.launch {
            when (val result = getTodayCheckInUseCase()) {
                is Result.Success -> {
                    updateTodayCheckIn(result.data)
                }
                is Result.Error -> {
                    // Handle error silently or show message
                }
            }
        }
    }

    private fun updateTodayCheckIn(todayCheckIn: com.softfocus.features.tracking.domain.model.TodayCheckIn) {
        _uiState.update { state ->
            if (state is TrackingUiState.Success) {
                state.copy(
                    data = state.data.copy(todayCheckIn = todayCheckIn)
                )
            } else {
                TrackingUiState.Success(TrackingData(todayCheckIn = todayCheckIn))
            }
        }
    }

    fun loadEmotionalCalendar(startDate: String? = null, endDate: String? = null) {
        viewModelScope.launch {
            when (val result = getEmotionalCalendarUseCase(startDate, endDate)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        if (state is TrackingUiState.Success) {
                            state.copy(
                                data = state.data.copy(emotionalCalendar = result.data)
                            )
                        } else {
                            TrackingUiState.Success(TrackingData(emotionalCalendar = result.data))
                        }
                    }
                }
                is Result.Error -> {
                    // Handle error
                }
            }
        }
    }

    fun loadCheckInHistory(
        startDate: String? = null,
        endDate: String? = null,
        pageNumber: Int? = null,
        pageSize: Int? = null
    ) {
        viewModelScope.launch {
            _uiState.update { state ->
                if (state is TrackingUiState.Success) {
                    state.copy(data = state.data.copy(isLoadingHistory = true))
                } else {
                    TrackingUiState.Loading
                }
            }

            when (val result = getCheckInsUseCase(startDate, endDate, pageNumber, pageSize)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        if (state is TrackingUiState.Success) {
                            state.copy(
                                data = state.data.copy(
                                    checkInHistory = result.data,
                                    isLoadingHistory = false
                                )
                            )
                        } else {
                            TrackingUiState.Success(
                                TrackingData(
                                    checkInHistory = result.data,
                                    isLoadingHistory = false
                                )
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state is TrackingUiState.Success) {
                            state.copy(data = state.data.copy(isLoadingHistory = false))
                        } else {
                            TrackingUiState.Error(result.message)
                        }
                    }
                }
            }
        }
    }

    fun loadDashboard(days: Int? = null) {
        viewModelScope.launch {
            when (val result = getTrackingDashboardUseCase(days)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        if (state is TrackingUiState.Success) {
                            state.copy(
                                data = state.data.copy(dashboard = result.data)
                            )
                        } else {
                            TrackingUiState.Success(TrackingData(dashboard = result.data))
                        }
                    }
                }
                is Result.Error -> {
                    // Handle error
                }
            }
        }
    }

    fun resetCheckInFormState() {
        _checkInFormState.value = CheckInFormState.Idle
    }

    fun resetEmotionalCalendarFormState() {
        _emotionalCalendarFormState.value = EmotionalCalendarFormState.Idle
    }
}
