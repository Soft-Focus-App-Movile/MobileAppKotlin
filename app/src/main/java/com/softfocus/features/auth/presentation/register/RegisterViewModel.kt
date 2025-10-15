package com.softfocus.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.auth.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for user registration.
 * Handles three separate registration flows:
 * 1. General User: Simple registration with just firstName, lastName, email, password
 * 2. Psychologist: Complex registration with professional data and document uploads
 * 3. OAuth User: Registration after OAuth verification (no password required) - auto-login with User+JWT
 */
class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _userType = MutableStateFlow<UserType?>(null)
    val userType: StateFlow<UserType?> = _userType


    private val _oauthTempToken = MutableStateFlow<String?>(null)
    val oauthTempToken: StateFlow<String?> = _oauthTempToken


    private val _registrationResultRegular = MutableStateFlow<Pair<String, String>?>(null)
    val registrationResultRegular: StateFlow<Pair<String, String>?> = _registrationResultRegular


    private val _registrationResultOAuth = MutableStateFlow<User?>(null)
    val registrationResultOAuth: StateFlow<User?> = _registrationResultOAuth

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    fun updateUserType(value: UserType) {
        _userType.value = value
    }

    fun setOAuthTempToken(token: String) {
        _oauthTempToken.value = token
    }

    fun registerGeneralUser(
        firstName: String,
        lastName: String,
        acceptsPrivacyPolicy: Boolean
    ) {

        val tempToken = _oauthTempToken.value

        if (tempToken != null) {
            // OAuth registration - no password required
            registerGeneralUserOAuth(tempToken, acceptsPrivacyPolicy)
            return
        }

        // Regular registration with password
        if (password.value != confirmPassword.value) {
            _errorMessage.value = "Las contraseñas no coinciden"
            return
        }

        if (!acceptsPrivacyPolicy) {
            _errorMessage.value = "Debes aceptar la política de privacidad"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.registerGeneralUser(
                firstName = firstName,
                lastName = lastName,
                email = email.value,
                password = password.value,
                acceptsPrivacyPolicy = acceptsPrivacyPolicy
            ).onSuccess { result ->
                _registrationResultRegular.value = result
                _isLoading.value = false
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error desconocido al registrar"
                _isLoading.value = false
            }
        }
    }

    /**
     * Registers a general user via OAuth (no password required).
     * Returns User with JWT token for auto-login.
     */
    private fun registerGeneralUserOAuth(
        tempToken: String,
        acceptsPrivacyPolicy: Boolean
    ) {
        if (!acceptsPrivacyPolicy) {
            _errorMessage.value = "Debes aceptar la política de privacidad"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.completeOAuthRegistrationGeneral(
                tempToken = tempToken,
                acceptsPrivacyPolicy = acceptsPrivacyPolicy
            ).onSuccess { user ->
                // OAuth registration returns User with JWT token for auto-login
                _registrationResultOAuth.value = user
                _isLoading.value = false
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error desconocido al registrar con OAuth"
                _isLoading.value = false
            }
        }
    }

    /**
     * Register a psychologist with all required documents.
     * Flow: Register with documents -> Navigate to Account Review Screen
     *
     * Note: Documents are uploaded directly in registration, no separate verification step needed
     */
    fun registerPsychologist(
        firstName: String,
        lastName: String,
        professionalLicense: String,
        yearsOfExperience: Int,
        collegiateRegion: String,
        university: String,
        graduationYear: Int,
        acceptsPrivacyPolicy: Boolean,
        licenseDocumentUri: String,
        diplomaDocumentUri: String,
        dniDocumentUri: String,
        specialties: String? = null, // comma-separated
        certificationDocumentUris: List<String>? = null
    ) {
        // Check if this is OAuth registration (tempToken present)
        val tempToken = _oauthTempToken.value

        if (tempToken != null) {
            // OAuth registration - no password required
            registerPsychologistOAuth(
                tempToken = tempToken,
                professionalLicense = professionalLicense,
                yearsOfExperience = yearsOfExperience,
                collegiateRegion = collegiateRegion,
                university = university,
                graduationYear = graduationYear,
                acceptsPrivacyPolicy = acceptsPrivacyPolicy,
                licenseDocumentUri = licenseDocumentUri,
                diplomaDocumentUri = diplomaDocumentUri,
                dniDocumentUri = dniDocumentUri,
                specialties = specialties,
                certificationDocumentUris = certificationDocumentUris
            )
            return
        }

        // Regular registration with password
        if (password.value != confirmPassword.value) {
            _errorMessage.value = "Las contraseñas no coinciden"
            return
        }

        if (!acceptsPrivacyPolicy) {
            _errorMessage.value = "Debes aceptar la política de privacidad"
            return
        }

        // Validate required fields
        if (professionalLicense.isBlank()) {
            _errorMessage.value = "El número de licencia es requerido"
            return
        }
        if (collegiateRegion.isBlank()) {
            _errorMessage.value = "La región de colegiatura es requerida"
            return
        }
        if (university.isBlank()) {
            _errorMessage.value = "La universidad es requerida"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.registerPsychologist(
                firstName = firstName,
                lastName = lastName,
                email = email.value,
                password = password.value,
                professionalLicense = professionalLicense,
                yearsOfExperience = yearsOfExperience,
                collegiateRegion = collegiateRegion,
                university = university,
                graduationYear = graduationYear,
                acceptsPrivacyPolicy = acceptsPrivacyPolicy,
                licenseDocumentUri = licenseDocumentUri,
                diplomaDocumentUri = diplomaDocumentUri,
                dniDocumentUri = dniDocumentUri,
                specialties = specialties,
                certificationDocumentUris = certificationDocumentUris
            ).onSuccess { result ->
                _registrationResultRegular.value = result
                _isLoading.value = false
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error desconocido al registrar"
                _isLoading.value = false
            }
        }
    }

    /**
     * Registers a psychologist via OAuth (no password required).
     * Returns User with JWT token for auto-login.
     */
    private fun registerPsychologistOAuth(
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
    ) {
        if (!acceptsPrivacyPolicy) {
            _errorMessage.value = "Debes aceptar la política de privacidad"
            return
        }

        // Validate required fields
        if (professionalLicense.isBlank()) {
            _errorMessage.value = "El número de licencia es requerido"
            return
        }
        if (collegiateRegion.isBlank()) {
            _errorMessage.value = "La región de colegiatura es requerida"
            return
        }
        if (university.isBlank()) {
            _errorMessage.value = "La universidad es requerida"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.completeOAuthRegistrationPsychologist(
                tempToken = tempToken,
                professionalLicense = professionalLicense,
                yearsOfExperience = yearsOfExperience,
                collegiateRegion = collegiateRegion,
                university = university,
                graduationYear = graduationYear,
                acceptsPrivacyPolicy = acceptsPrivacyPolicy,
                licenseDocumentUri = licenseDocumentUri,
                diplomaDocumentUri = diplomaDocumentUri,
                dniDocumentUri = dniDocumentUri,
                specialties = specialties,
                certificationDocumentUris = certificationDocumentUris
            ).onSuccess { user ->
                // OAuth registration returns User with JWT token for auto-login
                _registrationResultOAuth.value = user
                _isLoading.value = false
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error desconocido al registrar con OAuth"
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearRegistrationResult() {
        _registrationResultRegular.value = null
        _registrationResultOAuth.value = null
    }
}
