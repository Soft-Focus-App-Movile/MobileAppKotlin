package com.softfocus.features.library.domain.models

import java.time.LocalDateTime

/**
 * Entidad de dominio que representa un contenido marcado como favorito
 *
 * @property id ID único del favorito en MongoDB
 * @property userId ID del usuario que marcó como favorito
 * @property content Contenido completo embebido
 * @property addedAt Fecha y hora en que se agregó a favoritos
 */
data class Favorite(
    val id: String,
    val userId: String,
    val content: ContentItem,
    val addedAt: LocalDateTime
) {
    /**
     * Verifica si este favorito pertenece al usuario especificado
     */
    fun belongsToUser(userId: String): Boolean = this.userId == userId
}
