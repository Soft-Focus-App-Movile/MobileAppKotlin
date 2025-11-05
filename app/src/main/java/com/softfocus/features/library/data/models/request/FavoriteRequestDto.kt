package com.softfocus.features.library.data.models.request

import com.google.gson.annotations.SerializedName

/**
 * DTO para solicitud de agregar favorito
 *
 * @property contentId ID externo del contenido (ej: "tmdb-movie-27205")
 * @property contentType Tipo de contenido (Movie, Music, Video, Place)
 */
data class FavoriteRequestDto(
    @SerializedName("contentId")
    val contentId: String,

    @SerializedName("contentType")
    val contentType: String
)
