package com.softfocus.features.admin.presentation.verifypsychologist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.admin.domain.models.PsychologistDetail
import com.softfocus.features.admin.domain.repositories.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VerifyPsychologistViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _psychologist = MutableStateFlow<PsychologistDetail?>(null)
    val psychologist: StateFlow<PsychologistDetail?> = _psychologist

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _verificationSuccess = MutableStateFlow(false)
    val verificationSuccess: StateFlow<Boolean> = _verificationSuccess

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    fun loadPsychologistDetail(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getPsychologistDetail(userId)
                .onSuccess { detail ->
                    _psychologist.value = detail
                    _isLoading.value = false
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Error al cargar detalles"
                    _isLoading.value = false
                }
        }
    }

    fun updateNotes(value: String) {
        _notes.value = value
    }

    fun approvePsychologist() {
        _psychologist.value?.let { psychologist ->
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null

                repository.verifyPsychologist(
                    userId = psychologist.id,
                    isApproved = true,
                    notes = _notes.value.ifBlank { null }
                ).onSuccess {
                    _verificationSuccess.value = true
                    _isLoading.value = false
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Error al aprobar"
                    _isLoading.value = false
                }
            }
        }
    }

    fun rejectPsychologist() {
        _psychologist.value?.let { psychologist ->
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null

                repository.verifyPsychologist(
                    userId = psychologist.id,
                    isApproved = false,
                    notes = _notes.value.ifBlank { null }
                ).onSuccess {
                    _verificationSuccess.value = true
                    _isLoading.value = false
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Error al rechazar"
                    _isLoading.value = false
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetVerificationSuccess() {
        _verificationSuccess.value = false
    }
}
