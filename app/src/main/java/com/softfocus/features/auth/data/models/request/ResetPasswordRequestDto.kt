package com.softfocus.features.auth.data.models.request

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequestDto(
    @SerializedName("token")
    val token: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("newPassword")
    val newPassword: String
)
