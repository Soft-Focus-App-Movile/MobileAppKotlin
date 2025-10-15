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
        @Part("firstName") firstName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("professionalLicense") professionalLicense: RequestBody,
        @Part("yearsOfExperience") yearsOfExperience: RequestBody,
        @Part("collegiateRegion") collegiateRegion: RequestBody,
        @Part("university") university: RequestBody,
        @Part("graduationYear") graduationYear: RequestBody,
        @Part("acceptsPrivacyPolicy") acceptsPrivacyPolicy: RequestBody,
        @Part licenseDocument: MultipartBody.Part,
        @Part diplomaDocument: MultipartBody.Part,
        @Part dniDocument: MultipartBody.Part,
        @Part("specialties") specialties: RequestBody? = null,
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
        @Part("tempToken") tempToken: RequestBody,
        @Part("userType") userType: RequestBody,
        @Part("acceptsPrivacyPolicy") acceptsPrivacyPolicy: RequestBody
    ): LoginResponseDto


    @Multipart
    @POST("auth/oauth/complete-registration")
    suspend fun completeOAuthRegistrationPsychologist(
        @Part("tempToken") tempToken: RequestBody,
        @Part("userType") userType: RequestBody,
        @Part("acceptsPrivacyPolicy") acceptsPrivacyPolicy: RequestBody,
        @Part("professionalLicense") professionalLicense: RequestBody,
        @Part("yearsOfExperience") yearsOfExperience: RequestBody,
        @Part("collegiateRegion") collegiateRegion: RequestBody,
        @Part("university") university: RequestBody,
        @Part("graduationYear") graduationYear: RequestBody,
        @Part licenseDocument: MultipartBody.Part,
        @Part diplomaDocument: MultipartBody.Part,
        @Part dniDocument: MultipartBody.Part,
        @Part("specialties") specialties: RequestBody? = null,
        @Part certificationDocuments: List<MultipartBody.Part>? = null
    ): LoginResponseDto
}
