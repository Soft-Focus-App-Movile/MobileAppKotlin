package com.softfocus.features.admin.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.admin.domain.models.AdminUser
import com.softfocus.features.admin.domain.models.PaginationInfo
import com.softfocus.features.admin.domain.repositories.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminUsersViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<AdminUser>>(emptyList())
    val users: StateFlow<List<AdminUser>> = _users

    private val _paginationInfo = MutableStateFlow<PaginationInfo?>(null)
    val paginationInfo: StateFlow<PaginationInfo?> = _paginationInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _filterUserType = MutableStateFlow<String?>(null)
    val filterUserType: StateFlow<String?> = _filterUserType

    private val _filterIsVerified = MutableStateFlow<Boolean?>(null)
    val filterIsVerified: StateFlow<Boolean?> = _filterIsVerified

    private val _searchTerm = MutableStateFlow("")
    val searchTerm: StateFlow<String> = _searchTerm

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getAllUsers(
                page = _currentPage.value,
                pageSize = 20,
                userType = _filterUserType.value,
                isActive = null,
                isVerified = _filterIsVerified.value,
                searchTerm = _searchTerm.value.ifBlank { null },
                sortBy = null,
                sortDescending = false
            ).onSuccess { (users, pagination) ->
                _users.value = users
                _paginationInfo.value = pagination
                _isLoading.value = false
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al cargar usuarios"
                _isLoading.value = false
            }
        }
    }

    fun setFilterUserType(userType: String?) {
        _filterUserType.value = userType
        _currentPage.value = 1
        loadUsers()
    }

    fun setFilterIsVerified(isVerified: Boolean?) {
        _filterIsVerified.value = isVerified
        _currentPage.value = 1
        loadUsers()
    }

    fun updateSearchTerm(term: String) {
        _searchTerm.value = term
    }

    fun search() {
        _currentPage.value = 1
        loadUsers()
    }

    fun nextPage() {
        if (_paginationInfo.value?.hasNextPage == true) {
            _currentPage.value++
            loadUsers()
        }
    }

    fun previousPage() {
        if (_paginationInfo.value?.hasPreviousPage == true) {
            _currentPage.value--
            loadUsers()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
