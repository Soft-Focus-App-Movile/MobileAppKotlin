package com.softfocus.features.library.domain.models

enum class EmotionalTag(val displayName: String) {
    Happy("Alegre"),
    Calm("Tranquilo"),
    Energetic("Energético");

    companion object {
        fun fromString(value: String): EmotionalTag {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid EmotionalTag: $value")
            }
        }

        fun getAll(): List<EmotionalTag> = values().toList()
    }
}
