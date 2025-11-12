package com.softfocus.features.psychologist.domain.usecases

import com.softfocus.features.psychologist.domain.models.PsychologistStats
import com.softfocus.features.psychologist.domain.repositories.PsychologistRepository

class GetPsychologistStatsUseCase(
    private val repository: PsychologistRepository
) {
    suspend operator fun invoke(): Result<PsychologistStats> {
        return repository.getStats()
    }
}
