package com.softfocus.features.subscription.data.models.response

import com.google.gson.annotations.SerializedName

data class FeatureAccessResponseDto(
    @SerializedName("hasAccess")
    val hasAccess: Boolean,

    @SerializedName("denialReason")
    val denialReason: String?,

    @SerializedName("currentUsage")
    val currentUsage: Int?,

    @SerializedName("limit")
    val limit: Int?,

    @SerializedName("upgradeMessage")
    val upgradeMessage: String?
)
