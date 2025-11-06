package com.softfocus.features.profile.domain.repositories

import android.net.Uri
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.profile.domain.models.PsychologistProfile

interface ProfileRepository {
    suspend fun getProfile(): Result<User>
    suspend fun getPsychologistCompleteProfile(): Result<PsychologistProfile>
    suspend fun updateProfile(
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
    ): Result<User>
    suspend fun updateProfessionalProfile(
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
    ): Result<PsychologistProfile>
}
