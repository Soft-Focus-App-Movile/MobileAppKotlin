package com.softfocus.features.crisis.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.utils.LocationHelper
import com.softfocus.features.crisis.domain.models.CrisisAlert
import com.softfocus.features.crisis.domain.repositories.CrisisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CrisisViewModel(
    private val crisisRepository: CrisisRepository
) : ViewModel() {

    companion object {
        private const val TAG = "CrisisViewModel"
    }

    private val _crisisState = MutableStateFlow<CrisisState>(CrisisState.Idle)
    val crisisState: StateFlow<CrisisState> = _crisisState.asStateFlow()

    fun sendCrisisAlert(context: Context) {
        viewModelScope.launch {
            Log.d(TAG, "sendCrisisAlert: Iniciando envío de alerta de crisis")
            _crisisState.value = CrisisState.Loading

            try {
                val location = LocationHelper.getCurrentLocation(context)
                val latitude = location?.latitude
                val longitude = location?.longitude

                Log.d(TAG, "sendCrisisAlert: Ubicación obtenida - lat: $latitude, lon: $longitude")

                val result = crisisRepository.createCrisisAlert(latitude, longitude)

                result.onSuccess { alert ->
                    Log.d(TAG, "sendCrisisAlert: ✅ Alerta creada exitosamente: ${alert.id}")
                    _crisisState.value = CrisisState.Success(alert)
                }.onFailure { error ->
                    Log.e(TAG, "sendCrisisAlert: ❌ Error al crear alerta: ${error.message}")
                    _crisisState.value = CrisisState.Error(error.message ?: "Error al enviar alerta")
                }
            } catch (e: Exception) {
                Log.e(TAG, "sendCrisisAlert: ❌ Excepción: ${e.message}", e)
                _crisisState.value = CrisisState.Error(e.message ?: "Error al enviar alerta")
            }
        }
    }

    fun resetState() {
        _crisisState.value = CrisisState.Idle
    }
}

sealed class CrisisState {
    object Idle : CrisisState()
    object Loading : CrisisState()
    data class Success(val alert: CrisisAlert) : CrisisState()
    data class Error(val message: String) : CrisisState()
}
