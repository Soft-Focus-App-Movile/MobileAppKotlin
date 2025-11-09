package com.softfocus.features.crisis.domain.repositories

import com.softfocus.features.crisis.domain.models.CrisisAlert

interface CrisisRepository {
    suspend fun createCrisisAlert(latitude: Double?, longitude: Double?): Result<CrisisAlert>
    suspend fun getPatientAlerts(): Result<List<CrisisAlert>>
    suspend fun getPsychologistAlerts(severity: String? = null, status: String? = null, limit: Int? = null): Result<List<CrisisAlert>>
    suspend fun updateAlertStatus(alertId: String, status: String): Result<CrisisAlert>
}
