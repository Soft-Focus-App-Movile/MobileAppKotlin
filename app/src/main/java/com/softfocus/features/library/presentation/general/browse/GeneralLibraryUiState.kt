package com.softfocus.features.library.presentation.general.browse

import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.domain.models.WeatherCondition

/**
 * Estados de UI para la pantalla de biblioteca general
 */
sealed class GeneralLibraryUiState {
    /**
     * Estado inicial - cargando contenido
     */
    object Loading : GeneralLibraryUiState()

    /**
     * Estado exitoso - contenido cargado
     *
     * @property contentByType Mapa de tipo de contenido a lista de items
     * @property selectedType Tipo de contenido seleccionado actualmente
     * @property weatherCondition Información del clima (solo para Places)
     * @property isSelectionMode Si está en modo selección (para psicólogos)
     * @property selectedIds IDs de contenido seleccionado (para psicólogos)
     * @property assignedContent Contenido asignado por el terapeuta (para pacientes)
     */
    data class Success(
        val contentByType: Map<ContentType, List<ContentItem>>,
        val selectedType: ContentType = ContentType.Movie,
        val weatherCondition: WeatherCondition? = null,
        val isSelectionMode: Boolean = false,
        val selectedIds: Set<String> = emptySet(),
        val assignedContent: List<ContentItem> = emptyList()
    ) : GeneralLibraryUiState() {
        /**
         * Obtiene el contenido del tipo seleccionado
         */
        fun getSelectedContent(): List<ContentItem> = contentByType[selectedType] ?: emptyList()

        /**
         * Verifica si un contenido está seleccionado
         */
        fun isContentSelected(contentId: String): Boolean = selectedIds.contains(contentId)
    }

    /**
     * Estado de error
     *
     * @property message Mensaje de error
     */
    data class Error(val message: String) : GeneralLibraryUiState()
}
