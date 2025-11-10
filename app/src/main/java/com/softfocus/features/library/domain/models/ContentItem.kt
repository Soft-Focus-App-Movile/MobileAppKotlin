package com.softfocus.features.library.domain.models

/**
 * Entidad de dominio que representa un item de contenido multimedia
 *
 * @property id ID único del contenido en MongoDB
 * @property externalId ID externo (ej: "tmdb-movie-27205", "spotify-track-xxx")
 * @property type Tipo de contenido
 * @property title Título del contenido
 * @property overview Descripción/sinopsis
 * @property posterUrl URL del póster/imagen principal
 * @property backdropUrl URL de la imagen de fondo
 * @property rating Calificación (0-10)
 * @property duration Duración en minutos
 * @property genres Lista de géneros
 * @property trailerUrl URL del trailer (si aplica)
 * @property emotionalTags Etiquetas emocionales asociadas
 * @property externalUrl URL externa al contenido original
 *
 * // Propiedades específicas de Music
 * @property artist Artista (Music)
 * @property album Álbum (Music)
 * @property previewUrl URL de preview de 30s (Music)
 * @property spotifyUrl URL de Spotify (Music)
 *
 * // Propiedades específicas de Video
 * @property channelName Nombre del canal (Video)
 * @property youtubeUrl URL de YouTube (Video)
 * @property thumbnailUrl URL del thumbnail (Video)
 *
 * // Propiedades específicas de Place
 * @property category Categoría del lugar (Place)
 * @property address Dirección (Place)
 * @property latitude Latitud (Place)
 * @property longitude Longitud (Place)
 * @property distance Distancia en metros (Place)
 * @property photoUrl URL de la foto (Place)
 */
data class ContentItem(
    val id: String,
    val externalId: String,
    val type: ContentType,
    val title: String,
    val overview: String? = null,
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val rating: Double? = null,
    val duration: Int? = null,
    val releaseDate: String? = null,
    val genres: List<String> = emptyList(),
    val trailerUrl: String? = null,
    val emotionalTags: List<EmotionalTag> = emptyList(),
    val externalUrl: String? = null,

    // Music specific
    val artist: String? = null,
    val album: String? = null,
    val previewUrl: String? = null,
    val spotifyUrl: String? = null,

    // Video specific
    val channelName: String? = null,
    val youtubeUrl: String? = null,
    val thumbnailUrl: String? = null,

    // Place specific
    val category: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val distance: Double? = null,
    val photoUrl: String? = null
) {
    fun isMovie(): Boolean = type == ContentType.Movie

    fun isMusic(): Boolean = type == ContentType.Music

    fun isVideo(): Boolean = type == ContentType.Video

    fun getMainImageUrl(): String? = when (type) {
        ContentType.Movie -> posterUrl
        ContentType.Music -> posterUrl
        ContentType.Video -> thumbnailUrl
        ContentType.Weather -> null
    }

    fun getFormattedDuration(): String? = duration?.let {
        val hours = it / 60
        val minutes = it % 60
        when {
            hours > 0 -> "${hours}h ${minutes}min"
            else -> "${minutes}min"
        }
    }

    fun getFormattedRating(): String? = rating?.let {
        String.format("%.1f/10", it)
    }

    fun getReleaseYear(): String? = releaseDate?.let {
        if (it.length >= 4) it.substring(0, 4) else null
    }
}
