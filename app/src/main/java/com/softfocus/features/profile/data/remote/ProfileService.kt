package com.softfocus.features.profile.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.profile.data.models.response.ProfileResponseDto
import com.softfocus.features.profile.data.models.response.PsychologistCompleteProfileResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ProfileService {

    @GET(ApiConstants.Users.PROFILE)
    suspend fun getProfile(): Response<ProfileResponseDto>

    @GET(ApiConstants.Users.PSYCHOLOGIST_COMPLETE_PROFILE)
    suspend fun getPsychologistCompleteProfile(): Response<PsychologistCompleteProfileResponseDto>

    @Multipart
    @PUT(ApiConstants.Users.PROFILE)
    suspend fun updateProfileWithImage(
        @Part("FirstName") firstName: RequestBody?,
        @Part("LastName") lastName: RequestBody?,
        @Part("DateOfBirth") dateOfBirth: RequestBody?,
        @Part("Gender") gender: RequestBody?,
        @Part("Phone") phone: RequestBody?,
        @Part("Bio") bio: RequestBody?,
        @Part("Country") country: RequestBody?,
        @Part("City") city: RequestBody?,
        @Part interests: List<MultipartBody.Part>?,
        @Part mentalHealthGoals: List<MultipartBody.Part>?,
        @Part("EmailNotifications") emailNotifications: RequestBody?,
        @Part("PushNotifications") pushNotifications: RequestBody?,
        @Part("IsProfilePublic") isProfilePublic: RequestBody?,
        @Part profileImage: MultipartBody.Part?
    ): Response<ProfileResponseDto>

    @Headers("Content-Type: application/json")
    @PUT(ApiConstants.Users.PSYCHOLOGIST_PROFESSIONAL_DATA)
    suspend fun updateProfessionalProfile(
        @Body professionalData: Map<String, @JvmSuppressWildcards Any?>
    ): Response<PsychologistCompleteProfileResponseDto>
}
