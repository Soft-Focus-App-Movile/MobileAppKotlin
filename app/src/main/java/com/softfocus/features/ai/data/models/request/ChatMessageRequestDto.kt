package com.softfocus.features.ai.data.models.request

import com.google.gson.annotations.SerializedName

data class ChatMessageRequestDto(
    @SerializedName("message")
    val message: String,

    @SerializedName("sessionId")
    val sessionId: String? = null
)
