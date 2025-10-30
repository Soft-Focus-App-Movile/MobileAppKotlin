package com.softfocus.features.therapy.domain.usecases

import com.softfocus.features.therapy.domain.models.TherapeuticRelationship
import com.softfocus.features.therapy.domain.repositories.TherapyRepository

class GetMyRelationshipUseCase(
    private val repository: TherapyRepository
) {
    suspend operator fun invoke(): Result<TherapeuticRelationship?> {
        return repository.getMyRelationship()
    }
}
