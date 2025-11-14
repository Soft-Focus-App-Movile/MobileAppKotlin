package com.softfocus.features.subscription.data.models.request

import com.google.gson.annotations.SerializedName

data class CreateCheckoutSessionRequestDto(
    @SerializedName("successUrl")
    val successUrl: String,

    @SerializedName("cancelUrl")
    val cancelUrl: String
)
