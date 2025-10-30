package com.softfocus.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.data.local.LocalUserDataSource
import com.softfocus.features.therapy.domain.usecases.GetMyRelationshipUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getMyRelationshipUseCase: GetMyRelationshipUseCase,
    private val localUserDataSource: LocalUserDataSource
) : ViewModel() {

    private val _isPatient = MutableStateFlow(false)
    val isPatient: StateFlow<Boolean> = _isPatient.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkPatientStatus()
    }

    private fun checkPatientStatus() {
        viewModelScope.launch {
            _isLoading.value = true

            val hasLocalRelationship = localUserDataSource.hasTherapeuticRelationship()

            if (hasLocalRelationship) {
                _isPatient.value = true
                _isLoading.value = false
            } else {
                val result = getMyRelationshipUseCase()
                result.onSuccess { relationship ->
                    val isPatient = relationship != null
                    _isPatient.value = isPatient
                    if (isPatient) {
                        localUserDataSource.saveTherapeuticRelationship(true)
                    }
                    _isLoading.value = false
                }.onFailure {
                    _isPatient.value = false
                    _isLoading.value = false
                }
            }
        }
    }

    fun refreshPatientStatus() {
        checkPatientStatus()
    }
}
