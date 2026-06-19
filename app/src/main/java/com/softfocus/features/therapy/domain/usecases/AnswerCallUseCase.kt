package com.softfocus.features.therapy.domain.usecases

import com.softfocus.features.therapy.domain.models.CallAccess
import com.softfocus.features.therapy.domain.repositories.CallRepository

class AnswerCallUseCase(
    private val callRepository: CallRepository
) {
    suspend operator fun invoke(callId: String): Result<CallAccess> = callRepository.answerCall(callId)
}
