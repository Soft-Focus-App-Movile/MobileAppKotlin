package com.softfocus.features.ai.presentation.emotion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.ai.domain.repositories.AIEmotionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class EmotionDetectionViewModel(
    private val emotionRepository: AIEmotionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EmotionDetectionState())
    val state: StateFlow<EmotionDetectionState> = _state.asStateFlow()

    fun analyzeEmotion(imageFile: File, autoCheckIn: Boolean = true) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null,
                capturedImagePath = imageFile.absolutePath
            )

            emotionRepository.analyzeEmotion(imageFile, autoCheckIn)
                .onSuccess { analysis ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        emotionAnalysis = analysis,
                        error = null
                    )
                }
                .onFailure { exception ->
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
