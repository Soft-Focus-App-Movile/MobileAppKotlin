package com.softfocus.features.subscription.data.models.response

import com.google.gson.annotations.SerializedName

data class UsageStatsResponseDto(
    @SerializedName("plan")
    val plan: String,

    @SerializedName("featureUsages")
    val featureUsages: List<FeatureUsageDto>,

    @SerializedName("generatedAt")
    val generatedAt: String
)

data class FeatureUsageDto(
    @SerializedName("featureType")
    val featureType: String,

    @SerializedName("currentUsage")
    val currentUsage: Int,

    @SerializedName("limit")
    val limit: Int?,

    @SerializedName("isUnlimited")
    val isUnlimited: Boolean,

    @SerializedName("limitReached")
    val limitReached: Boolean,

    @SerializedName("remaining")
    val remaining: Int?,

    @SerializedName("periodStart")
    val periodStart: String,

    @SerializedName("periodEnd")
    val periodEnd: String,

    @SerializedName("lastUsedAt")
    val lastUsedAt: String?
)
