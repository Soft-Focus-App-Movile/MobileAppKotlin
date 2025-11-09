package com.softfocus.features.therapy.domain.usecases

import com.softfocus.features.therapy.domain.models.PatientDirectory
import com.softfocus.features.therapy.domain.repositories.TherapyRepository

class GetMyPatientsUseCase(
    private val repository: TherapyRepository
) {
    // Permite llamar a la clase como si fuera una función
    suspend operator fun invoke(): Result<List<PatientDirectory>> {
        return repository.getMyPatients()
    }
}