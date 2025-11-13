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
    private val getTrackingDashboardUseCase: GetTrackingDashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrackingUiState>(TrackingUiState.Initial)
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()

    private val _checkInFormState = MutableStateFlow<CheckInFormState>(CheckInFormState.Idle)
    val checkInFormState: StateFlow<CheckInFormState> = _checkInFormState.asStateFlow()

    private val _emotionalCalendarFormState = MutableStateFlow<EmotionalCalendarFormState>(EmotionalCalendarFormState.Idle)
    val emotionalCalendarFormState: StateFlow<EmotionalCalendarFormState> = _emotionalCalendarFormState.asStateFlow()

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
        date: String,
        emotionalEmoji: String,
        moodLevel: Int,
        emotionalTags: List<String>
    ) {
        viewModelScope.launch {
            _emotionalCalendarFormState.value = EmotionalCalendarFormState.Loading

            when (val result = createEmotionalCalendarEntryUseCase(
                date = date,
                emotionalEmoji = emotionalEmoji,
                moodLevel = moodLevel,
                emotionalTags = emotionalTags
            )) {
                is Result.Success -> {
                    _emotionalCalendarFormState.value = EmotionalCalendarFormState.Success
                    // Recargar calendario después de crear entrada
                    loadEmotionalCalendar()
                }
                is Result.Error -> {
                    _emotionalCalendarFormState.value = EmotionalCalendarFormState.Error(result.message)
                }
            }
        }
    }

    fun loadTodayCheckIn() {
        viewModelScope.launch {
            when (val result = getTodayCheckInUseCase()) {
                is Result.Success -> {
                    _uiState.update { state ->
                        if (state is TrackingUiState.Success) {
                            state.copy(
                                data = state.data.copy(todayCheckIn = result.data)
                            )
                        } else {
                            TrackingUiState.Success(TrackingData(todayCheckIn = result.data))
                        }
                    }
                }
                is Result.Error -> {
                    // Handle error silently or show message
                }
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