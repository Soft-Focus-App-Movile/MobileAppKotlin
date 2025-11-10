package com.softfocus.features.psychologist.data.repositories

import com.softfocus.features.psychologist.data.remote.PsychologistService
import com.softfocus.features.psychologist.domain.models.InvitationCode
import com.softfocus.features.psychologist.domain.repositories.PsychologistRepository

class PsychologistRepositoryImpl(
    private val psychologistService: PsychologistService
) : PsychologistRepository {

    override suspend fun getInvitationCode(): Result<InvitationCode> {
        return try {
            val response = psychologistService.getInvitationCode()
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
