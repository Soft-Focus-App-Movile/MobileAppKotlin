package com.softfocus.features.therapy.data.models.request

import com.google.gson.annotations.SerializedName

data class SendChatMessageRequestDto(
    @SerializedName("relationshipId") val relationshipId: String,
    @SerializedName("receiverId") val receiverId: String,
    @SerializedName("content") val content: String,
    @SerializedName("messageType") val messageType: String = "text"
)