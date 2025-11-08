package com.softfocus.features.therapy.data.repositories

import android.content.Context
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.therapy.data.models.request.ConnectWithPsychologistRequestDto
import com.softfocus.features.therapy.data.remote.TherapyService
import com.softfocus.features.therapy.domain.models.TherapeuticRelationship
import com.softfocus.features.therapy.domain.repositories.TherapyRepository

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
}
