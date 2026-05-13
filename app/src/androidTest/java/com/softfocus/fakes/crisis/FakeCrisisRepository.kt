package com.softfocus.fakes.crisis

import com.softfocus.features.crisis.domain.models.CrisisAlert
import com.softfocus.features.crisis.domain.repositories.CrisisRepository

class FakeCrisisRepository : CrisisRepository {

    var createAlertResult: Result<CrisisAlert> = Result.success(defaultAlert())
    var getPatientAlertsResult: Result<List<CrisisAlert>> = Result.success(emptyList())
    var getPsychologistAlertsResult: Result<List<CrisisAlert>> = Result.success(listOf(defaultAlert()))
    var updateAlertStatusResult: Result<CrisisAlert> = Result.success(defaultAlert().copy(status = "ATTENDED"))

    override suspend fun createCrisisAlert(latitude: Double?, longitude: Double?): Result<CrisisAlert> = createAlertResult

    override suspend fun getPatientAlerts(): Result<List<CrisisAlert>> = getPatientAlertsResult

    override suspend fun getPsychologistAlerts(severity: String?, status: String?, limit: Int?): Result<List<CrisisAlert>> = getPsychologistAlertsResult

    override suspend fun updateAlertStatus(alertId: String, status: String): Result<CrisisAlert> = updateAlertStatusResult

    fun reset() {
        createAlertResult = Result.success(defaultAlert())
        getPatientAlertsResult = Result.success(emptyList())
        getPsychologistAlertsResult = Result.success(listOf(defaultAlert()))
        updateAlertStatusResult = Result.success(defaultAlert().copy(status = "ATTENDED"))
    }

    companion object {
        fun defaultAlert() = CrisisAlert(
            id = "alert-123",
            patientId = "patient-123",
            patientName = "Paciente de Prueba",
            createdAt = "2026-05-12T10:00:00Z",
            attendedAt = "",
            resolvedAt = "",
            severity = "HIGH",
            status = "PENDING",
            location = null,
            emotionalContext = null,
            triggerSource = "Manual",
            triggerReason = "",
            psychologistId = "",
            psychologistNotes = "",
            patientPhotoUrl = ""
        )
    }
}