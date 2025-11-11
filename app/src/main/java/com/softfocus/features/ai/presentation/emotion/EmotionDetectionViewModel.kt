package com.softfocus.features.ai.presentation.emotion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.common.result.Result
import com.softfocus.features.ai.domain.repositories.AIEmotionRepository
import com.softfocus.features.tracking.domain.usecase.GetTodayCheckInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class EmotionDetectionViewModel(
    private val emotionRepository: AIEmotionRepository,
    private val getTodayCheckInUseCase: GetTodayCheckInUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EmotionDetectionState())
    val state: StateFlow<EmotionDetectionState> = _state.asStateFlow()

    init {
        checkTodayCheckIn()
    }

    private fun checkTodayCheckIn() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCheckingTodayCheckIn = true)
            when (val result = getTodayCheckInUseCase()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        hasCheckInToday = result.data.hasCompletedToday,
                        isCheckingTodayCheckIn = false
                    )
                }
                is Result.Error -> {
                    // Si falla, asumimos que no tiene check-in hoy
                    _state.value = _state.value.copy(
                        hasCheckInToday = false,
                        isCheckingTodayCheckIn = false
                    )
                }
            }
        }
    }

    fun analyzeEmotion(imageFile: File, autoCheckIn: Boolean = true) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null,
                capturedImagePath = imageFile.absolutePath
            )

            emotionRepository.analyzeEmotion(imageFile, autoCheckIn)
                .onSuccess { analysis: com.softfocus.features.ai.domain.models.EmotionAnalysis ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        emotionAnalysis = analysis,
                        error = null
                    )
                }
                .onFailure { exception: Throwable ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error desconocido al analizar la emoción"
                    )
                }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun reset() {
        _state.value = EmotionDetectionState()
    }
}
