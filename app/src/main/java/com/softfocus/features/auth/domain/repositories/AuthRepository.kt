package com.softfocus.features.auth.domain.repositories

import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.models.UserType

/**
 * Repository interface for authentication operations.
 *
 * This interface defines the contract for authentication-related operations
 * without any implementation details. The actual implementation will be
 * provided in the data layer.
 *
 * All methods use Result<T> to handle success and failure cases in a functional way.
 */
interface AuthRepository {

    /**
     * Authenticates a user with email and password.
     *
     * @param email User's email address
     * @param password User's password
     * @return Result containing the authenticated User on success, or an exception on failure
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Registers a new general user in the platform.
     *
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email address
     * @param password User's password
     * @param acceptsPrivacyPolicy Whether the user accepts the privacy policy
     * @return Result containing userId and email on success
     */
    suspend fun registerGeneralUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        acceptsPrivacyPolicy: Boolean
    ): Result<Pair<String, String>> // Returns (userId, email)

    /**
     * Registers a new psychologist in the platform.
     * Requires document uploads.
     *
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email address
     * @param password User's password
     * @param professionalLicense Professional license number
     * @param yearsOfExperience Years of professional experience
     * @param collegiateRegion College region
     * @param university University name
     * @param graduationYear Year of graduation
     * @param acceptsPrivacyPolicy Whether the user accepts the privacy policy
     * @param licenseDocumentUri URI of license document file
     * @param diplomaDocumentUri URI of diploma certificate file
     * @param dniDocumentUri URI of DNI/identity document file
     * @param specialties Comma-separated list of specialties (optional)
     * @param certificationDocumentUris List of URIs for additional certificates (optional)
     * @return Result containing userId and email on success (account pending verification)
     */
    suspend fun registerPsychologist(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        professionalLicense: String,
        yearsOfExperience: Int,
        collegiateRegion: String,
        university: String,
        graduationYear: Int,
        acceptsPrivacyPolicy: Boolean,
        licenseDocumentUri: String,
        diplomaDocumentUri: String,
        dniDocumentUri: String,
        specialties: String? = null,
        certificationDocumentUris: List<String>? = null
    ): Result<Pair<String, String>> // Returns (userId, email)

    /**
     * Authenticates a user using a social provider (Google, Facebook, etc.).
     *
     * @param provider Name of the social provider (e.g., "GOOGLE", "FACEBOOK")
     * @param token Authentication token from the social provider
     * @return Result containing the authenticated User on success, or an exception on failure
     */
    suspend fun socialLogin(provider: String, token: String): Result<User>

    /**
     * Verifies OAuth token and returns verification result.
     *
     * @param provider OAuth provider ("Google" or "Facebook")
     * @param accessToken Access token from OAuth provider
     * @return Result containing OAuth verification data (email, fullName, needsRegistration, etc.)
     */
    suspend fun verifyOAuth(provider: String, accessToken: String): Result<OAuthVerificationData>

    /**
     * Logs in an existing user via OAuth.
     *
     * @param provider OAuth provider ("Google" or "Facebook")
     * @param token Token from OAuth provider
     * @return Result containing the authenticated User on success
     */
    suspend fun oauthLogin(provider: String, token: String): Result<User>

    /**
     * Completes registration for a new OAuth general user.
     * Use this after verifyOAuth returns needsRegistration = true.
     *
     * @param tempToken Temporary token received from verifyOAuth
     * @param acceptsPrivacyPolicy Whether the user accepts the privacy policy
     * @return Result containing authenticated User with JWT token (auto-login)
     */
    suspend fun completeOAuthRegistrationGeneral(
        tempToken: String,
        acceptsPrivacyPolicy: Boolean
    ): Result<User>

    /**
     * Completes registration for a new OAuth psychologist user.
     * Use this after verifyOAuth returns needsRegistration = true.
     * Requires professional data and document uploads.
     *
     * @param tempToken Temporary token received from verifyOAuth
     * @param professionalLicense Professional license number
     * @param yearsOfExperience Years of professional experience
     * @param collegiateRegion College region
     * @param university University name
     * @param graduationYear Year of graduation
     * @param acceptsPrivacyPolicy Whether the user accepts the privacy policy
     * @param licenseDocumentUri URI of license document file
     * @param diplomaDocumentUri URI of diploma certificate file
     * @param dniDocumentUri URI of DNI/identity document file
     * @param specialties Comma-separated list of specialties (optional)
     * @param certificationDocumentUris List of URIs for additional certificates (optional)
     * @return Result containing authenticated User with JWT token (auto-login, pending verification for psychologists)
     */
    suspend fun completeOAuthRegistrationPsychologist(
        tempToken: String,
        professionalLicense: String,
        yearsOfExperience: Int,
        collegiateRegion: String,
        university: String,
        graduationYear: Int,
        acceptsPrivacyPolicy: Boolean,
        licenseDocumentUri: String,
        diplomaDocumentUri: String,
        dniDocumentUri: String,
        specialties: String? = null,
        certificationDocumentUris: List<String>? = null
    ): Result<User>

    /**
     * Requests a password reset for the given email address.
     * Sends a reset code to the user's email if registered.
     *
     * @param email User's email address
     * @return Result containing success message
     */
    suspend fun forgotPassword(email: String): Result<String>

    /**
     * Resets the user's password using the reset token.
     *
     * @param token Reset token received via email
     * @param email User's email address
     * @param newPassword New password to set
     * @return Result containing success message
     */
    suspend fun resetPassword(token: String, email: String, newPassword: String): Result<String>
}

/**
 * Data class representing OAuth verification result.
 */
data class OAuthVerificationData(
    val email: String,
    val fullName: String,
    val provider: String,
    val tempToken: String,
    val needsRegistration: Boolean,
    val existingUserType: String?
)