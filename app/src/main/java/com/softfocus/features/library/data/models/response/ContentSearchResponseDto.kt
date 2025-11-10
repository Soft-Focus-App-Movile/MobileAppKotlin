package com.softfocus.features.library.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * DTO para respuesta paginada de búsqueda de contenido
 *
 * @property results Lista de contenidos encontrados
 * @property totalResults Número total de resultados
 * @property page Página actual
 */
data class ContentSearchResponseDto(
    @SerializedName("results")
    val results: List<ContentItemResponseDto>,

    @SerializedName("totalResults")
    val totalResults: Int,

    @SerializedName("page")
    val page: Int
)
