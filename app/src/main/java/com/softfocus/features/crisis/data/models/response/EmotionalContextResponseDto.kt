package com.softfocus.features.crisis.data.models.response

import com.google.gson.annotations.SerializedName

data class EmotionalContextResponseDto(
    @SerializedName("lastDetectedEmotion")
    val lastDetectedEmotion: String?,
    @SerializedName("lastEmotionDetectedAt")
    val lastEmotionDetectedAt: String?,
    @SerializedName("emotionSource")
    val emotionSource: String?
)
