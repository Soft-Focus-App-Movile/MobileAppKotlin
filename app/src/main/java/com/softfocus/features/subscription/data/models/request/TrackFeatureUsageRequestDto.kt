package com.softfocus.features.subscription.data.models.request

import com.google.gson.annotations.SerializedName

data class TrackFeatureUsageRequestDto(
    @SerializedName("userId")
    val userId: String,

    @SerializedName("featureType")
    val featureType: String
)
