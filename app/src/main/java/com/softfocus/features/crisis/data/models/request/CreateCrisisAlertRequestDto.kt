package com.softfocus.features.crisis.data.models.request

import com.google.gson.annotations.SerializedName

data class CreateCrisisAlertRequestDto(
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?
)
