package com.softfocus.features.therapy.domain.usecases

import com.softfocus.features.therapy.domain.repositories.TherapyRepository
import com.softfocus.features.tracking.domain.model.CheckIn

/**
 * Caso de uso para obtener el historial de check-ins de un paciente.
 */
class GetPatientCheckInsUseCase(
    private val repository: TherapyRepository
) {
    suspend operator fun invoke(
        patientId: String,
        startDate: String? = null,
        endDate: String? = null,
        page: Int,
        pageSize: Int
    ): Result<List<CheckIn>> {
        return repository.getPatientCheckIns(
            patientId = patientId,
            startDate = startDate,
            endDate = endDate,
            page = page,
            pageSize = pageSize
        )
    }
}