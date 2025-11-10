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

    @SerializedName("releaseDate")
    val releaseDate: String? = null,

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
     * Parsea la duración desde el formato del backend
     * Ejemplos: "148min" -> 148, "2 temporadas" -> 2
     */
    private fun parseDuration(durationString: String?): Int? {
        if (durationString.isNullOrBlank()) return null

        // Extraer el primer número de la cadena
        val numberMatch = Regex("\\d+").find(durationString)
        return numberMatch?.value?.toIntOrNull()
    }

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
            duration = parseDuration(duration),
            releaseDate = releaseDate,
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
            // Fallback para Spotify: si spotifyUrl es null o vacío, construirlo desde externalId
            spotifyUrl = spotifyUrl?.takeIf { it.isNotBlank() } ?: run {
                // externalId formato esperado: "spotify-track-TRACK_ID"
                if (externalId.startsWith("spotify-track-")) {
                    val trackId = externalId.removePrefix("spotify-track-")
                    if (trackId.isNotBlank()) {
                        "https://open.spotify.com/track/$trackId"
                    } else null
                } else null
            },
            // Video
            channelName = channelName,
            // Fallback para YouTube: si youtubeUrl es null o vacío, construirlo desde externalId
            youtubeUrl = youtubeUrl?.takeIf { it.isNotBlank() } ?: run {
                // externalId formato esperado: "youtube-video-VIDEO_ID"
                if (externalId.startsWith("youtube-video-")) {
                    val videoId = externalId.removePrefix("youtube-video-")
                    if (videoId.isNotBlank()) {
                        "https://www.youtube.com/watch?v=$videoId"
                    } else null
                } else null
            },
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
