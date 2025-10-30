package com.softfocus.features.therapy.data.repositories

import com.softfocus.features.therapy.data.models.request.ConnectWithPsychologistRequestDto
import com.softfocus.features.therapy.data.remote.TherapyService
import com.softfocus.features.therapy.domain.models.TherapeuticRelationship
import com.softfocus.features.therapy.domain.repositories.TherapyRepository

class TherapyRepositoryImpl(
    private val therapyService: TherapyService
) : TherapyRepository {

    override suspend fun getMyRelationship(): Result<TherapeuticRelationship?> {
        return try {
            val response = therapyService.getMyRelationship()
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
            val response = therapyService.connectWithPsychologist(request)
            Result.success(response.relationshipId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
