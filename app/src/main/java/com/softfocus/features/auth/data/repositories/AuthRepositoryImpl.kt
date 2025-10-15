package com.softfocus.features.auth.data.repositories

import android.content.Context
import android.net.Uri
import com.softfocus.features.auth.data.models.request.LoginRequestDto
import com.softfocus.features.auth.data.models.request.OAuthLoginRequestDto
import com.softfocus.features.auth.data.models.request.OAuthVerifyRequestDto
import com.softfocus.features.auth.data.models.request.RegisterGeneralUserRequestDto
import com.softfocus.features.auth.data.models.request.RegisterRequestDto
import com.softfocus.features.auth.data.models.request.SocialLoginRequestDto
import com.softfocus.features.auth.data.remote.AuthService
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.repositories.AuthRepository
import com.softfocus.features.auth.domain.repositories.OAuthVerificationData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * Implementation of AuthRepository interface.
 *
 * This class provides the concrete implementation for authentication operations,
 * handling HTTP requests through Retrofit and mapping DTOs to domain models.
 *
 * @property authService Retrofit service for making API calls
 * @property context Android context for accessing content resolver
 */
class AuthRepositoryImpl(
    private val authService: AuthService,
    private val context: Context
) : AuthRepository {

    /**
     * Authenticates a user with email and password.
     *
     * @param email User's email address
     * @param password User's password
     * @return Result containing the authenticated User on success, or an exception on failure
     */
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val request = LoginRequestDto(email = email, password = password)
            val response = authService.login(request)
            val user = response.toDomain()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registers a new general user in the platform.
     */
    override suspend fun registerGeneralUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        acceptsPrivacyPolicy: Boolean
    ): Result<Pair<String, String>> {
        return try {
            val request = RegisterGeneralUserRequestDto(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                acceptsPrivacyPolicy = acceptsPrivacyPolicy
            )
            val response = authService.registerGeneralUser(request)
            Result.success(Pair(response.userId, response.email))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registers a new psychologist in the platform.
     * Requires document uploads.
     */
    override suspend fun registerPsychologist(
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
        specialties: String?,
        certificationDocumentUris: List<String>?
    ): Result<Pair<String, String>> {
        return try {
            android.util.Log.d("AuthRepositoryImpl", "Starting psychologist registration")
            android.util.Log.d("AuthRepositoryImpl", "firstName: $firstName, lastName: $lastName, email: $email")
            android.util.Log.d("AuthRepositoryImpl", "professionalLicense: $professionalLicense, years: $yearsOfExperience")
            android.util.Log.d("AuthRepositoryImpl", "region: $collegiateRegion, university: $university, year: $graduationYear")
            android.util.Log.d("AuthRepositoryImpl", "specialties: $specialties")
            android.util.Log.d("AuthRepositoryImpl", "licenseUri: $licenseDocumentUri")
            android.util.Log.d("AuthRepositoryImpl", "diplomaUri: $diplomaDocumentUri")
            android.util.Log.d("AuthRepositoryImpl", "dniUri: $dniDocumentUri")

            // Convert URIs to MultipartBody.Part
            val licensePart = uriToMultipartBody(licenseDocumentUri, "licenseDocument")
                ?: return Result.failure(Exception("Failed to process license document"))
            val diplomaPart = uriToMultipartBody(diplomaDocumentUri, "diplomaDocument")
                ?: return Result.failure(Exception("Failed to process diploma document"))
            val dniPart = uriToMultipartBody(dniDocumentUri, "dniDocument")
                ?: return Result.failure(Exception("Failed to process DNI document"))

            android.util.Log.d("AuthRepositoryImpl", "Documents processed successfully")

            // Process certification documents (optional)
            val certificationParts = certificationDocumentUris?.mapNotNull { uri ->
                uriToMultipartBody(uri, "certificationDocuments")
            }

            // Prepare text fields as RequestBody
            val firstNameBody = firstName.toRequestBody("text/plain".toMediaTypeOrNull())
            val lastNameBody = lastName.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
            val professionalLicenseBody = professionalLicense.toRequestBody("text/plain".toMediaTypeOrNull())
            val yearsOfExperienceBody = yearsOfExperience.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val collegiateRegionBody = collegiateRegion.toRequestBody("text/plain".toMediaTypeOrNull())
            val universityBody = university.toRequestBody("text/plain".toMediaTypeOrNull())
            val graduationYearBody = graduationYear.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val acceptsPrivacyPolicyBody = acceptsPrivacyPolicy.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val specialtiesBody = specialties?.toRequestBody("text/plain".toMediaTypeOrNull())

            android.util.Log.d("AuthRepositoryImpl", "Calling registerPsychologist API...")

            val response = authService.registerPsychologist(
                firstName = firstNameBody,
                lastName = lastNameBody,
                email = emailBody,
                password = passwordBody,
                professionalLicense = professionalLicenseBody,
                yearsOfExperience = yearsOfExperienceBody,
                collegiateRegion = collegiateRegionBody,
                university = universityBody,
                graduationYear = graduationYearBody,
                acceptsPrivacyPolicy = acceptsPrivacyPolicyBody,
                licenseDocument = licensePart,
                diplomaDocument = diplomaPart,
                dniDocument = dniPart,
                specialties = specialtiesBody,
                certificationDocuments = certificationParts
            )

            android.util.Log.d("AuthRepositoryImpl", "Registration successful: ${response.userId}")
            Result.success(Pair(response.userId, response.email))
        } catch (e: Exception) {
            android.util.Log.e("AuthRepositoryImpl", "Registration failed: ${e.message}", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Authenticates a user using a social provider (Google, Facebook, etc.).
     *
     * @param provider Name of the social provider (e.g., "GOOGLE", "FACEBOOK")
     * @param token Authentication token from the social provider
     * @return Result containing the authenticated User on success, or an exception on failure
     */
    override suspend fun socialLogin(provider: String, token: String): Result<User> {
        return try {
            val request = SocialLoginRequestDto(provider = provider, token = token)
            val response = authService.socialLogin(request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verifies OAuth token with the backend.
     */
    override suspend fun verifyOAuth(provider: String, accessToken: String): Result<OAuthVerificationData> {
        return try {
            val request = OAuthVerifyRequestDto(
                provider = provider,
                accessToken = accessToken
            )
            val response = authService.verifyOAuth(request)

            val data = OAuthVerificationData(
                email = response.email,
                fullName = response.fullName,
                provider = response.provider,
                tempToken = response.tempToken,
                needsRegistration = response.needsRegistration,
                existingUserType = response.existingUserType
            )
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs in existing user via OAuth.
     */
    override suspend fun oauthLogin(provider: String, token: String): Result<User> {
        return try {
            val request = OAuthLoginRequestDto(
                provider = provider,
                token = token
            )
            val response = authService.oauthLogin(request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Completes registration for a new OAuth general user.
     * Returns authenticated User with JWT token for auto-login.
     */
    override suspend fun completeOAuthRegistrationGeneral(
        tempToken: String,
        acceptsPrivacyPolicy: Boolean
    ): Result<User> {
        return try {
            val tempTokenBody = tempToken.toRequestBody("text/plain".toMediaTypeOrNull())
            val userTypeBody = "General".toRequestBody("text/plain".toMediaTypeOrNull())
            val acceptsPrivacyPolicyBody = acceptsPrivacyPolicy.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = authService.completeOAuthRegistration(
                tempToken = tempTokenBody,
                userType = userTypeBody,
                acceptsPrivacyPolicy = acceptsPrivacyPolicyBody
            )

            val user = response.toDomain()
            Result.success(user)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepositoryImpl", "OAuth general registration failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Completes registration for a new OAuth psychologist user.
     */
    override suspend fun completeOAuthRegistrationPsychologist(
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
        specialties: String?,
        certificationDocumentUris: List<String>?
    ): Result<User> {
        return try {
            android.util.Log.d("AuthRepositoryImpl", "Starting OAuth psychologist registration")

            // Convert URIs to MultipartBody.Part
            val licensePart = uriToMultipartBody(licenseDocumentUri, "licenseDocument")
                ?: return Result.failure(Exception("Failed to process license document"))
            val diplomaPart = uriToMultipartBody(diplomaDocumentUri, "diplomaDocument")
                ?: return Result.failure(Exception("Failed to process diploma document"))
            val dniPart = uriToMultipartBody(dniDocumentUri, "dniDocument")
                ?: return Result.failure(Exception("Failed to process DNI document"))

            // Process certification documents (optional)
            val certificationParts = certificationDocumentUris?.mapNotNull { uri ->
                uriToMultipartBody(uri, "certificationDocuments")
            }

            // Prepare text fields as RequestBody
            val tempTokenBody = tempToken.toRequestBody("text/plain".toMediaTypeOrNull())
            val userTypeBody = "Psychologist".toRequestBody("text/plain".toMediaTypeOrNull())
            val professionalLicenseBody = professionalLicense.toRequestBody("text/plain".toMediaTypeOrNull())
            val yearsOfExperienceBody = yearsOfExperience.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val collegiateRegionBody = collegiateRegion.toRequestBody("text/plain".toMediaTypeOrNull())
            val universityBody = university.toRequestBody("text/plain".toMediaTypeOrNull())
            val graduationYearBody = graduationYear.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val acceptsPrivacyPolicyBody = acceptsPrivacyPolicy.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val specialtiesBody = specialties?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = authService.completeOAuthRegistrationPsychologist(
                tempToken = tempTokenBody,
                userType = userTypeBody,
                acceptsPrivacyPolicy = acceptsPrivacyPolicyBody,
                professionalLicense = professionalLicenseBody,
                yearsOfExperience = yearsOfExperienceBody,
                collegiateRegion = collegiateRegionBody,
                university = universityBody,
                graduationYear = graduationYearBody,
                licenseDocument = licensePart,
                diplomaDocument = diplomaPart,
                dniDocument = dniPart,
                specialties = specialtiesBody,
                certificationDocuments = certificationParts
            )

            android.util.Log.d("AuthRepositoryImpl", "OAuth psychologist registration successful: ${response.user.id}")

            val user = response.toDomain()
            Result.success(user)

        } catch (e: Exception) {
            android.util.Log.e("AuthRepositoryImpl", "OAuth psychologist registration failed: ${e.message}", e)
            Result.failure(e)
        }
    }


    /**
     * Converts a URI string to a MultipartBody.Part for file upload.
     */
    private fun uriToMultipartBody(uriString: String, partName: String): MultipartBody.Part? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null

            // Get the original filename and extension from the URI
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            var fileName = "document_${System.currentTimeMillis()}"
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        fileName = it.getString(displayNameIndex)
                    }
                }
            }

            // If no extension, try to get mime type and add appropriate extension
            if (!fileName.contains(".")) {
                val mimeType = context.contentResolver.getType(uri)
                val extension = when {
                    mimeType?.startsWith("image/") == true -> ".jpg"
                    mimeType == "application/pdf" -> ".pdf"
                    else -> ".pdf" // Default to PDF
                }
                fileName += extension
            }

            // Create temp file with proper extension
            val file = File(context.cacheDir, fileName)
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            // Set proper media type based on file extension
            val mediaType = when {
                fileName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
                fileName.endsWith(".jpg", ignoreCase = true) ||
                fileName.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
                fileName.endsWith(".png", ignoreCase = true) -> "image/png"
                else -> "application/octet-stream"
            }.toMediaTypeOrNull()

            val requestBody = file.asRequestBody(mediaType)
            MultipartBody.Part.createFormData(partName, file.name, requestBody)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepositoryImpl", "Error creating multipart body: ${e.message}", e)
            null
        }
    }
}
