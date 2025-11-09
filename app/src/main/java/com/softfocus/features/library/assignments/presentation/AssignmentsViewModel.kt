package com.softfocus.features.library.assignments.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        loadAssignedContent()
    }

    fun loadAssignedContent(completed: Boolean? = null) {
        viewModelScope.launch {
            _uiState.value = AssignmentsUiState.Loading

            repository.getAssignedContent(completed).fold(
                onSuccess = { assignments ->
                    val pending = assignments.count { !it.isCompleted }
                    val completedCount = assignments.count { it.isCompleted }

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
                    loadAssignedContent()
                },
                onFailure = { exception ->
                    _uiState.value = AssignmentsUiState.Error(
                        message = exception.message ?: "Error al completar asignación"
                    )
                }
            )
        }
    }

    fun retry() {
        loadAssignedContent()
    }
}
