package com.softfocus.features.psychologist.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.psychologist.domain.models.PsychologistStats

data class PsychologistStatsResponseDto(
    @SerializedName("activePatientsCount")
    val activePatientsCount: Int,

    @SerializedName("pendingCrisisAlerts")
    val pendingCrisisAlerts: Int,

    @SerializedName("todayCheckInsCompleted")
    val todayCheckInsCompleted: Int,

    @SerializedName("averageAdherenceRate")
    val averageAdherenceRate: Double,

    @SerializedName("newPatientsThisMonth")
    val newPatientsThisMonth: Int,

    @SerializedName("averageEmotionalLevel")
    val averageEmotionalLevel: Double,

    @SerializedName("statsGeneratedAt")
    val statsGeneratedAt: String
) {
    fun toDomain(): PsychologistStats {
        return PsychologistStats(
            activePatientsCount = activePatientsCount,
            pendingCrisisAlerts = pendingCrisisAlerts,
            todayCheckInsCompleted = todayCheckInsCompleted,
            averageAdherenceRate = averageAdherenceRate,
            newPatientsThisMonth = newPatientsThisMonth,
            averageEmotionalLevel = averageEmotionalLevel,
            statsGeneratedAt = statsGeneratedAt
        )
    }
}
