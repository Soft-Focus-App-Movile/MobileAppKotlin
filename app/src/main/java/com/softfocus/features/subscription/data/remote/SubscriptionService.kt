package com.softfocus.features.subscription.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.subscription.data.models.request.CancelSubscriptionRequestDto
import com.softfocus.features.subscription.data.models.request.CreateCheckoutSessionRequestDto
import com.softfocus.features.subscription.data.models.request.TrackFeatureUsageRequestDto
import com.softfocus.features.subscription.data.models.response.CheckoutSessionResponseDto
import com.softfocus.features.subscription.data.models.response.FeatureAccessResponseDto
import com.softfocus.features.subscription.data.models.response.SubscriptionResponseDto
import com.softfocus.features.subscription.data.models.response.UsageStatsResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SubscriptionService {
    @GET(ApiConstants.Subscription.ME)
    suspend fun getMySubscription(): Response<SubscriptionResponseDto>

    @GET(ApiConstants.Subscription.USAGE)
    suspend fun getUsageStats(): Response<UsageStatsResponseDto>

    @GET(ApiConstants.Subscription.CHECK_ACCESS)
    suspend fun checkFeatureAccess(
        @Path("featureType") featureType: String
    ): Response<FeatureAccessResponseDto>

    @POST(ApiConstants.Subscription.UPGRADE_CHECKOUT)
    suspend fun createCheckoutSession(
        @Body request: CreateCheckoutSessionRequestDto
    ): Response<CheckoutSessionResponseDto>

    @POST(ApiConstants.Subscription.CANCEL)
    suspend fun cancelSubscription(
        @Body request: CancelSubscriptionRequestDto
    ): Response<SubscriptionResponseDto>

    @POST(ApiConstants.Subscription.TRACK_USAGE)
    suspend fun trackUsage(
        @Body request: TrackFeatureUsageRequestDto
    ): Response<Unit>

    @POST(ApiConstants.Subscription.INITIALIZE)
    suspend fun initializeSubscription(): Response<SubscriptionResponseDto>
}
