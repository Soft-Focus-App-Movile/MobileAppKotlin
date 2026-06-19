package com.softfocus.features.therapy.domain.usecases

import com.softfocus.features.therapy.domain.repositories.CallRepository

class RejectCallUseCase(
    private val callRepository: CallRepository
) {
    suspend operator fun invoke(callId: String): Result<Unit> = callRepository.rejectCall(callId)
}
