package com.softfocus.features.therapy.domain.models

data class ChatMessage(
    val id: String,
    val relationshipId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: String, // Usamos String para simplicidad, parseado en la UI
    val messageType: String,
    val isFromMe: Boolean // Flag de la UI para saber si es emisor o receptor
)