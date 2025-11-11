package com.softfocus.features.therapy.domain.repositories

import com.softfocus.features.therapy.domain.models.ChatMessage
import com.softfocus.features.therapy.domain.models.PatientDirectory
import com.softfocus.features.therapy.domain.models.PatientProfile
import com.softfocus.features.therapy.domain.models.TherapeuticRelationship

interface TherapyRepository {

    suspend fun getMyRelationship(): Result<TherapeuticRelationship?>

    suspend fun connectWithPsychologist(connectionCode: String): Result<String>

    suspend fun getMyPatients(): Result<List<PatientDirectory>>

    suspend fun getChatHistory(
        relationshipId: String,
        page: Int,
        size: Int
    ): Result<List<ChatMessage>>

    suspend fun sendChatMessage(
        relationshipId: String,
        receiverId: String,
        content: String
    ): Result<ChatMessage>

    suspend fun getPatientProfile(patientId: String): Result<PatientProfile>

}
