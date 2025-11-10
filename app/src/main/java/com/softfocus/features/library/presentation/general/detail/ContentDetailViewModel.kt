package com.softfocus.features.library.presentation.general.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.repositories.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de detalle de contenido
 *
 * @param contentId ID del contenido a mostrar
 * @param repository Repositorio de library
 */
class ContentDetailViewModel(
    private val contentId: String,
    private val repository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContentDetailUiState>(ContentDetailUiState.Loading)
    val uiState: StateFlow<ContentDetailUiState> = _uiState.asStateFlow()

    private var currentFavoriteId: String? = null

    init {
        loadContentDetail()
    }

    private fun loadContentDetail() {
        viewModelScope.launch {
            _uiState.value = ContentDetailUiState.Loading

            repository.getContentById(contentId)
                .onSuccess { content ->
                    val relatedContent = mutableListOf<ContentItem>()

                    // Si el contenido tiene emotional tags, usar el primero para filtrar relacionados
                    if (content.emotionalTags.isNotEmpty()) {
                        repository.getRecommendedByEmotion(
                            emotion = content.emotionalTags.first(),
                            contentType = content.type,
                            limit = 10
                        ).onSuccess { related ->
                            relatedContent.addAll(related.filter { it.externalId != content.externalId })
                        }
                    } else {
                        // Si no tiene tags emocionales, usar recomendaciones generales
                        repository.getRecommendedContent(
                            contentType = content.type,
                            limit = 10
                        ).onSuccess { related ->
                            relatedContent.addAll(related.filter { it.externalId != content.externalId })
                        }
                    }

                    var isFavorite = false
                    repository.getFavorites().onSuccess { favorites ->
                        val favorite = favorites.find { it.content.externalId == content.externalId }
                        isFavorite = favorite != null
                        currentFavoriteId = favorite?.id
                    }

                    _uiState.value = ContentDetailUiState.Success(
                        content = content,
                        relatedContent = relatedContent.take(5),
                        isFavorite = isFavorite
                    )
                }
                .onFailure { error ->
                    _uiState.value = ContentDetailUiState.Error(
                        error.message ?: "Error al cargar contenido"
                    )
                }
        }
    }

    /**
     * Alterna el estado de favorito
     */
    fun toggleFavorite() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ContentDetailUiState.Success) {
                val content = currentState.content
                val isFavorite = currentState.isFavorite

                if (isFavorite) {
                    currentFavoriteId?.let { favId ->
                        repository.deleteFavorite(favId).onSuccess {
                            currentFavoriteId = null
                            _uiState.value = currentState.copy(isFavorite = false)
                        }
                    }
                } else {
                    repository.addFavorite(
                        contentId = content.externalId,
                        contentType = content.type
                    ).onSuccess { favorite ->
                        currentFavoriteId = favorite.id
                        _uiState.value = currentState.copy(isFavorite = true)
                    }
                }
            }
        }
    }

    /**
     * Reintenta cargar el contenido
     */
    fun retry() {
        loadContentDetail()
    }
}
