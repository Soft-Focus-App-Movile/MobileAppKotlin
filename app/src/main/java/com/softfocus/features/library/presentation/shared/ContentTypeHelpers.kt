package com.softfocus.features.library.presentation.shared

import com.softfocus.features.library.domain.models.ContentType

/**
 * Extensiones y helpers para ContentType
 */

/**
 * Obtiene el emoji para cada tipo de contenido
 */

/**
 * Obtiene el nombre legible para cada tipo de contenido
 */
fun ContentType.getDisplayName(): String = when (this) {
    ContentType.Movie -> "Películas"
    ContentType.Music -> "Música"
    ContentType.Video -> "Videos"
    ContentType.Weather -> "Clima"
}
