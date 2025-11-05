package com.softfocus.features.library.data.models.request

import com.google.gson.annotations.SerializedName

/**
 * DTO para solicitud de búsqueda de contenido
 *
 * @property query Término de búsqueda
 * @property contentType Tipo de contenido a buscar (Movie, Music, Video, Place)
 * @property emotionFilter Filtro opcional por emoción
 * @property limit Número máximo de resultados (1-100, default: 20)
 */
data class ContentSearchRequestDto(
    @SerializedName("query")
    val query: String,

    @SerializedName("contentType")
    val contentType: String,

    @SerializedName("emotionFilter")
    val emotionFilter: String? = null,

    @SerializedName("limit")
    val limit: Int = 20
)
