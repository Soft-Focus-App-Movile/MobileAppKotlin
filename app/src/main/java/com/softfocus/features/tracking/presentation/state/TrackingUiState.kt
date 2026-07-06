package com.softfocus.features.tracking.presentation.state

import com.softfocus.features.tracking.domain.model.*

sealed class TrackingUiState {
    object Initial : TrackingUiState()
    object Loading : TrackingUiState()
    data class Success(val data: TrackingData) : TrackingUiState()
    data class Error(val message: String) : TrackingUiState()
}

data class TrackingData(
    val todayCheckIn: TodayCheckIn? = null,
    val checkInHistory: CheckInHistory? = null,
    val emotionalCalendar: EmotionalCalendar? = null,
    val dashboard: TrackingDashboard? = null,
    val isLoadingHistory: Boolean = false // Para mostrar loading específico del historial
)

sealed class CheckInFormState {
    object Idle : CheckInFormState()
    object Loading : CheckInFormState()
    object Success : CheckInFormState()
    data class Error(val message: String) : CheckInFormState()
}

sealed class EmotionalCalendarFormState {
    object Idle : EmotionalCalendarFormState()
    object Loading : EmotionalCalendarFormState()
    object Success : EmotionalCalendarFormState()
    data class Error(val message: String) : EmotionalCalendarFormState()
}

sealed class DeleteTodayEntriesState {
    object Idle : DeleteTodayEntriesState()
    object Loading : DeleteTodayEntriesState()
    data class Success(val deletedCount: Int) : DeleteTodayEntriesState()
    data class Error(val message: String) : DeleteTodayEntriesState()
}
