package com.softfocus.features.psychologist.data.remote

import com.softfocus.features.psychologist.data.models.response.InvitationCodeResponseDto
import retrofit2.http.GET

interface PsychologistService {
    @GET("users/psychologist/invitation-code")
    suspend fun getInvitationCode(): InvitationCodeResponseDto
}
