package com.softfocus.features.auth.presentation.login

import android.content.Intent
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.auth.data.remote.GoogleSignInManager
import com.softfocus.features.auth.data.remote.GoogleSignInRequiredException
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.repositories.AuthRepository
import com.softfocus.features.auth.domain.repositories.OAuthVerificationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginViewModel(
    private val repository: AuthRepository,
    private val googleSignInManager: GoogleSignInManager
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _psychologistPendingVerification = MutableStateFlow(false)
    val psychologistPendingVerification: StateFlow<Boolean> = _psychologistPendingVerification

    // OAuth data to pass to registration screen
    private val _oauthDataForRegistration = MutableStateFlow<OAuthVerificationData?>(null)
    val oauthDataForRegistration: StateFlow<OAuthVerificationData?> = _oauthDataForRegistration

    // Google Sign-In Intent to launch
    private val _googleSignInIntent = MutableStateFlow<Intent?>(null)
    val googleSignInIntent: StateFlow<Intent?> = _googleSignInIntent

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.login(email.value, password.value)
                .onSuccess { user ->
                    _user.value = user
                    _isLoading.value = false
                }
                .onFailure { error ->
                    // Check if it's a psychologist pending verification error
                    val errorMsg = error.message ?: "Error desconocido"
                    if (errorMsg.contains("pending verification", ignoreCase = true) ||
                        errorMsg.contains("wait for admin approval", ignoreCase = true)) {
                        _psychologistPendingVerification.value = true
                        _isLoading.value = false
                    } else {
                        _errorMessage.value = errorMsg
                        _isLoading.value = false
                    }
                }
        }
    }

    /**
     * Initiates Google Sign-In flow.
     * @param serverClientId The OAuth 2.0 server client ID from Google Cloud Console
     */
    fun signInWithGoogle(serverClientId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // Try to get cached account or request sign-in
            googleSignInManager.signIn(serverClientId)
                .onSuccess { googleSignInResult ->
                    // User was already signed in, verify with backend
                    verifyAndLoginWithGoogle(googleSignInResult.idToken)
                }
                .onFailure { error ->
                    if (error is GoogleSignInRequiredException) {
                        // Need to show Google Sign-In UI
                        val intent = googleSignInManager.getSignInIntent(serverClientId)
                        _googleSignInIntent.value = intent
                        _isLoading.value = false
                    } else {
                        _errorMessage.value = error.message ?: "Error al iniciar sesión con Google"
                        _isLoading.value = false
                    }
                }
        }
    }

    /**
     * Handles the result from Google Sign-In Activity.
     * Call this from the Composable after receiving activity result.
     */
    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            googleSignInManager.handleSignInResult(data)
                .onSuccess { googleSignInResult ->
                    // Sign-in successful, verify with backend
                    verifyAndLoginWithGoogle(googleSignInResult.idToken)
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Error al iniciar sesión con Google"
                    _isLoading.value = false
                }
        }
    }

    /**
     * Verifies Google token with backend and logs in if user exists.
     * If user doesn't exist, stores OAuth data to navigate to registration.
     */
    private suspend fun verifyAndLoginWithGoogle(idToken: String) {
        repository.verifyOAuth(provider = "Google", accessToken = idToken)
            .onSuccess { verificationData ->
                if (verificationData.needsRegistration) {
                    // User needs to register - store data and trigger navigation
                    _oauthDataForRegistration.value = verificationData
                    _isLoading.value = false
                } else {
                    // User already exists - backend returns JWT token directly in tempToken field
                    android.util.Log.d("LoginViewModel", "OAuth user exists, using JWT token from verification")

                    try {
                        // Decode JWT to extract user ID
                        val userId = extractUserIdFromJwt(verificationData.tempToken)

                        val user = User(
                            id = userId,
                            email = verificationData.email,
                            fullName = verificationData.fullName,
                            token = verificationData.tempToken,
                            userType = mapStringToUserType(verificationData.existingUserType ?: "General"),
                            isVerified = true // Existing users are already verified
                        )

                        _user.value = user
                        _isLoading.value = false
                    } catch (e: Exception) {
                        android.util.Log.e("LoginViewModel", "Error decoding JWT token", e)
                        _errorMessage.value = "Error al procesar la autenticación"
                        _isLoading.value = false
                    }
                }
            }
            .onFailure { error ->
                // Check if it's a psychologist pending verification error
                val errorMsg = error.message ?: "Error al verificar la cuenta de Google"
                if (errorMsg.contains("pending verification", ignoreCase = true) ||
                    errorMsg.contains("wait for admin approval", ignoreCase = true)) {
                    _psychologistPendingVerification.value = true
                    _isLoading.value = false
                } else {
                    _errorMessage.value = errorMsg
                    _isLoading.value = false
                }
            }
    }

    /**
     * Extracts the user ID from a JWT token by decoding the payload.
     */
    private fun extractUserIdFromJwt(token: String): String {
        try {
            // JWT structure: header.payload.signature
            val parts = token.split(".")
            if (parts.size != 3) {
                throw IllegalArgumentException("Invalid JWT token format")
            }

            // Decode the payload (second part)
            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            // Parse JSON payload
            val jsonPayload = JSONObject(decodedString)

            // Extract user ID from the "sub" claim (standard JWT claim for user ID)
            return jsonPayload.optString("sub") ?: jsonPayload.optString("user_id")
                ?: throw IllegalArgumentException("User ID not found in JWT token")
        } catch (e: Exception) {
            android.util.Log.e("LoginViewModel", "Error extracting user ID from JWT", e)
            throw e
        }
    }

    private fun mapStringToUserType(role: String): com.softfocus.features.auth.domain.models.UserType {
        return when (role.uppercase()) {
            "GENERAL" -> com.softfocus.features.auth.domain.models.UserType.GENERAL
            "PSYCHOLOGIST" -> com.softfocus.features.auth.domain.models.UserType.PSYCHOLOGIST
            "PATIENT" -> com.softfocus.features.auth.domain.models.UserType.PATIENT
            "ADMIN" -> com.softfocus.features.auth.domain.models.UserType.ADMIN
            else -> com.softfocus.features.auth.domain.models.UserType.GENERAL
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearOAuthData() {
        _oauthDataForRegistration.value = null
    }

    fun clearGoogleSignInIntent() {
        _googleSignInIntent.value = null
    }

    fun clearPendingVerification() {
        _psychologistPendingVerification.value = false
    }

    /**
     * Sets user from OAuth registration for auto-login.
     * Call this after successful OAuth registration to log in the user automatically.
     */
    fun setUserFromOAuthRegistration(user: User) {
        _user.value = user
        _isLoading.value = false
    }
}
