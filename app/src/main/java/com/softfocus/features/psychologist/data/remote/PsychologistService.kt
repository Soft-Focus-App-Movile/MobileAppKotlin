package com.softfocus.features.psychologist.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.psychologist.data.models.response.InvitationCodeResponseDto
import retrofit2.http.GET

interface PsychologistService {
    @GET(ApiConstants.Users.PSYCHOLOGIST_INVITATION_CODE)
    suspend fun getInvitationCode(): InvitationCodeResponseDto
}
