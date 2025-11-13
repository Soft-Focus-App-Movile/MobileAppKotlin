package com.softfocus.features.auth.data.models.response

import com.google.gson.annotations.SerializedName

data class ResetPasswordResponseDto(
    @SerializedName("message")
    val message: String,

    @SerializedName("success")
    val success: Boolean
)
