package com.softfocus.features.psychologist.domain.repositories

import com.softfocus.features.psychologist.domain.models.InvitationCode

interface PsychologistRepository {
    suspend fun getInvitationCode(): Result<InvitationCode>
}
