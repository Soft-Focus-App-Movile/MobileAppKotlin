package com.softfocus.features.therapy.domain.models

data class TherapeuticRelationship(
    val id: String,
    val psychologistId: String,
    val patientId: String,
    val startDate: String,
    val status: String,
    val isActive: Boolean,
    val sessionCount: Int
)
