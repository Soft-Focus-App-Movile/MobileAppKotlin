package com.softfocus.features.ai.data.repositories

import com.softfocus.features.ai.data.remote.AIEmotionService
import com.softfocus.features.ai.domain.models.EmotionAnalysis
import com.softfocus.features.ai.domain.repositories.AIEmotionRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AIEmotionRepositoryImpl(
    private val aiEmotionService: AIEmotionService
) : AIEmotionRepository {

    override suspend fun analyzeEmotion(imageFile: File, autoCheckIn: Boolean): Result<EmotionAnalysis> {
        return try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
            val autoCheckInBody = autoCheckIn.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = aiEmotionService.analyzeEmotion(imagePart, autoCheckInBody)

            if (response.isSuccessful && response.body() != null) {
                val emotionResponse = response.body()!!
                Result.success(emotionResponse.toDomain())
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Imagen inválida o no se detectó un rostro. Por favor, intenta de nuevo."
                    413 -> "La imagen es demasiado grande. El tamaño máximo es 5MB."
                    429 -> "Has alcanzado el límite de análisis de emociones. Se resetea pronto."
                    500 -> "Error del servidor. Por favor, intenta más tarde."
                    else -> "Error al analizar emoción: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}
