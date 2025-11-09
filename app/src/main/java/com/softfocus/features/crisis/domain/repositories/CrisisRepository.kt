package com.softfocus.features.crisis.domain.repositories

import com.softfocus.features.crisis.domain.models.CrisisAlert

interface CrisisRepository {
    suspend fun createCrisisAlert(latitude: Double?, longitude: Double?): Result<CrisisAlert>
    suspend fun getPatientAlerts(): Result<List<CrisisAlert>>
    suspend fun getPsychologistAlerts(): Result<List<CrisisAlert>>
}
