package com.softfocus.features.profile.presentation.psychologist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.profile.domain.models.PsychologistProfile
import com.softfocus.features.profile.domain.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PsychologistProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow<PsychologistProfileUiState>(PsychologistProfileUiState.Loading)
    val uiState: StateFlow<PsychologistProfileUiState> = _uiState.asStateFlow()

    private val _profile = MutableStateFlow<PsychologistProfile?>(null)
    val profile: StateFlow<PsychologistProfile?> = _profile.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = PsychologistProfileUiState.Loading

            profileRepository.getPsychologistCompleteProfile()
                .onSuccess { profile ->
                    _profile.value = profile
                    _uiState.value = PsychologistProfileUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = PsychologistProfileUiState.Error(
                        error.message ?: "Error al cargar perfil"
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userSession.clear()
        }
    }
}

sealed class PsychologistProfileUiState {
    object Loading : PsychologistProfileUiState()
    object Success : PsychologistProfileUiState()
    data class Error(val message: String) : PsychologistProfileUiState()
}
