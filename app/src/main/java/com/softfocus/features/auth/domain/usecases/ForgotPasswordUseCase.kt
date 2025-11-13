package com.softfocus.features.auth.domain.usecases

import com.softfocus.features.auth.domain.repositories.AuthRepository

class ForgotPasswordUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<String> {
        return repository.forgotPassword(email)
    }
}
