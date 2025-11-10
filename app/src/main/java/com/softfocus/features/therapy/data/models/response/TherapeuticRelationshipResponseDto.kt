package com.softfocus.features.therapy.data.models.response

import com.softfocus.features.therapy.domain.models.TherapeuticRelationship

data class MyRelationshipResponseDto(
    val hasRelationship: Boolean,
    val relationship: RelationshipDto?
)

data class RelationshipDto(
    val id: String,
    val psychologistId: String,
    val patientId: String,
    val startDate: String,
    val status: String,
    val isActive: Boolean,
    val sessionCount: Int
) {
    fun toDomain() = TherapeuticRelationship(
        id = id,
        psychologistId = psychologistId,
        patientId = patientId,
        startDate = startDate,
        status = status,
        isActive = isActive,
        sessionCount = sessionCount
    )
}

data class ConnectResponseDto(
    val relationshipId: String
)
