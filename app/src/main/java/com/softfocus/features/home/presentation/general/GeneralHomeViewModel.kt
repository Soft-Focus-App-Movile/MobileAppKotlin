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

        // Palabras clave de respaldo si el endpoint de recomendaciones no devuelve contenido.
        // Solo keywords que devuelven resultados válidos en TMDB con language=es-PE
        // (el backend descarta películas sin poster/overview/rating/fecha).
        private val MOVIE_KEYWORDS = listOf("family", "hope", "friendship", "feel-good")
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
                // Solo películas y música, usando el mismo endpoint de recomendaciones
                // que la biblioteca (devuelve contenido curado con imágenes)
                loadAndAddContent(ContentType.Movie, MOVIE_KEYWORDS)
                loadAndAddContent(ContentType.Music, MUSIC_KEYWORDS)

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
     * Carga contenido recomendado por tipo; si no hay resultados,
     * usa búsqueda por palabra clave como respaldo
     */
    private suspend fun loadAndAddContent(contentType: ContentType, fallbackKeywords: List<String>) {
        try {
            Log.d(TAG, "loadAndAddContent: Cargando recomendaciones de $contentType")

            val result = repository.getRecommendedContent(
                contentType = contentType,
                limit = RESULTS_PER_SEARCH
            )

            val recommended = result.getOrNull().orEmpty()
            if (recommended.isNotEmpty()) {
                Log.d(TAG, "loadAndAddContent: ✅ $contentType - Recibidos ${recommended.size} items recomendados")
                allContent.addAll(recommended)
                return
            }

            // Respaldo: búsqueda por palabra clave
            val keyword = getNextKeyword(fallbackKeywords)
            Log.w(TAG, "loadAndAddContent: ⚠️ $contentType sin recomendaciones, buscando con '$keyword'")

            repository.searchContent(
                query = keyword,
                contentType = contentType,
                limit = RESULTS_PER_SEARCH
            ).onSuccess { content ->
                Log.d(TAG, "loadAndAddContent: ✅ $contentType - Recibidos ${content.size} items con '$keyword'")
                allContent.addAll(content)
            }.onFailure { error ->
                Log.w(TAG, "loadAndAddContent: ⚠️ $contentType - Error con '$keyword': ${error.message}")
                // No lanzar error, continuar con otros tipos
            }
        } catch (e: Exception) {
            Log.w(TAG, "loadAndAddContent: ⚠️ Excepción en $contentType: ${e.message}")
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