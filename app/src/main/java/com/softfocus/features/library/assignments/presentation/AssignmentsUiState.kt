package com.softfocus.features.library.assignments.presentation

import com.softfocus.features.library.domain.models.Assignment

sealed class AssignmentsUiState {

    data object Loading : AssignmentsUiState()

    data class Success(
        val assignments: List<Assignment> = emptyList(),
        val pendingCount: Int = 0,
        val completedCount: Int = 0
    ) : AssignmentsUiState()

    data class Error(
        val message: String
    ) : AssignmentsUiState()
}
