package com.softfocus.features.library.presentation.general.detail

import com.softfocus.features.library.domain.models.ContentItem

/**
 * Estados de UI para la pantalla de detalle de contenido
 */
sealed class ContentDetailUiState {
    /**
     * Estado inicial - cargando detalle
     */
    object Loading : ContentDetailUiState()

    /**
     * Estado exitoso - detalle cargado
     *
     * @property content Contenido principal
     * @property relatedContent Contenido relacionado
     * @property isFavorite Si el contenido está en favoritos
     */
    data class Success(
        val content: ContentItem,
        val relatedContent: List<ContentItem> = emptyList(),
        val isFavorite: Boolean = false
    ) : ContentDetailUiState()

    /**
     * Estado de error
     *
     * @property message Mensaje de error
     */
    data class Error(val message: String) : ContentDetailUiState()
}
