package com.softfocus.features.admin.domain.repositories

import com.softfocus.features.admin.domain.models.AdminUser
import com.softfocus.features.admin.domain.models.PaginationInfo
import com.softfocus.features.admin.domain.models.PsychologistDetail

interface AdminRepository {
    suspend fun getAllUsers(
        page: Int,
        pageSize: Int,
        userType: String?,
        isActive: Boolean?,
        isVerified: Boolean?,
        searchTerm: String?,
        sortBy: String?,
        sortDescending: Boolean
    ): Result<Pair<List<AdminUser>, PaginationInfo>>

    suspend fun getPsychologistDetail(userId: String): Result<PsychologistDetail>

    suspend fun verifyPsychologist(
        userId: String,
        isApproved: Boolean,
        notes: String?
    ): Result<Unit>

    suspend fun changeUserStatus(
        userId: String,
        isActive: Boolean,
        reason: String?
    ): Result<Unit>
}
