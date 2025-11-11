package com.softfocus.features.tracking.data.remote

import com.softfocus.features.tracking.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface TrackingApi {

    // ============= CHECK-INS ENDPOINTS =============

    @POST("tracking/check-ins")
    suspend fun createCheckIn(
        @Body request: CreateCheckInRequest
    ): Response<CreateCheckInApiResponse>

    @GET("tracking/check-ins")
    suspend fun getCheckIns(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("pageNumber") pageNumber: Int? = null,
        @Query("pageSize") pageSize: Int? = null
    ): Response<CheckInsHistoryApiResponse>

    @GET("tracking/check-ins/{id}")
    suspend fun getCheckInById(
        @Path("id") id: String
    ): Response<CreateCheckInApiResponse>

    @GET("tracking/check-ins/today")
    suspend fun getTodayCheckIn(): Response<TodayCheckInApiResponse>

    // ============= EMOTIONAL CALENDAR ENDPOINTS =============

    @POST("tracking/emotional-calendar")
    suspend fun createEmotionalCalendarEntry(
        @Body request: CreateEmotionalCalendarRequest
    ): Response<CreateEmotionalCalendarApiResponse>

    @GET("tracking/emotional-calendar")
    suspend fun getEmotionalCalendar(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<EmotionalCalendarApiResponse>

    @GET("tracking/emotional-calendar/{date}")
    suspend fun getEmotionalCalendarByDate(
        @Path("date") date: String
    ): Response<CreateEmotionalCalendarApiResponse>

    // ============= DASHBOARD ENDPOINT =============

    @GET("tracking/dashboard")
    suspend fun getDashboard(
        @Query("days") days: Int? = null
    ): Response<DashboardApiResponse>

    @GET("tracking/dashboard/{userId}")
    suspend fun getPatientDashboard(
        @Path("userId") userId: String,
        @Query("days") days: Int? = null
    ): Response<DashboardApiResponse>
}