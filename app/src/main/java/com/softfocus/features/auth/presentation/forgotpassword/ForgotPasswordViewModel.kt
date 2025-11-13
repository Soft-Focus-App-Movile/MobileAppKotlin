package com.softfocus.features.auth.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.auth.domain.usecases.ForgotPasswordUseCase
import com.softfocus.features.auth.domain.usecases.ResetPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.EnterEmail)
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _token = MutableStateFlow("")
    val token: StateFlow<String> = _token.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _tokenError = MutableStateFlow<String?>(null)
    val tokenError: StateFlow<String?> = _tokenError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError.asStateFlow()

    fun onEmailChange(value: String) {
        _email.value = value
        _emailError.value = null
    }

    fun onTokenChange(value: String) {
        _token.value = value
        _tokenError.value = null
    }

    fun onNewPasswordChange(value: String) {
        _newPassword.value = value
        _passwordError.value = null
    }

    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = value
        _confirmPasswordError.value = null
    }

    fun sendResetCode() {
        if (!validateEmail()) return

        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading

            forgotPasswordUseCase(email.value).fold(
                onSuccess = { message ->
                    _uiState.value = ForgotPasswordUiState.CodeSent(message)
                },
                onFailure = { exception ->
                    _uiState.value = ForgotPasswordUiState.Error(
                        exception.message ?: "Error al enviar el código"
                    )
                }
            )
        }
    }

    fun resetPassword() {
        if (!validateResetPassword()) return

        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading

            resetPasswordUseCase(token.value, email.value, newPassword.value).fold(
                onSuccess = { message ->
                    _uiState.value = ForgotPasswordUiState.Success(message)
                },
                onFailure = { exception ->
                    _uiState.value = ForgotPasswordUiState.Error(
                        exception.message ?: "Error al restablecer la contraseña"
                    )
                }
            )
        }
    }

    private fun validateEmail(): Boolean {
        if (email.value.isBlank()) {
            _emailError.value = "El correo es requerido"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            _emailError.value = "Correo inválido"
            return false
        }
        return true
    }

    private fun validateResetPassword(): Boolean {
        var isValid = true

        if (token.value.isBlank()) {
            _tokenError.value = "El código es requerido"
            isValid = false
        }

        if (newPassword.value.isBlank()) {
            _passwordError.value = "La contraseña es requerida"
            isValid = false
        } else if (newPassword.value.length < 8) {
            _passwordError.value = "La contraseña debe tener al menos 8 caracteres"
            isValid = false
        }

        if (confirmPassword.value != newPassword.value) {
            _confirmPasswordError.value = "Las contraseñas no coinciden"
            isValid = false
        }

        return isValid
    }

    fun clearError() {
        if (_uiState.value is ForgotPasswordUiState.Error) {
            _uiState.value = ForgotPasswordUiState.CodeSent("Código enviado a tu correo")
        }
    }
}

sealed class ForgotPasswordUiState {
    object EnterEmail : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    data class CodeSent(val message: String) : ForgotPasswordUiState()
    data class Success(val message: String) : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()
}
