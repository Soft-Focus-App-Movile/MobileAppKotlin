package com.softfocus.features.home.presentation.general

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.domain.repositories.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class GeneralHomeViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    companion object {
        private const val TAG = "GeneralHomeVM"
        private const val RESULTS_PER_SEARCH = 15

        // Palabras clave rotativas enfocadas en bienestar y salud mental
        private val MOVIE_KEYWORDS = listOf("inspirational", "feel-good", "hope", "friendship", "family", "uplifting")
        private val MUSIC_KEYWORDS = listOf("meditation", "relaxing", "peaceful", "healing", "soothing", "calming")
    }

    private val _recommendationsState = MutableStateFlow<RecommendationsState>(RecommendationsState.Loading)
    val recommendationsState: StateFlow<RecommendationsState> = _recommendationsState.asStateFlow()

    private var currentKeywordIndex = 0
    private val allContent = mutableListOf<ContentItem>()

    init {
        loadRecommendations()
    }

    /**
     * Carga contenido variado usando búsquedas con palabras clave rotativas
     */
    fun loadRecommendations() {
        viewModelScope.launch {
            Log.d(TAG, "loadRecommendations: Iniciando carga de películas y música")
            _recommendationsState.value = RecommendationsState.Loading
            allContent.clear()

            try {
                // Solo buscar películas y música
                searchAndAddContent(ContentType.Movie, getNextKeyword(MOVIE_KEYWORDS))
                searchAndAddContent(ContentType.Music, getNextKeyword(MUSIC_KEYWORDS))

                // Filtrar solo contenido que tenga imagen válida
                val contentWithImages = allContent.filter { item ->
                    val hasValidImage = listOfNotNull(
                        item.posterUrl,
                        item.backdropUrl,
                        item.thumbnailUrl,
                        item.photoUrl
                    ).any { url ->
                        url.isNotBlank() && (url.startsWith("http://") || url.startsWith("https://"))
                    }

                    if (hasValidImage) {
                        Log.d(TAG, "✅ Con imagen: ${item.title}")
                    } else {
                        Log.d(TAG, "❌ Sin imagen: ${item.title}")
                    }

                    hasValidImage
                }.toMutableList()

                // Mezclar todo el contenido aleatoriamente para variedad
                contentWithImages.shuffle()

                Log.d(TAG, "loadRecommendations: ✅ Total items: ${allContent.size}, Con imágenes: ${contentWithImages.size}")

                _recommendationsState.value = if (contentWithImages.isEmpty()) {
                    RecommendationsState.Empty
                } else {
                    RecommendationsState.Success(contentWithImages)
                }

            } catch (e: Exception) {
                Log.e(TAG, "loadRecommendations: ❌ Excepción: ${e.message}", e)
                _recommendationsState.value = RecommendationsState.Error(
                    e.message ?: "Error al cargar recomendaciones"
                )
            }
        }
    }

    /**
     * Busca contenido por tipo y palabra clave
     */
    private suspend fun searchAndAddContent(contentType: ContentType, keyword: String) {
        try {
            Log.d(TAG, "searchAndAddContent: Buscando $contentType con keyword: $keyword")

            val result = repository.searchContent(
                query = keyword,
                contentType = contentType,
                limit = RESULTS_PER_SEARCH
            )

            result.onSuccess { content ->
                Log.d(TAG, "searchAndAddContent: ✅ $contentType - Recibidos ${content.size} items con '$keyword'")
                allContent.addAll(content)
            }.onFailure { error ->
                Log.w(TAG, "searchAndAddContent: ⚠️ $contentType - Error con '$keyword': ${error.message}")
                // No lanzar error, continuar con otros tipos
            }
        } catch (e: Exception) {
            Log.w(TAG, "searchAndAddContent: ⚠️ Excepción en $contentType: ${e.message}")
        }
    }

    /**
     * Obtiene la siguiente palabra clave de forma rotatoria
     */
    private fun getNextKeyword(keywords: List<String>): String {
        val keyword = keywords[currentKeywordIndex % keywords.size]
        currentKeywordIndex++
        return keyword
    }

    fun retry() {
        loadRecommendations()
    }
}

sealed class RecommendationsState {
    object Loading : RecommendationsState()
    data class Success(val recommendations: List<ContentItem>) : RecommendationsState()
    object Empty : RecommendationsState()
    data class Error(val message: String) : RecommendationsState()
}