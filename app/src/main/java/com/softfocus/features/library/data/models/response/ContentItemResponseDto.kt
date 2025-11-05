package com.softfocus.features.library.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.domain.models.EmotionalTag

/**
 * DTO para respuesta de contenido multimedia del backend
 */
data class ContentItemResponseDto(
    @SerializedName("id")
    val externalId: String,

    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("type")
    val type: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("overview")
    val overview: String? = null,

    @SerializedName("posterUrl")
    val posterUrl: String? = null,

    @SerializedName("backdropUrl")
    val backdropUrl: String? = null,

    @SerializedName("rating")
    val rating: String? = null,

    @SerializedName("duration")
    val duration: String? = null,

    @SerializedName("genres")
    val genres: List<String>? = null,

    @SerializedName("trailerUrl")
    val trailerUrl: String? = null,

    @SerializedName("emotionalTags")
    val emotionalTags: List<String>? = null,

    @SerializedName("externalUrl")
    val externalUrl: String? = null,

    // Music specific
    @SerializedName("artist")
    val artist: String? = null,

    @SerializedName("album")
    val album: String? = null,

    @SerializedName("previewUrl")
    val previewUrl: String? = null,

    @SerializedName("spotifyUrl")
    val spotifyUrl: String? = null,

    // Video specific
    @SerializedName("channelName")
    val channelName: String? = null,

    @SerializedName("youtubeUrl")
    val youtubeUrl: String? = null,

    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String? = null,

    // Place specific
    @SerializedName("category")
    val category: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("latitude")
    val latitude: Double? = null,

    @SerializedName("longitude")
    val longitude: Double? = null,

    @SerializedName("distance")
    val distance: Double? = null,

    @SerializedName("photoUrl")
    val photoUrl: String? = null
) {
    /**
     * Mapea el DTO a la entidad de dominio
     */
    fun toDomain(): ContentItem {
        return ContentItem(
            id = id ?: externalId,
            externalId = externalId,
            type = ContentType.fromString(type),
            title = title,
            overview = overview,
            posterUrl = posterUrl,
            backdropUrl = backdropUrl,
            rating = rating?.toDoubleOrNull(),
            duration = duration?.toIntOrNull(),
            genres = genres ?: emptyList(),
            trailerUrl = trailerUrl,
            emotionalTags = emotionalTags?.mapNotNull { tag ->
                try {
                    EmotionalTag.fromString(tag)
                } catch (e: IllegalArgumentException) {
                    null
                }
            } ?: emptyList(),
            externalUrl = externalUrl,
            // Music
            artist = artist,
            album = album,
            previewUrl = previewUrl,
            spotifyUrl = spotifyUrl,
            // Video
            channelName = channelName,
            youtubeUrl = youtubeUrl,
            thumbnailUrl = thumbnailUrl,
            // Place
            category = category,
            address = address,
            latitude = latitude,
            longitude = longitude,
            distance = distance,
            photoUrl = photoUrl
        )
    }
}
