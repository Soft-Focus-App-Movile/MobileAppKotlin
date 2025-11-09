package com.softfocus.features.library.domain.models

/**
 * Tipos de contenido multimedia disponibles en la biblioteca
 *
 * @property Movie Películas de TMDB
 * @property Music Canciones de Spotify
 * @property Video Videos de YouTube (meditación/bienestar)
 * @property Weather Información del clima y ubicación actual
 */
enum class ContentType {
    Movie,
    Music,
    Video,
    Weather;

    companion object {
        /**
         * Convierte un string del backend al enum correspondiente
         * @param value String value from backend
         * @return ContentType enum value
         * @throws IllegalArgumentException if value is not valid
         */
        fun fromString(value: String): ContentType {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid ContentType: $value")
            }
        }
    }
}
