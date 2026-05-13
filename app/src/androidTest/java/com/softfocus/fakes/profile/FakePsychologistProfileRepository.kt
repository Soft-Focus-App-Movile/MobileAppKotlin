package com.softfocus.fakes.profile

import android.net.Uri
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.profile.domain.models.AssignedPsychologist
import com.softfocus.features.profile.domain.models.PsychologistProfile
import com.softfocus.features.profile.domain.repositories.ProfileRepository

class FakePsychologistProfileRepository : ProfileRepository {

    var getProfileResult: Result<User> = Result.success(defaultPsychologistUser())
    var updateProfileResult: Result<User> = Result.success(defaultPsychologistUser())
    var updateProfessionalProfileResult: Result<Unit> = Result.success(Unit)
    var getPsychologistCompleteProfileResult: Result<PsychologistProfile> =
        Result.success(defaultPsychologistProfile())

    override suspend fun getProfile(): Result<User> = getProfileResult

    override suspend fun updateProfile(
        firstName: String?, lastName: String?, dateOfBirth: String?,
        gender: String?, phone: String?, bio: String?, country: String?,
        city: String?, interests: List<String>?, mentalHealthGoals: List<String>?,
        emailNotifications: Boolean?, pushNotifications: Boolean?,
        isProfilePublic: Boolean?, profileImageUri: Uri?
    ): Result<User> = updateProfileResult

    override suspend fun updateProfessionalProfile(
        professionalBio: String?, isAcceptingNewPatients: Boolean?,
        maxPatientsCapacity: Int?, targetAudience: List<String>?,
        languages: List<String>?, businessName: String?,
        businessAddress: String?, bankAccount: String?,
        paymentMethods: String?, isProfileVisibleInDirectory: Boolean?,
        allowsDirectMessages: Boolean?
    ): Result<Unit> = updateProfessionalProfileResult

    override suspend fun getAssignedPsychologist(): Result<AssignedPsychologist?> =
        Result.success(null)

    override suspend fun getPsychologistCompleteProfile(): Result<PsychologistProfile> =
        getPsychologistCompleteProfileResult

    fun reset() {
        getProfileResult = Result.success(defaultPsychologistUser())
        updateProfileResult = Result.success(defaultPsychologistUser())
        updateProfessionalProfileResult = Result.success(Unit)
        getPsychologistCompleteProfileResult = Result.success(defaultPsychologistProfile())
    }

    companion object {
        fun defaultPsychologistUser() = User(
            id = "psych-user-123",
            email = "psych@softfocus.com",
            fullName = "Dra. Ana Torres",
            firstName = "Ana",
            lastName = "Torres",
            userType = UserType.PSYCHOLOGIST,
            isVerified = true,
            token = "fake-psych-jwt-token"
        )

        fun defaultPsychologistProfile() = PsychologistProfile(
            id = "psych-user-123",
            email = "psych@softfocus.com",
            fullName = "Dra. Ana Torres",
            firstName = "Ana",
            lastName = "Torres",
            userType = "PSYCHOLOGIST",
            licenseNumber = "CMP-12345",
            professionalCollege = "Colegio de Psicólogos del Perú",
            specialties = listOf("Ansiedad", "Depresión"),
            yearsOfExperience = 8,
            isVerified = true,
            professionalBio = "Psicóloga clínica especializada en trastornos del estado de ánimo"
        )
    }
}
