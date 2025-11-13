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
import java.time.ZonedDateTime

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
    override suspend fun getChatHistory(relationshipId: String, page: Int, size: Int): Result<List<ChatMessage>> {
        return try {
            val response = therapyService.getChatHistory(
                token = getAuthToken(),
                relationshipId = relationshipId,
                page = page,
                size = size
            )
            // Asumimos que el DTO tiene un mapper .toDomain()
            val messages = response.messages.map { it.toDomain(getUserId()) }
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getUserId(): String {
        return userSession.getUser()?.id ?: throw IllegalStateException("ID de usuario no disponible.")
    }

    override suspend fun sendChatMessage(relationshipId: String, receiverId: String, content: String): Result<ChatMessage> {
        return try {
            val request = SendChatMessageRequestDto(
                relationshipId = relationshipId,
                receiverId = receiverId,
                content = content,
                messageType = "text"
            )
            val response = therapyService.sendChatMessage(
                token = getAuthToken(),
                request = request
            )

            // Creamos un ChatMessage local temporalmente, ya que el backend
            // solo devuelve el ID. El mensaje real llegará por SignalR.
            // O, si el backend devuelve el mensaje completo, lo mapeamos.
            // Por ahora, creamos uno local:
            val sentMessage = ChatMessage(
                id = response.messageId,
                relationshipId = relationshipId,
                senderId = getUserId(),
                receiverId = receiverId,
                content = content,
                timestamp = ZonedDateTime.now().toString(),
                isFromMe = true,
                messageType = " "
            )
            Result.success(sentMessage)

        } catch (e: Exception) {
            Result.failure(e)
        }
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
}
