package com.softfocus.features.admin.data.repositories

import com.softfocus.features.admin.data.models.request.ChangeUserStatusRequestDto
import com.softfocus.features.admin.data.models.request.VerifyPsychologistRequestDto
import com.softfocus.features.admin.data.remote.AdminService
import com.softfocus.features.admin.domain.models.AdminUser
import com.softfocus.features.admin.domain.models.PaginationInfo
import com.softfocus.features.admin.domain.models.PsychologistDetail
import com.softfocus.features.admin.domain.repositories.AdminRepository

class AdminRepositoryImpl(
    private val adminService: AdminService
) : AdminRepository {

    override suspend fun getAllUsers(
        page: Int,
        pageSize: Int,
        userType: String?,
        isActive: Boolean?,
        isVerified: Boolean?,
        searchTerm: String?,
        sortBy: String?,
        sortDescending: Boolean
    ): Result<Pair<List<AdminUser>, PaginationInfo>> {
        return try {
            val response = adminService.getAllUsers(
                page = page,
                pageSize = pageSize,
                userType = userType,
                isActive = isActive,
                isVerified = isVerified,
                searchTerm = searchTerm,
                sortBy = sortBy,
                sortDescending = sortDescending
            )
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPsychologistDetail(userId: String): Result<PsychologistDetail> {
        return try {
            val response = adminService.getUserDetail(userId)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyPsychologist(
        userId: String,
        isApproved: Boolean,
        notes: String?
    ): Result<Unit> {
        return try {
            val request = VerifyPsychologistRequestDto(
                isApproved = isApproved,
                notes = notes
            )
            adminService.verifyPsychologist(userId, request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changeUserStatus(
        userId: String,
        isActive: Boolean,
        reason: String?
    ): Result<Unit> {
        return try {
            val request = ChangeUserStatusRequestDto(
                isActive = isActive,
                reason = reason
            )
            adminService.changeUserStatus(userId, request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
