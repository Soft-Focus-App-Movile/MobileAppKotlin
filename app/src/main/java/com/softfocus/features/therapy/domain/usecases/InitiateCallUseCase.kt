package com.softfocus.features.therapy.domain.usecases

import com.softfocus.features.therapy.domain.models.CallAccess
import com.softfocus.features.therapy.domain.repositories.CallRepository

class InitiateCallUseCase(
    private val callRepository: CallRepository
) {
    suspend operator fun invoke(
        callType: String,
        mode: String = "Direct",
        targetUserId: String? = null
    ): Result<CallAccess> = callRepository.initiateCall(callType, mode, targetUserId)
}
