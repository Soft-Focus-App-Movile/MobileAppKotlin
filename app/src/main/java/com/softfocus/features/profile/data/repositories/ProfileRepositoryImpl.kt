package com.softfocus.features.profile.data.repositories

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.profile.data.remote.ProfileService
import com.softfocus.features.profile.domain.repositories.ProfileRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService,
    private val userSession: UserSession,
    private val context: Context
) : ProfileRepository {

    override suspend fun getProfile(): Result<User> {
        return try {
            val response = profileService.getProfile()

            if (response.isSuccessful && response.body() != null) {
                val currentToken = userSession.getUser()?.token
                val user = response.body()!!.toDomain(currentToken)
                userSession.saveUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Error al obtener perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPsychologistCompleteProfile(): Result<com.softfocus.features.profile.domain.models.PsychologistProfile> {
        return try {
            val response = profileService.getPsychologistCompleteProfile()

            if (response.isSuccessful && response.body() != null) {
                val psychologistProfile = response.body()!!.toDomain()
                Result.success(psychologistProfile)
            } else {
                Result.failure(Exception("Error al obtener perfil de psicólogo: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(
        firstName: String?,
        lastName: String?,
        dateOfBirth: String?,
        gender: String?,
        phone: String?,
        bio: String?,
        country: String?,
        city: String?,
        interests: List<String>?,
        mentalHealthGoals: List<String>?,
        emailNotifications: Boolean?,
        pushNotifications: Boolean?,
        isProfilePublic: Boolean?,
        profileImageUri: Uri?
    ): Result<User> {
        return try {
            // Always use multipart/form-data as backend only accepts [FromForm]
            val imagePart = if (profileImageUri != null) {
                prepareImagePart(profileImageUri)
            } else {
                null
            }

            val interestsParts = interests?.map { interest ->
                MultipartBody.Part.createFormData("Interests", interest)
            }

            val mentalHealthGoalsParts = mentalHealthGoals?.map { goal ->
                MultipartBody.Part.createFormData("MentalHealthGoals", goal)
            }

            val response = profileService.updateProfileWithImage(
                firstName = firstName?.toRequestBody("text/plain".toMediaTypeOrNull()),
                lastName = lastName?.toRequestBody("text/plain".toMediaTypeOrNull()),
                dateOfBirth = dateOfBirth?.toRequestBody("text/plain".toMediaTypeOrNull()),
                gender = gender?.toRequestBody("text/plain".toMediaTypeOrNull()),
                phone = phone?.toRequestBody("text/plain".toMediaTypeOrNull()),
                bio = bio?.toRequestBody("text/plain".toMediaTypeOrNull()),
                country = country?.toRequestBody("text/plain".toMediaTypeOrNull()),
                city = city?.toRequestBody("text/plain".toMediaTypeOrNull()),
                interests = interestsParts,
                mentalHealthGoals = mentalHealthGoalsParts,
                emailNotifications = emailNotifications?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                pushNotifications = pushNotifications?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                isProfilePublic = isProfilePublic?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                profileImage = imagePart
            )

            if (response.isSuccessful && response.body() != null) {
                val currentToken = userSession.getUser()?.token
                val user = response.body()!!.toDomain(currentToken)
                userSession.saveUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Error al actualizar perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun prepareImagePart(imageUri: Uri): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val tempFile = File(context.cacheDir, getFileName(imageUri) ?: "profile_image.jpg")

            inputStream?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("profileImage", tempFile.name, requestFile)
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }
        return fileName
    }

    override suspend fun updateProfessionalProfile(
        professionalBio: String?,
        isAcceptingNewPatients: Boolean?,
        maxPatientsCapacity: Int?,
        targetAudience: List<String>?,
        languages: List<String>?,
        businessName: String?,
        businessAddress: String?,
        bankAccount: String?,
        paymentMethods: String?,
        isProfileVisibleInDirectory: Boolean?,
        allowsDirectMessages: Boolean?
    ): Result<com.softfocus.features.profile.domain.models.PsychologistProfile> {
        return try {
            android.util.Log.d("ProfileRepository", "updateProfessionalProfile called")
            val professionalData = mutableMapOf<String, Any?>()

            professionalBio?.let { professionalData["professionalBio"] = it }
            isAcceptingNewPatients?.let { professionalData["isAcceptingNewPatients"] = it }
            maxPatientsCapacity?.let { professionalData["maxPatientsCapacity"] = it }
            targetAudience?.let { professionalData["targetAudience"] = it }
            languages?.let { professionalData["languages"] = it }
            businessName?.let { professionalData["businessName"] = it }
            businessAddress?.let { professionalData["businessAddress"] = it }
            bankAccount?.let { professionalData["bankAccount"] = it }
            paymentMethods?.let { professionalData["paymentMethods"] = it }
            isProfileVisibleInDirectory?.let { professionalData["isProfileVisibleInDirectory"] = it }
            allowsDirectMessages?.let { professionalData["allowsDirectMessages"] = it }

            android.util.Log.d("ProfileRepository", "Professional data to send: $professionalData")
            val response = profileService.updateProfessionalProfile(professionalData)
            android.util.Log.d("ProfileRepository", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")

            if (response.isSuccessful && response.body() != null) {
                val psychologistProfile = response.body()!!.toDomain()
                Result.success(psychologistProfile)
            } else {
                Result.failure(Exception("Error al actualizar perfil profesional: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
