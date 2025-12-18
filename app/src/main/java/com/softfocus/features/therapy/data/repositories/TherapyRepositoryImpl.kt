package com.softfocus.features.therapy.data.repositories

import android.content.Context
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.therapy.data.models.request.ConnectWithPsychologistRequestDto
import com.softfocus.features.therapy.data.models.request.SendChatMessageRequestDto
import com.softfocus.features.therapy.data.models.response.toDomain
import com.softfocus.features.therapy.data.remote.TherapyService
import com.softfocus.features.therapy.domain.models.ChatMessage
import com.softfocus.features.therapy.domain.models.PatientDirectory
import com.softfocus.features.therapy.domain.models.PatientProfile
import com.softfocus.features.therapy.domain.models.TherapeuticRelationship
import com.softfocus.features.therapy.domain.repositories.TherapyRepository
import com.softfocus.features.tracking.data.mapper.toDomain
import com.softfocus.features.tracking.domain.model.CheckIn
import java.io.IOException

class TherapyRepositoryImpl(
    private val therapyService: TherapyService,
    private val context: Context
) : TherapyRepository {

    private val userSession = UserSession(context)

    private fun getAuthToken(): String {
        val token = userSession.getUser()?.token
        if (token.isNullOrEmpty()) {
            throw IllegalStateException("Token no disponible. Usuario debe iniciar sesión nuevamente.")
        }
        return "Bearer $token"
    }

    override suspend fun getMyRelationship(): Result<TherapeuticRelationship?> {
        return try {
            val response = therapyService.getMyRelationship(
                token = getAuthToken()
            )
            val relationship = if (response.hasRelationship && response.relationship != null) {
                response.relationship.toDomain()
            } else {
                null
            }
            Result.success(relationship)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRelationshipWithPatient(patientId: String): Result<String> {
        return try {
            val response = therapyService.getRelationshipWithPatient(
                token = getAuthToken(),
                patientId = patientId
            )
            Result.success(response.relationshipId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun connectWithPsychologist(connectionCode: String): Result<String> {
        return try {
            val request = ConnectWithPsychologistRequestDto(connectionCode)
            val response = therapyService.connectWithPsychologist(
                token = getAuthToken(),
                request = request
            )
            Result.success(response.relationshipId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyPatients(): Result<List<PatientDirectory>> {
        return try {
            val response = therapyService.getMyPatients(
                token = getAuthToken()
            )
            val patients = response.map { it.toDomain() }
            Result.success(patients)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getUserId(): String {
        return userSession.getUser()?.id ?: throw IllegalStateException("ID de usuario no disponible.")
    }

    override suspend fun getPatientProfile(patientId: String): Result<PatientProfile> {
        return try {
            val response = therapyService.getPatientDetails(
                token = getAuthToken(),
                id = patientId
            )
            // Usamos la función .toDomain() que creamos en el DTO
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPatientCheckIns(
        patientId: String,
        startDate: String?,
        endDate: String?,
        page: Int,
        pageSize: Int
    ): Result<List<CheckIn>> {
        return try {
            val response = therapyService.getPatientCheckIns(
                token = getAuthToken(),
                patientId = patientId,
                startDate = startDate,
                endDate = endDate,
                page = page,
                pageSize = pageSize
            )

            val checkIns = response.data.map { it.toDomain() }

            Result.success(checkIns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun disconnectRelationship(relationshipId: String): Result<Unit> {
        return try {
            therapyService.disconnectRelationship(
                token = getAuthToken(),
                relationshipId = relationshipId
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Implementación de Send Chat Message
     * Devuelve el ID del mensaje como confirmación.
     */
    override suspend fun sendChatMessage(
        relationshipId: String,
        receiverId: String,
        content: String,
        messageType: String
    ): Result<String> {
        return try {
            val request = SendChatMessageRequestDto(
                relationshipId = relationshipId,
                receiverId = receiverId,
                content = content,
                messageType = messageType
            )
            val response = therapyService.sendChatMessage(
                token = getAuthToken(),
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                // Devuelve solo el ID del mensaje, como solicitaste
                Result.success(response.body()!!.messageId)
            } else {
                Result.failure(IOException("Error al enviar mensaje: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Implementación de Get Chat History
     * Mapea los DTOs (incluyendo el "content.value") a Modelos de Dominio.
     */
    override suspend fun getChatHistory(
        relationshipId: String,
        page: Int,
        size: Int
    ): Result<List<ChatMessage>> {
        return try {
            val response = therapyService.getChatHistory(
                token = getAuthToken(),
                relationshipId = relationshipId,
                page = page,
                size = size
            )

            if (response.isSuccessful) {
                val dtos = response.body() ?: emptyList()
                val userId = getUserId()

                // Mapear DTOs a Modelos de Dominio
                val messages = dtos.map { dto ->
                    ChatMessage(
                        id = dto.id,
                        relationshipId = dto.relationshipId,
                        senderId = dto.senderId,
                        receiverId = dto.receiverId,
                        content = dto.content.value, // <-- Mapeo clave del DTO anidado
                        timestamp = dto.timestamp,
                        isFromMe = dto.senderId == userId, // Lógica para la UI
                        messageType = dto.messageType
                    )
                }
                Result.success(messages)
            } else {
                Result.failure(IOException("Error al obtener historial: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Implementación de Get Last Received Message
     * Obtiene el último mensaje o null si no existe (404).
     */
    override suspend fun getLastReceivedMessage(): Result<ChatMessage?> {
        return try {
            val response = therapyService.getLastReceivedMessage(getAuthToken())

            // 404 Not Found es un caso de éxito, significa que no hay mensajes.
            if (response.code() == 404) {
                return Result.success(null)
            }

            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val userId = getUserId()

                val message = ChatMessage(
                    id = dto.id,
                    relationshipId = dto.relationshipId,
                    senderId = dto.senderId,
                    receiverId = dto.receiverId,
                    content = dto.content.value, // <-- Mapeo clave
                    timestamp = dto.timestamp,
                    isFromMe = dto.senderId == userId,
                    messageType = dto.messageType
                )
                Result.success(message)
            } else {
                Result.failure(IOException("Error al obtener el último mensaje: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
