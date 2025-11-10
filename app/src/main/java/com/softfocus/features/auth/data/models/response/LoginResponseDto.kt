package com.softfocus.features.auth.data.models.response

import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.models.UserType

/**
 * Response DTO for login matching backend's actual response structure.
 * Backend returns: { user: {...}, token: "...", expiresAt: "...", tokenType: "..." }
 */
data class LoginResponseDto(
    val user: UserDataDto,
    val token: String,
    val expiresAt: String,
    val tokenType: String = "Bearer"
) {
    fun toDomain(): User {
        val userType = mapRoleToUserType(user.role)
        return User(
            id = user.id,
            email = user.email,
            userType = userType,
            // Use backend's isVerified if available, otherwise default to true for general users
            // and false for psychologists (pending verification)
            isVerified = user.isVerified ?: (userType != UserType.PSYCHOLOGIST),
            token = token,
            fullName = user.fullName
        )
    }

    private fun mapRoleToUserType(role: String): UserType {
        return when (role.uppercase()) {
            "GENERAL" -> UserType.GENERAL
            "PSYCHOLOGIST" -> UserType.PSYCHOLOGIST
            "PATIENT" -> UserType.PATIENT
            "ADMIN" -> UserType.ADMIN
            else -> UserType.GENERAL
        }
    }
}

data class UserDataDto(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String,
    val profileImageUrl: String? = null,
    val lastLogin: String? = null,
    val roleDisplay: String? = null,
    val capabilities: UserCapabilitiesDto? = null,
    val isVerified: Boolean? = null // For psychologists, indicates if account is verified
)

data class UserCapabilitiesDto(
    val canManageUsers: Boolean = false,
    val canProvideTherapy: Boolean = false,
    val canAccessPremiumFeatures: Boolean = false,
    val isAdmin: Boolean = false,
    val isPsychologist: Boolean = false,
    val isGeneral: Boolean = false
)
