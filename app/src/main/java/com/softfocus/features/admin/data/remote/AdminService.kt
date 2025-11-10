package com.softfocus.features.admin.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.admin.data.models.request.ChangeUserStatusRequestDto
import com.softfocus.features.admin.data.models.request.VerifyPsychologistRequestDto
import com.softfocus.features.admin.data.models.response.PsychologistDetailResponseDto
import com.softfocus.features.admin.data.models.response.UserListResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminService {

    @GET(ApiConstants.Users.BASE)
    suspend fun getAllUsers(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("userType") userType: String? = null,
        @Query("isActive") isActive: Boolean? = null,
        @Query("isVerified") isVerified: Boolean? = null,
        @Query("searchTerm") searchTerm: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortDescending") sortDescending: Boolean = false
    ): UserListResponseDto

    @GET(ApiConstants.Users.BY_ID)
    suspend fun getUserDetail(
        @Path("id") userId: String
    ): PsychologistDetailResponseDto

    @PUT(ApiConstants.Users.VERIFY_PSYCHOLOGIST)
    suspend fun verifyPsychologist(
        @Path("id") userId: String,
        @Body request: VerifyPsychologistRequestDto
    )

    @PUT(ApiConstants.Users.CHANGE_STATUS)
    suspend fun changeUserStatus(
        @Path("id") userId: String,
        @Body request: ChangeUserStatusRequestDto
    )
}
