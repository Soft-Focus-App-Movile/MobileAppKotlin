package com.softfocus.features.library.assignments.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.analytics.SoftFocusAnalytics
import com.softfocus.features.library.assignments.domain.repositories.AssignmentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssignmentsViewModel(
    private val repository: AssignmentsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AssignmentsUiState>(AssignmentsUiState.Loading)
    val uiState: StateFlow<AssignmentsUiState> = _uiState.asStateFlow()

    init {
        loadAssignedContent(completed = false)
    }

    fun loadAssignedContent(completed: Boolean? = null) {
        viewModelScope.launch {
            _uiState.value = AssignmentsUiState.Loading

            repository.getAssignedContent(completed).fold(
                onSuccess = { assignments ->
                    val pending = assignments.count { !it.isCompleted }
                    val completedCount = assignments.count { it.isCompleted }

                    SoftFocusAnalytics.logAssignmentsViewed(pending, completedCount)
                    _uiState.value = AssignmentsUiState.Success(
                        assignments = assignments,
                        pendingCount = pending,
                        completedCount = completedCount
                    )
                },
                onFailure = { exception ->
                    _uiState.value = AssignmentsUiState.Error(
                        message = exception.message ?: "Error al cargar asignaciones"
                    )
                }
            )
        }
    }

    fun completeAssignment(assignmentId: String) {
        viewModelScope.launch {
            repository.completeAssignment(assignmentId).fold(
                onSuccess = {
                    SoftFocusAnalytics.logAssignmentCompleted(assignmentId)
                    val currentState = _uiState.value
                    if (currentState is AssignmentsUiState.Success) {
                        val updatedAssignments = currentState.assignments.filter { it.id != assignmentId }
                        val pending = updatedAssignments.count { !it.isCompleted }
                        val completedCount = updatedAssignments.count { it.isCompleted }

                        _uiState.value = AssignmentsUiState.Success(
                            assignments = updatedAssignments,
                            pendingCount = pending,
                            completedCount = completedCount
                        )
                    }
                },
                onFailure = { exception ->
                    Log.e("AssignmentsViewModel", "Error al completar asignación: ${exception.message}", exception)
                }
            )
        }
    }

    fun retry() {
        loadAssignedContent()
    }
}
