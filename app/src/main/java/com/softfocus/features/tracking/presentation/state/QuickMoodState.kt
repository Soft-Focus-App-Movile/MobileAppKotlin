package com.softfocus.features.tracking.presentation.state

import com.softfocus.features.tracking.domain.model.EmotionalCalendarEntry

sealed class QuickMoodState {
    object Idle : QuickMoodState()
    object Loading : QuickMoodState()
    data class Success(val entry: EmotionalCalendarEntry) : QuickMoodState()
    data class Error(val message: String) : QuickMoodState()
}
