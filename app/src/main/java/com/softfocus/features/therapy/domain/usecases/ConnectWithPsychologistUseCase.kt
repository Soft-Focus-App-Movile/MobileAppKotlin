package com.softfocus.features.therapy.domain.usecases

import com.softfocus.features.therapy.domain.repositories.TherapyRepository

class ConnectWithPsychologistUseCase(
    private val repository: TherapyRepository
) {
    suspend operator fun invoke(connectionCode: String): Result<String> {
        return repository.connectWithPsychologist(connectionCode)
    }
}
