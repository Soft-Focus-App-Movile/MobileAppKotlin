package com.softfocus.fakes.profile

import android.net.Uri
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.profile.domain.models.AssignedPsychologist
import com.softfocus.features.profile.domain.models.PsychologistProfile
import com.softfocus.features.profile.domain.repositories.ProfileRepository

class FakeProfileRepository : ProfileRepository {

    var getProfileResult: Result<User> = Result.success(defaultUser())
    var updateProfileResult: Result<User> = Result.success(defaultUser())
    var updateProfessionalProfileResult: Result<Unit> = Result.success(Unit)
    var getAssignedPsychologistResult: Result<AssignedPsychologist?> = Result.success(defaultPsychologist())
    var getPsychologistCompleteProfileResult: Result<PsychologistProfile> = Result.failure(NotImplementedError())

    var getProfileCallCount = 0
    var updateProfileCallCount = 0

    override suspend fun getProfile(): Result<User> {
        getProfileCallCount++
        return getProfileResult
    }

    override suspend fun updateProfile(
        firstName: String?, lastName: String?, dateOfBirth: String?,
        gender: String?, phone: String?, bio: String?, country: String?,
        city: String?, interests: List<String>?, mentalHealthGoals: List<String>?,
        emailNotifications: Boolean?, pushNotifications: Boolean?,
        isProfilePublic: Boolean?, profileImageUri: Uri?
    ): Result<User> {
        updateProfileCallCount++
        return updateProfileResult
    }

    override suspend fun updateProfessionalProfile(
        professionalBio: String?, isAcceptingNewPatients: Boolean?,
        maxPatientsCapacity: Int?, targetAudience: List<String>?,
        languages: List<String>?, businessName: String?,
        businessAddress: String?, bankAccount: String?,
        paymentMethods: String?, isProfileVisibleInDirectory: Boolean?,
        allowsDirectMessages: Boolean?
    ): Result<Unit> = updateProfessionalProfileResult

    override suspend fun getAssignedPsychologist(): Result<AssignedPsychologist?> =
        getAssignedPsychologistResult

    override suspend fun getPsychologistCompleteProfile(): Result<PsychologistProfile> =
        getPsychologistCompleteProfileResult

    fun reset() {
        getProfileResult = Result.success(defaultUser())
        updateProfileResult = Result.success(defaultUser())
        updateProfessionalProfileResult = Result.success(Unit)
        getAssignedPsychologistResult = Result.success(defaultPsychologist())
        getProfileCallCount = 0
        updateProfileCallCount = 0
    }

    companion object {
        fun defaultUser() = User(
            id = "user-123",
            email = "test@softfocus.com",
            fullName = "Juan Pérez",
            firstName = "Juan",
            lastName = "Pérez",
            userType = UserType.GENERAL,
            isVerified = true,
            token = "fake-jwt-token",
            bio = "Bio de prueba",
            country = "Perú",
            city = "Lima"
        )

        fun defaultPsychologist() = AssignedPsychologist(
            id = "psych-123",
            fullName = "Dra. María García",
            profileImageUrl = null,
            professionalBio = "Psicóloga clínica con 10 años de experiencia",
            specialties = listOf("Ansiedad", "Depresión")
        )
    }
}
