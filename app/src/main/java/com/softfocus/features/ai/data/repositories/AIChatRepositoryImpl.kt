package com.softfocus.features.ai.data.repositories

import com.softfocus.features.ai.data.models.request.ChatMessageRequestDto
import com.softfocus.features.ai.data.remote.AIChatService
import com.softfocus.features.ai.domain.models.AIUsageStats
import com.softfocus.features.ai.domain.models.ChatMessage
import com.softfocus.features.ai.domain.models.ChatSession
import com.softfocus.features.ai.domain.repositories.AIChatRepository
import com.softfocus.features.ai.domain.repositories.ChatResponse

class AIChatRepositoryImpl(
    private val aiChatService: AIChatService
) : AIChatRepository {

    override suspend fun sendMessage(message: String, sessionId: String?): Result<ChatResponse> {
        return try {
            val request = ChatMessageRequestDto(
                message = message,
                sessionId = sessionId
            )
            val response = aiChatService.sendMessage(request)

            if (response.isSuccessful && response.body() != null) {
                val chatMessageResponse = response.body()!!
                Result.success(
                    ChatResponse(
                        message = chatMessageResponse.toDomain(),
                        sessionId = chatMessageResponse.sessionId
                    )
                )
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Mensaje inválido. Por favor, intenta de nuevo."
                    429 -> "Has alcanzado el límite de mensajes. Se resetea pronto."
                    500 -> "Error del servidor. Por favor, intenta más tarde."
                    else -> "Error al enviar mensaje: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    override suspend fun getUsageStats(): Result<AIUsageStats> {
        return try {
            val response = aiChatService.getUsageStats()

            if (response.isSuccessful && response.body() != null) {
                val statsResponse = response.body()!!
                Result.success(statsResponse.toDomain())
            } else {
                Result.failure(Exception("Error al obtener estadísticas: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    override suspend fun getChatSessions(pageSize: Int): Result<List<ChatSession>> {
        return try {
            val response = aiChatService.getChatSessions(pageSize)

            if (response.isSuccessful && response.body() != null) {
                val sessionsResponse = response.body()!!
                Result.success(sessionsResponse.sessions.map { it.toDomain() })
            } else {
                Result.failure(Exception("Error al obtener sesiones: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    override suspend fun getSessionMessages(sessionId: String, limit: Int): Result<List<ChatMessage>> {
        return try {
            val response = aiChatService.getSessionMessages(sessionId, limit)

            if (response.isSuccessful && response.body() != null) {
                val messagesResponse = response.body()!!
                Result.success(messagesResponse.messages.map { it.toDomain() })
            } else {
                Result.failure(Exception("Error al obtener mensajes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}
