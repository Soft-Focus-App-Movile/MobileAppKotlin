package com.softfocus.features.therapy.domain.usecases

import com.softfocus.features.therapy.domain.models.PatientProfile
import com.softfocus.features.therapy.domain.repositories.TherapyRepository

class GetPatientProfileUseCase(
    private val repository: TherapyRepository
) {
    /**
     * Llama al repositorio para obtener el perfil de un paciente por su ID.
     */
    suspend operator fun invoke(patientId: String): Result<PatientProfile> {
        return repository.getPatientProfile(patientId)
    }
}