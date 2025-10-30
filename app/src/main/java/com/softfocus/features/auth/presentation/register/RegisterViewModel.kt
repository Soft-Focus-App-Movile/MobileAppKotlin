package com.softfocus.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.data.repositories.UniversityInfo
import com.softfocus.core.data.repositories.UniversityRepository
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.auth.domain.repositories.AuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
class RegisterViewModel(
    private val repository: AuthRepository,
    private val universityRepository: UniversityRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    // Validation error states
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError

    private val _userType = MutableStateFlow<UserType?>(null)
    val userType: StateFlow<UserType?> = _userType


    private val _oauthTempToken = MutableStateFlow<String?>(null)
    val oauthTempToken: StateFlow<String?> = _oauthTempToken


    private val _registrationResultRegular = MutableStateFlow<Pair<String, String>?>(null)
    val registrationResultRegular: StateFlow<Pair<String, String>?> = _registrationResultRegular


    private val _registrationResultOAuth = MutableStateFlow<User?>(null)
    val registrationResultOAuth: StateFlow<User?> = _registrationResultOAuth

    private val _psychologistPendingVerification = MutableStateFlow(false)
    val psychologistPendingVerification: StateFlow<Boolean> = _psychologistPendingVerification

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _universitySuggestions = MutableStateFlow<List<UniversityInfo>>(emptyList())
    val universitySuggestions: StateFlow<List<UniversityInfo>> = _universitySuggestions

    private var searchJob: Job? = null

    fun updateEmail(value: String) {
        _email.value = value
        validateEmail(value)
    }

    fun updatePassword(value: String) {
        _password.value = value
        validatePassword(value)
        // Also revalidate confirm password if it has a value
        if (_confirmPassword.value.isNotEmpty()) {
            validateConfirmPassword(_confirmPassword.value)
        }
    }

    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
        validateConfirmPassword(value)
    }

    private fun validateEmail(email: String) {
        _emailError.value = when {
            email.isEmpty() -> null // Don't show error for empty field
            !email.contains('@') -> "El email debe contener @"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email inválido"
            email.length > 100 -> "El email no puede exceder 100 caracteres"
            else -> null
        }
    }

    private fun validatePassword(password: String) {
        _passwordError.value = when {
            password.isEmpty() -> null // Don't show error for empty field
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            password.length > 100 -> "La contraseña no puede exceder 100 caracteres"
            !password.any { it.isUpperCase() } -> "Debe contener al menos una mayúscula"
            !password.any { it.isLowerCase() } -> "Debe contener al menos una minúscula"
            !password.any { it.isDigit() } -> "Debe contener al menos un número"
            !password.any { it in "@\$!%*?&" } -> "Debe contener al menos un carácter especial (@\$!%*?&)"
            else -> null
        }
    }

    private fun validateConfirmPassword(confirmPassword: String) {
        _confirmPasswordError.value = when {
            confirmPassword.isEmpty() -> null // Don't show error for empty field
            confirmPassword != _password.value -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    fun clearValidationErrors() {
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
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

            android.util.Log.d("RegisterViewModel", "Registering psychologist:")
            android.util.Log.d("RegisterViewModel", "firstName: $firstName")
            android.util.Log.d("RegisterViewModel", "lastName: $lastName")
            android.util.Log.d("RegisterViewModel", "email: ${email.value}")
            android.util.Log.d("RegisterViewModel", "password: ${password.value}")
            android.util.Log.d("RegisterViewModel", "professionalLicense: $professionalLicense")
            android.util.Log.d("RegisterViewModel", "university: $university")
            android.util.Log.d("RegisterViewModel", "collegiateRegion: $collegiateRegion")

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

            android.util.Log.d("RegisterViewModel", "=== ANTES DE LLAMAR REPOSITORY ===")
            android.util.Log.d("RegisterViewModel", "yearsOfExperience: $yearsOfExperience")
            android.util.Log.d("RegisterViewModel", "graduationYear: $graduationYear")
            android.util.Log.d("RegisterViewModel", "professionalLicense: $professionalLicense")
            android.util.Log.d("RegisterViewModel", "collegiateRegion: $collegiateRegion")

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
                // Check if it's a pending verification error
                if (error is com.softfocus.features.auth.data.repositories.AuthRepositoryImpl.PsychologistPendingVerificationException) {
                    _psychologistPendingVerification.value = true
                    _isLoading.value = false
                } else {
                    _errorMessage.value = error.message ?: "Error desconocido al registrar con OAuth"
                    _isLoading.value = false
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearRegistrationResult() {
        _registrationResultRegular.value = null
        _registrationResultOAuth.value = null
        _psychologistPendingVerification.value = false
    }

    fun searchUniversities(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            universityRepository.searchUniversities(query)
                .onSuccess { suggestions ->
                    _universitySuggestions.value = suggestions
                }
                .onFailure {
                    _universitySuggestions.value = emptyList()
                }
        }
    }

    fun clearUniversitySuggestions() {
        _universitySuggestions.value = emptyList()
    }
}
