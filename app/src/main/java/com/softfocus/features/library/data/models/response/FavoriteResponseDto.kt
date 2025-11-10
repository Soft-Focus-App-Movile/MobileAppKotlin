package com.softfocus.features.library.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.library.domain.models.Favorite
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * DTO para respuesta de favorito del backend
 */
data class FavoriteResponseDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("content")
    val content: ContentItemResponseDto,

    @SerializedName("addedAt")
    val addedAt: String // ISO 8601 format: "2025-11-02T10:30:00"
) {
    /**
     * Mapea el DTO a la entidad de dominio
     */
    fun toDomain(): Favorite {
        return Favorite(
            id = id,
            userId = userId,
            content = content.toDomain(),
            addedAt = parseDateTime(addedAt)
        )
    }

    companion object {
        /**
         * Parsea un string de fecha/hora a LocalDateTime
         */
        private fun parseDateTime(dateTimeString: String): LocalDateTime {
            return try {
                LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
            } catch (e: Exception) {
                // Fallback: intenta otros formatos comunes
                try {
                    LocalDateTime.parse(dateTimeString)
                } catch (e: Exception) {
                    LocalDateTime.now() // Último fallback
                }
            }
        }
    }
}
