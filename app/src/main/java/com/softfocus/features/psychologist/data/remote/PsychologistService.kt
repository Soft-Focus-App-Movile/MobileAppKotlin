package com.softfocus.features.psychologist.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.psychologist.data.models.response.InvitationCodeResponseDto
import com.softfocus.features.psychologist.data.models.response.PsychologistStatsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PsychologistService {
    @GET(ApiConstants.Users.PSYCHOLOGIST_INVITATION_CODE)
    suspend fun getInvitationCode(): InvitationCodeResponseDto

    @GET(ApiConstants.Users.PSYCHOLOGIST_STATS)
    suspend fun getStats(
        @Query("fromDate") fromDate: String? = null,
        @Query("toDate") toDate: String? = null
    ): PsychologistStatsResponseDto
}
