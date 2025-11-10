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
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 401) {
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        val jsonObject = org.json.JSONObject(errorBody)
                        val errorMessage = jsonObject.optString("message", "Invalid credentials")
                        Result.failure(Exception(errorMessage))
                    } else {
                        Result.failure(Exception("Invalid credentials"))
                    }
                } catch (jsonException: Exception) {
                    Result.failure(Exception("Invalid credentials"))
                }
            } else {
                Result.failure(e)
            }
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
            val licensePart = uriToMultipartBody(licenseDocumentUri, "licenseDocument")
                ?: return Result.failure(Exception("Failed to process license document"))
            val diplomaPart = uriToMultipartBody(diplomaDocumentUri, "diplomaDocument")
                ?: return Result.failure(Exception("Failed to process diploma document"))
            val dniPart = uriToMultipartBody(dniDocumentUri, "dniDocument")
                ?: return Result.failure(Exception("Failed to process DNI document"))

            val certificationParts = certificationDocumentUris?.mapNotNull { uri ->
                uriToMultipartBody(uri, "certificationDocuments")
            }

            val firstNamePart = textToMultipartPart(firstName, "firstName")
            val lastNamePart = textToMultipartPart(lastName, "lastName")
            val emailPart = textToMultipartPart(email, "email")
            val passwordPart = textToMultipartPart(password, "password")
            val professionalLicensePart = textToMultipartPart(professionalLicense, "professionalLicense")
            val yearsOfExperiencePart = textToMultipartPart(yearsOfExperience.toString(), "yearsOfExperience")
            val collegiateRegionPart = textToMultipartPart(collegiateRegion, "collegiateRegion")
            val universityPart = textToMultipartPart(university, "university")
            val graduationYearPart = textToMultipartPart(graduationYear.toString(), "graduationYear")
            val acceptsPrivacyPolicyPart = textToMultipartPart(acceptsPrivacyPolicy.toString(), "acceptsPrivacyPolicy")
            val specialtiesPart = specialties?.let { textToMultipartPart(it, "specialties") }

            val response = authService.registerPsychologist(
                firstName = firstNamePart,
                lastName = lastNamePart,
                email = emailPart,
                password = passwordPart,
                professionalLicense = professionalLicensePart,
                yearsOfExperience = yearsOfExperiencePart,
                collegiateRegion = collegiateRegionPart,
                university = universityPart,
                graduationYear = graduationYearPart,
                acceptsPrivacyPolicy = acceptsPrivacyPolicyPart,
                licenseDocument = licensePart,
                diplomaDocument = diplomaPart,
                dniDocument = dniPart,
                specialties = specialtiesPart,
                certificationDocuments = certificationParts
            )

            Result.success(Pair(response.userId, response.email))
        } catch (e: Exception) {
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
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 401) {
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        val jsonObject = org.json.JSONObject(errorBody)
                        val errorMessage = jsonObject.optString("message", "Your account is pending verification. Please wait for admin approval.")
                        Result.failure(Exception(errorMessage))
                    } else {
                        Result.failure(Exception("Your account is pending verification. Please wait for admin approval."))
                    }
                } catch (jsonException: Exception) {
                    Result.failure(Exception("Your account is pending verification. Please wait for admin approval."))
                }
            } else {
                Result.failure(e)
            }
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
            val tempTokenPart = textToMultipartPart(tempToken, "tempToken")
            val userTypePart = textToMultipartPart("General", "userType")
            val acceptsPrivacyPolicyPart = textToMultipartPart(acceptsPrivacyPolicy.toString(), "acceptsPrivacyPolicy")

            val response = authService.completeOAuthRegistration(
                tempToken = tempTokenPart,
                userType = userTypePart,
                acceptsPrivacyPolicy = acceptsPrivacyPolicyPart
            )

            val user = response.toDomain()
            Result.success(user)
        } catch (e: Exception) {
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
            val licensePart = uriToMultipartBody(licenseDocumentUri, "licenseDocument")
                ?: return Result.failure(Exception("Failed to process license document"))
            val diplomaPart = uriToMultipartBody(diplomaDocumentUri, "diplomaDocument")
                ?: return Result.failure(Exception("Failed to process diploma document"))
            val dniPart = uriToMultipartBody(dniDocumentUri, "dniDocument")
                ?: return Result.failure(Exception("Failed to process DNI document"))

            val certificationParts = certificationDocumentUris?.mapNotNull { uri ->
                uriToMultipartBody(uri, "certificationDocuments")
            }

            val tempTokenPart = textToMultipartPart(tempToken, "tempToken")
            val userTypePart = textToMultipartPart("Psychologist", "userType")
            val professionalLicensePart = textToMultipartPart(professionalLicense, "professionalLicense")
            val yearsOfExperiencePart = textToMultipartPart(yearsOfExperience.toString(), "yearsOfExperience")
            val collegiateRegionPart = textToMultipartPart(collegiateRegion, "collegiateRegion")
            val universityPart = textToMultipartPart(university, "university")
            val graduationYearPart = textToMultipartPart(graduationYear.toString(), "graduationYear")
            val acceptsPrivacyPolicyPart = textToMultipartPart(acceptsPrivacyPolicy.toString(), "acceptsPrivacyPolicy")
            val specialtiesPart = specialties?.let { textToMultipartPart(it, "specialties") }

            val response = authService.completeOAuthRegistrationPsychologist(
                tempToken = tempTokenPart,
                userType = userTypePart,
                acceptsPrivacyPolicy = acceptsPrivacyPolicyPart,
                professionalLicense = professionalLicensePart,
                yearsOfExperience = yearsOfExperiencePart,
                collegiateRegion = collegiateRegionPart,
                university = universityPart,
                graduationYear = graduationYearPart,
                licenseDocument = licensePart,
                diplomaDocument = diplomaPart,
                dniDocument = dniPart,
                specialties = specialtiesPart,
                certificationDocuments = certificationParts
            )

            val user = response.toDomain()
            Result.success(user)

        } catch (e: retrofit2.HttpException) {
            if (e.code() == 401) {
                Result.failure(PsychologistPendingVerificationException("Your account is pending verification"))
            } else {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    class PsychologistPendingVerificationException(message: String) : Exception(message)


    /**
     * Converts a text string to a MultipartBody.Part for form data.
     */
    private fun textToMultipartPart(text: String, partName: String): MultipartBody.Part {
        return MultipartBody.Part.createFormData(partName, text)
    }

    /**
     * Converts a URI string to a MultipartBody.Part for file upload.
     */
    private fun uriToMultipartBody(uriString: String, partName: String): MultipartBody.Part? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null

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

            if (!fileName.contains(".")) {
                val mimeType = context.contentResolver.getType(uri)
                val extension = when {
                    mimeType?.startsWith("image/") == true -> ".jpg"
                    mimeType == "application/pdf" -> ".pdf"
                    else -> ".pdf"
                }
                fileName += extension
            }

            val file = File(context.cacheDir, fileName)
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }

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
            null
        }
    }
}
