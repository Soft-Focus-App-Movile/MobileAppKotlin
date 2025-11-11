package com.softfocus.features.psychologist.domain.models

data class PsychologistStats(
    val activePatientsCount: Int,
    val pendingCrisisAlerts: Int,
    val todayCheckInsCompleted: Int,
    val averageAdherenceRate: Double,
    val newPatientsThisMonth: Int,
    val averageEmotionalLevel: Double,
    val statsGeneratedAt: String
)
