package com.softfocus.features.subscription.data.models.response

import com.google.gson.annotations.SerializedName

data class CheckoutSessionResponseDto(
    @SerializedName("sessionId")
    val sessionId: String,

    @SerializedName("checkoutUrl")
    val checkoutUrl: String
)
