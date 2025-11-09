package com.softfocus.features.crisis.data.repositories

import com.softfocus.features.crisis.data.models.request.CreateCrisisAlertRequestDto
import com.softfocus.features.crisis.data.models.request.UpdateAlertStatusRequestDto
import com.softfocus.features.crisis.data.remote.CrisisService
import com.softfocus.features.crisis.domain.models.CrisisAlert
import com.softfocus.features.crisis.domain.models.EmotionalContext
import com.softfocus.features.crisis.domain.models.Location
import com.softfocus.features.crisis.domain.repositories.CrisisRepository
import javax.inject.Inject

class CrisisRepositoryImpl @Inject constructor(
    private val crisisService: CrisisService
) : CrisisRepository {

    override suspend fun createCrisisAlert(latitude: Double?, longitude: Double?): Result<CrisisAlert> {
        return try {
            val request = CreateCrisisAlertRequestDto(latitude, longitude)
            val response = crisisService.createCrisisAlert(request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPatientAlerts(): Result<List<CrisisAlert>> {
        return try {
            val response = crisisService.getPatientAlerts()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPsychologistAlerts(severity: String?, status: String?, limit: Int?): Result<List<CrisisAlert>> {
        return try {
            val response = crisisService.getPsychologistAlerts(severity, status, limit)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAlertStatus(alertId: String, status: String): Result<CrisisAlert> {
        return try {
            val request = UpdateAlertStatusRequestDto(status)
            val response = crisisService.updateAlertStatus(alertId, request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun com.softfocus.features.crisis.data.models.response.CrisisAlertResponseDto.toDomain() = CrisisAlert(
    id = id,
    patientId = patientId,
    patientName = patientName,
    patientPhotoUrl = patientPhotoUrl,
    psychologistId = psychologistId,
    severity = severity,
    status = status,
    triggerSource = triggerSource,
    triggerReason = triggerReason,
    location = location?.let { Location(it.latitude, it.longitude, it.displayString) },
    emotionalContext = emotionalContext?.let {
        EmotionalContext(it.lastDetectedEmotion, it.lastEmotionDetectedAt, it.emotionSource)
    },
    psychologistNotes = psychologistNotes,
    createdAt = createdAt,
    attendedAt = attendedAt,
    resolvedAt = resolvedAt
)
