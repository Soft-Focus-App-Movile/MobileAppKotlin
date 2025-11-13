package com.softfocus.features.auth.domain.usecases

import com.softfocus.features.auth.domain.repositories.AuthRepository

class ResetPasswordUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(token: String, email: String, newPassword: String): Result<String> {
        return repository.resetPassword(token, email, newPassword)
    }
}
