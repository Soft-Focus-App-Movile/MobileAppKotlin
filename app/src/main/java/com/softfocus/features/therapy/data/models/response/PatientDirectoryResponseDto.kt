package com.softfocus.features.therapy.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.therapy.domain.models.PatientDirectory

// DTO para la respuesta de la API (therapy/patients)
// Nombres de campos usan @SerializedName para coincidir con el JSON del backend (camelCase)
data class PatientDirectoryResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("psychologistId") val psychologistId: String,
    @SerializedName("patientId") val patientId: String,
    @SerializedName("patientName") val patientName: String,
    @SerializedName("age") val age: Int,
    @SerializedName("profilePhotoUrl") val profilePhotoUrl: String,
    @SerializedName("status") val status: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("sessionCount") val sessionCount: Int,
    @SerializedName("lastSessionDate") val lastSessionDate: String?
) {
    // Función de mapeo a modelo de dominio
    fun toDomain(): PatientDirectory {
        return PatientDirectory(
            id = id,
            psychologistId = psychologistId,
            patientId = patientId,
            patientName = patientName,
            age = age,
            profilePhotoUrl = profilePhotoUrl,
            status = status,
            startDate = startDate,
            sessionCount = sessionCount,
            lastSessionDate = lastSessionDate
        )
    }
}