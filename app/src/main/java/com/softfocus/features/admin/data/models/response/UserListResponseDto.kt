package com.softfocus.features.admin.data.models.response

import com.softfocus.features.admin.domain.models.AdminUser
import com.softfocus.features.admin.domain.models.PaginationInfo

data class UserListResponseDto(
    val users: List<UserItemDto>,
    val pagination: PaginationDto,
    val filters: FiltersDto
) {
    fun toDomain(): Pair<List<AdminUser>, PaginationInfo> {
        val adminUsers = users.map { it.toDomain() }
        val paginationInfo = pagination.toDomain()
        return Pair(adminUsers, paginationInfo)
    }
}

data class UserItemDto(
    val id: String,
    val email: String,
    val fullName: String,
    val userType: String,
    val isActive: Boolean,
    val lastLogin: String?,
    val createdAt: String,
    val isVerified: Boolean?
) {
    fun toDomain() = AdminUser(
        id = id,
        email = email,
        fullName = fullName,
        userType = userType,
        isActive = isActive,
        lastLogin = lastLogin,
        createdAt = createdAt,
        isVerified = isVerified
    )
}

data class PaginationDto(
    val page: Int,
    val pageSize: Int,
    val totalCount: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
) {
    fun toDomain() = PaginationInfo(
        page = page,
        pageSize = pageSize,
        totalCount = totalCount,
        totalPages = totalPages,
        hasNextPage = hasNextPage,
        hasPreviousPage = hasPreviousPage
    )
}

data class FiltersDto(
    val userType: String?,
    val isActive: Boolean?,
    val isVerified: Boolean?,
    val searchTerm: String?,
    val sortBy: String?,
    val sortDescending: Boolean
)
