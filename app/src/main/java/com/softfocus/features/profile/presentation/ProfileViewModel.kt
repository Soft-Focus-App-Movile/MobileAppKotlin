package com.softfocus.features.profile.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.profile.domain.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            val cachedUser = userSession.getUser()
            if (cachedUser != null) {
                _user.value = cachedUser
            }

            profileRepository.getProfile()
                .onSuccess { user ->
                    _user.value = user
                    _uiState.value = ProfileUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(
                        error.message ?: "Error al cargar perfil"
                    )
                }
        }
    }

    fun updateProfile(
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
        profileImageUri: Uri? = null
    ) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            profileRepository.updateProfile(
                firstName = firstName,
                lastName = lastName,
                dateOfBirth = dateOfBirth,
                gender = gender,
                phone = phone,
                bio = bio,
                country = country,
                city = city,
                interests = interests,
                mentalHealthGoals = mentalHealthGoals,
                emailNotifications = emailNotifications,
                pushNotifications = pushNotifications,
                isProfilePublic = isProfilePublic,
                profileImageUri = profileImageUri
            )
                .onSuccess { user ->
                    _user.value = user
                    _uiState.value = ProfileUiState.UpdateSuccess
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(
                        error.message ?: "Error al actualizar perfil"
                    )
                }
        }
    }

    fun updateProfessionalProfile(
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
    ) {
        viewModelScope.launch {
            android.util.Log.d("ProfileViewModel", "updateProfessionalProfile called")
            android.util.Log.d("ProfileViewModel", "Bio: $professionalBio, Languages: $languages, TargetAudience: $targetAudience")

            _uiState.value = ProfileUiState.Loading

            val result = profileRepository.updateProfessionalProfile(
                professionalBio = professionalBio,
                isAcceptingNewPatients = isAcceptingNewPatients,
                maxPatientsCapacity = maxPatientsCapacity,
                targetAudience = targetAudience,
                languages = languages,
                businessName = businessName,
                businessAddress = businessAddress,
                bankAccount = bankAccount,
                paymentMethods = paymentMethods,
                isProfileVisibleInDirectory = isProfileVisibleInDirectory,
                allowsDirectMessages = allowsDirectMessages
            )

            android.util.Log.d("ProfileViewModel", "Result: ${result.isSuccess}, ${result.isFailure}")

            result.onSuccess {
                    android.util.Log.d("ProfileViewModel", "Professional profile updated successfully")
                    // Reload profile to get updated data
                    loadProfile()
                    _uiState.value = ProfileUiState.UpdateSuccess
                }
                .onFailure { error ->
                    android.util.Log.e("ProfileViewModel", "Error updating professional profile", error)
                    _uiState.value = ProfileUiState.Error(
                        error.message ?: "Error al actualizar perfil profesional"
                    )
                }
        }
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    object UpdateSuccess : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
