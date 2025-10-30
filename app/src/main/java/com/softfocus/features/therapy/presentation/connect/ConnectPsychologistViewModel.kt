package com.softfocus.features.therapy.presentation.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.data.local.LocalUserDataSource
import com.softfocus.features.therapy.domain.usecases.ConnectWithPsychologistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConnectPsychologistViewModel(
    private val connectWithPsychologistUseCase: ConnectWithPsychologistUseCase,
    private val localUserDataSource: LocalUserDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConnectUiState>(ConnectUiState.Idle)
    val uiState: StateFlow<ConnectUiState> = _uiState.asStateFlow()

    fun connectWithPsychologist(code: String) {
        viewModelScope.launch {
            _uiState.value = ConnectUiState.Loading

            val result = connectWithPsychologistUseCase(code)

            result.onSuccess {
                localUserDataSource.saveTherapeuticRelationship(true)
                _uiState.value = ConnectUiState.Success
            }.onFailure { exception ->
                _uiState.value = ConnectUiState.Error(
                    exception.message ?: "Error al conectar con el psicólogo"
                )
            }
        }
    }
}

sealed class ConnectUiState {
    object Idle : ConnectUiState()
    object Loading : ConnectUiState()
    object Success : ConnectUiState()
    data class Error(val message: String) : ConnectUiState()
}
