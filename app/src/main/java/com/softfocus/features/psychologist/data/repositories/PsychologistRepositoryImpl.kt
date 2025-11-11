package com.softfocus.features.psychologist.data.repositories

import com.softfocus.features.psychologist.data.remote.PsychologistService
import com.softfocus.features.psychologist.domain.models.InvitationCode
import com.softfocus.features.psychologist.domain.models.PsychologistStats
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

    override suspend fun getStats(fromDate: String?, toDate: String?): Result<PsychologistStats> {
        return try {
            val response = psychologistService.getStats(fromDate, toDate)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
