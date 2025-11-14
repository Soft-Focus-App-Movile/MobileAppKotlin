package com.softfocus.features.subscription.data.models.response

import com.google.gson.annotations.SerializedName

data class SubscriptionResponseDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("userType")
    val userType: String,

    @SerializedName("plan")
    val plan: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("currentPeriodStart")
    val currentPeriodStart: String?,

    @SerializedName("currentPeriodEnd")
    val currentPeriodEnd: String?,

    @SerializedName("cancelAtPeriodEnd")
    val cancelAtPeriodEnd: Boolean,

    @SerializedName("cancelledAt")
    val cancelledAt: String?,

    @SerializedName("isActive")
    val isActive: Boolean,

    @SerializedName("usageLimits")
    val usageLimits: UsageLimitsDto,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

data class UsageLimitsDto(
    @SerializedName("aiChatMessagesPerDay")
    val aiChatMessagesPerDay: Int?,

    @SerializedName("facialAnalysisPerWeek")
    val facialAnalysisPerWeek: Int?,

    @SerializedName("contentRecommendationsPerWeek")
    val contentRecommendationsPerWeek: Int?,

    @SerializedName("checkInsPerDay")
    val checkInsPerDay: Int?,

    @SerializedName("maxPatientConnections")
    val maxPatientConnections: Int?,

    @SerializedName("contentAssignmentsPerWeek")
    val contentAssignmentsPerWeek: Int?
)
