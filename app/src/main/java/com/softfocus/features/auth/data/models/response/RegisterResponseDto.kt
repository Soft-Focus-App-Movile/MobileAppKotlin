package com.softfocus.features.auth.data.models.response

import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.models.UserType

/**
 * Response DTO for user registration.
 * Backend returns: { message: "Registration successful", userId: "xxx", email: "xxx" }
 * Note: No token is provided. User must login after registration to get authentication token.
 */
data class RegisterResponseDto(
    val message: String,
    val userId: String,
    val email: String
) {
    fun toDomain(): User {
        // Registration doesn't return a token, so we create a minimal User
        // The app will automatically login after registration to get the full user data
        return User(
            id = userId,
            email = email,
            userType = UserType.GENERAL, // Temporary, will be set on login
            isVerified = false,
            token = "", // Empty, will be set on login
            fullName = "" // Not returned by registration endpoint
        )
    }
}
