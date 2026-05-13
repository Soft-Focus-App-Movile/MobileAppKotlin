package com.softfocus.fakes.profile

import com.softfocus.features.therapy.domain.models.ChatMessage
import com.softfocus.features.therapy.domain.models.PatientDirectory
import com.softfocus.features.therapy.domain.models.PatientProfile
import com.softfocus.features.therapy.domain.models.TherapeuticRelationship
import com.softfocus.features.therapy.domain.repositories.TherapyRepository
import com.softfocus.features.tracking.domain.model.CheckIn

class FakeTherapyRepository : TherapyRepository {

    var getMyRelationshipResult: Result<TherapeuticRelationship?> = Result.success(defaultRelationship())
    var disconnectResult: Result<Unit> = Result.success(Unit)

    override suspend fun getMyRelationship(): Result<TherapeuticRelationship?> =
        getMyRelationshipResult

    override suspend fun disconnectRelationship(relationshipId: String): Result<Unit> =
        disconnectResult

    override suspend fun connectWithPsychologist(connectionCode: String): Result<String> =
        Result.success("relationship-123")

    override suspend fun getMyPatients(): Result<List<PatientDirectory>> =
        Result.success(emptyList())

    override suspend fun getRelationshipWithPatient(patientId: String): Result<String> =
        Result.success("relationship-123")

    override suspend fun sendChatMessage(
        relationshipId: String, receiverId: String,
        content: String, messageType: String
    ): Result<String> = Result.success("msg-123")

    override suspend fun getChatHistory(
        relationshipId: String, page: Int, size: Int
    ): Result<List<ChatMessage>> = Result.success(emptyList())

    override suspend fun getLastReceivedMessage(): Result<ChatMessage?> = Result.success(null)

    override suspend fun getPatientProfile(patientId: String): Result<PatientProfile> =
        Result.failure(NotImplementedError())

    override suspend fun getPatientCheckIns(
        patientId: String, startDate: String?, endDate: String?,
        page: Int, pageSize: Int
    ): Result<List<CheckIn>> = Result.success(emptyList())

    companion object {
        fun defaultRelationship() = TherapeuticRelationship(
            id = "relationship-123",
            psychologistId = "psych-123",
            patientId = "user-123",
            startDate = "2026-01-01",
            status = "active",
            isActive = true,
            sessionCount = 3
        )

        fun noRelationship(): TherapeuticRelationship? = null
    }
}
