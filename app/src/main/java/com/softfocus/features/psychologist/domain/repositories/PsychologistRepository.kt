package com.softfocus.features.psychologist.domain.repositories

import com.softfocus.features.psychologist.domain.models.InvitationCode
import com.softfocus.features.psychologist.domain.models.PsychologistStats

interface PsychologistRepository {
    suspend fun getInvitationCode(): Result<InvitationCode>
    suspend fun getStats(fromDate: String? = null, toDate: String? = null): Result<PsychologistStats>
}
