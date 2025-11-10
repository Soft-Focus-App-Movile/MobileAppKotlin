package com.softfocus.features.crisis.data.models.response

import com.google.gson.annotations.SerializedName

data class CrisisAlertResponseDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("patientId")
    val patientId: String,
    @SerializedName("patientName")
    val patientName: String,
    @SerializedName("patientPhotoUrl")
    val patientPhotoUrl: String?,
    @SerializedName("psychologistId")
    val psychologistId: String,
    @SerializedName("severity")
    val severity: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("triggerSource")
    val triggerSource: String,
    @SerializedName("triggerReason")
    val triggerReason: String?,
    @SerializedName("location")
    val location: LocationResponseDto?,
    @SerializedName("emotionalContext")
    val emotionalContext: EmotionalContextResponseDto?,
    @SerializedName("psychologistNotes")
    val psychologistNotes: String?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("attendedAt")
    val attendedAt: String?,
    @SerializedName("resolvedAt")
    val resolvedAt: String?
)
