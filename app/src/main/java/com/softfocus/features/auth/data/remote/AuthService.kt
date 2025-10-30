package com.softfocus.features.auth.data.remote

import com.softfocus.features.auth.data.models.request.LoginRequestDto
import com.softfocus.features.auth.data.models.request.OAuthLoginRequestDto
import com.softfocus.features.auth.data.models.request.OAuthVerifyRequestDto
import com.softfocus.features.auth.data.models.request.RegisterGeneralUserRequestDto
import com.softfocus.features.auth.data.models.request.RegisterRequestDto
import com.softfocus.features.auth.data.models.request.SocialLoginRequestDto
import com.softfocus.features.auth.data.models.response.LoginResponseDto
import com.softfocus.features.auth.data.models.response.OAuthVerificationResponseDto
import com.softfocus.features.auth.data.models.response.RegisterResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthService {


    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto


    @POST("auth/register/general")
    suspend fun registerGeneralUser(@Body request: RegisterGeneralUserRequestDto): RegisterResponseDto


    @Multipart
    @POST("auth/register/psychologist")
    suspend fun registerPsychologist(
        @Part firstName: MultipartBody.Part,
        @Part lastName: MultipartBody.Part,
        @Part email: MultipartBody.Part,
        @Part password: MultipartBody.Part,
        @Part professionalLicense: MultipartBody.Part,
        @Part yearsOfExperience: MultipartBody.Part,
        @Part collegiateRegion: MultipartBody.Part,
        @Part university: MultipartBody.Part,
        @Part graduationYear: MultipartBody.Part,
        @Part acceptsPrivacyPolicy: MultipartBody.Part,
        @Part licenseDocument: MultipartBody.Part,
        @Part diplomaDocument: MultipartBody.Part,
        @Part dniDocument: MultipartBody.Part,
        @Part specialties: MultipartBody.Part? = null,
        @Part certificationDocuments: List<MultipartBody.Part>? = null
    ): RegisterResponseDto


    @Deprecated("Use registerGeneralUser or registerPsychologist instead")
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): RegisterResponseDto


    @POST("auth/social-login")
    suspend fun socialLogin(@Body request: SocialLoginRequestDto): LoginResponseDto


    @POST("auth/oauth/verify")
    suspend fun verifyOAuth(@Body request: OAuthVerifyRequestDto): OAuthVerificationResponseDto


    @POST("auth/oauth")
    suspend fun oauthLogin(@Body request: OAuthLoginRequestDto): LoginResponseDto


    @Multipart
    @POST("auth/oauth/complete-registration")
    suspend fun completeOAuthRegistration(
        @Part tempToken: MultipartBody.Part,
        @Part userType: MultipartBody.Part,
        @Part acceptsPrivacyPolicy: MultipartBody.Part
    ): LoginResponseDto


    @Multipart
    @POST("auth/oauth/complete-registration")
    suspend fun completeOAuthRegistrationPsychologist(
        @Part tempToken: MultipartBody.Part,
        @Part userType: MultipartBody.Part,
        @Part acceptsPrivacyPolicy: MultipartBody.Part,
        @Part professionalLicense: MultipartBody.Part,
        @Part yearsOfExperience: MultipartBody.Part,
        @Part collegiateRegion: MultipartBody.Part,
        @Part university: MultipartBody.Part,
        @Part graduationYear: MultipartBody.Part,
        @Part licenseDocument: MultipartBody.Part,
        @Part diplomaDocument: MultipartBody.Part,
        @Part dniDocument: MultipartBody.Part,
        @Part specialties: MultipartBody.Part? = null,
        @Part certificationDocuments: List<MultipartBody.Part>? = null
    ): LoginResponseDto
}
