package com.softfocus.features.therapy.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.therapy.domain.models.ChatMessage

data class SendChatMessageResponseDto(
    @SerializedName("messageId") val messageId: String
)


data class ChatHistoryResponseDto(
    @SerializedName("messages") val messages: List<ChatMessageDto>
    // Asumimos que el backend podría incluir paginación en el futuro
)

data class ChatMessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("relationshipId") val relationshipId: String,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("receiverId") val receiverId: String,
    @SerializedName("content") val content: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("messageType") val messageType: String,
    @SerializedName("isRead") val isRead: Boolean
) {

    fun toDomain(currentUserId: String): ChatMessage {
        return ChatMessage(
            id = id,
            relationshipId = relationshipId,
            senderId = senderId,
            receiverId = receiverId,
            content = content,
            timestamp = timestamp,
            messageType = messageType,
            isFromMe = (senderId == currentUserId) // Clave para la UI
        )
    }
}