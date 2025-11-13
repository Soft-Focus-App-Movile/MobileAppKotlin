package com.softfocus.features.auth.data.models.request

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequestDto(
    @SerializedName("email")
    val email: String
)
