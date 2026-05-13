package com.softfocus.fakes.auth

import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.auth.domain.repositories.AuthRepository
import com.softfocus.features.auth.domain.repositories.OAuthVerificationData

/**
 * Fake implementation of AuthRepository para UI e Integration tests.
 *
 * No llama al servidor real. Devuelve respuestas controladas.
 * Cambia loginResult antes del test para simular éxito o error.
 */
class FakeAuthRepository : AuthRepository {

    var loginResult: Result<User> = Result.success(defaultUser())
    var forgotPasswordResult: Result<String> = Result.success("Correo enviado")
    var registerResult: Result<Pair<String, String>> = Result.success(Pair("user-123", "test@softfocus.com"))

    var loginCallCount = 0

    override suspend fun login(email: String, password: String): Result<User> {
        loginCallCount++
        return loginResult
    }

    override suspend fun forgotPassword(email: String): Result<String> = forgotPasswordResult

    override suspend fun registerGeneralUser(
        firstName: String, lastName: String, email: String,
        password: String, acceptsPrivacyPolicy: Boolean
    ): Result<Pair<String, String>> = registerResult

    override suspend fun registerPsychologist(
        firstName: String, lastName: String, email: String, password: String,
        professionalLicense: String, yearsOfExperience: Int, collegiateRegion: String,
        university: String, graduationYear: Int, acceptsPrivacyPolicy: Boolean,
        licenseDocumentUri: String, diplomaDocumentUri: String, dniDocumentUri: String,
        specialties: String?, certificationDocumentUris: List<String>?
    ): Result<Pair<String, String>> = Result.success(Pair("psych-123", "psych@softfocus.com"))

    override suspend fun socialLogin(provider: String, token: String): Result<User> =
        Result.success(defaultUser())

    override suspend fun verifyOAuth(provider: String, accessToken: String): Result<OAuthVerificationData> =
        Result.success(OAuthVerificationData("test@gmail.com", "Test User", provider, "temp-token", false, "General"))

    override suspend fun oauthLogin(provider: String, token: String): Result<User> =
        Result.success(defaultUser())

    override suspend fun completeOAuthRegistrationGeneral(
        tempToken: String, acceptsPrivacyPolicy: Boolean
    ): Result<User> = Result.success(defaultUser())

    override suspend fun completeOAuthRegistrationPsychologist(
        tempToken: String, professionalLicense: String, yearsOfExperience: Int,
        collegiateRegion: String, university: String, graduationYear: Int,
        acceptsPrivacyPolicy: Boolean, licenseDocumentUri: String,
        diplomaDocumentUri: String, dniDocumentUri: String,
        specialties: String?, certificationDocumentUris: List<String>?
    ): Result<User> = Result.success(defaultUser())

    override suspend fun resetPassword(token: String, email: String, newPassword: String): Result<String> =
        Result.success("Contraseña actualizada")

    fun reset() {
        loginResult = Result.success(defaultUser())
        forgotPasswordResult = Result.success("Correo enviado")
        registerResult = Result.success(Pair("user-123", "test@softfocus.com"))
        loginCallCount = 0
    }

    companion object {
        fun defaultUser() = User(
            id = "user-123",
            email = "test@softfocus.com",
            fullName = "Test User",
            userType = UserType.GENERAL,
            isVerified = true,
            token = "fake-jwt-token"
        )
    }
}
