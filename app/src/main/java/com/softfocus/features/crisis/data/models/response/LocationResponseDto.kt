package com.softfocus.features.crisis.data.models.response

import com.google.gson.annotations.SerializedName

data class LocationResponseDto(
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("displayString")
    val displayString: String
)
