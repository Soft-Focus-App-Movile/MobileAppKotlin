package com.softfocus.features.subscription.data.models.request

import com.google.gson.annotations.SerializedName

data class CancelSubscriptionRequestDto(
    @SerializedName("cancelImmediately")
    val cancelImmediately: Boolean = false
)
