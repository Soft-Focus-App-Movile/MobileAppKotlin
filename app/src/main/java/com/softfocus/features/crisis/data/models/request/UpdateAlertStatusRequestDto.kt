package com.softfocus.features.crisis.data.models.request

import com.google.gson.annotations.SerializedName

data class UpdateAlertStatusRequestDto(
    @SerializedName("status")
    val status: String
)
