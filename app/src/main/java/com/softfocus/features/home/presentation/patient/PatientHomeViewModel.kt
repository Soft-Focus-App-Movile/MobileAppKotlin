package com.softfocus.features.home.presentation.patient

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.home.presentation.general.RecommendationsState
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.domain.repositories.LibraryRepository
import com.softfocus.features.search.domain.models.Psychologist
import com.softfocus.features.search.domain.repositories.SearchRepository
import com.softfocus.features.therapy.domain.repositories.TherapyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientHomeViewModel(
    private val libraryRepository: LibraryRepository,
    private val therapyRepository: TherapyRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {

    companion object {
        private const val TAG = "PatientHomeVM"
        private const val RESULTS_PER_SEARCH = 15
        private val MOVIE_KEYWORDS = listOf("inspirational", "feel-good", "hope", "friendship", "family", "uplifting")
        private val MUSIC_KEYWORDS = listOf("meditation", "relaxing", "peaceful", "healing", "soothing", "calming")
    }

    private val _recommendationsState = MutableStateFlow<RecommendationsState>(RecommendationsState.Loading)
    val recommendationsState: StateFlow<RecommendationsState> = _recommendationsState.asStateFlow()

    private val _therapistState = MutableStateFlow<TherapistState>(TherapistState.Loading)
    val therapistState: StateFlow<TherapistState> = _therapistState.asStateFlow()

    private var currentKeywordIndex = 0
    private val allContent = mutableListOf<ContentItem>()

    init {
        loadRecommendations()
        loadTherapistInfo()
    }

    private fun loadTherapistInfo() {
        viewModelScope.launch {
            Log.d(TAG, "loadTherapistInfo: Iniciando carga de información del terapeuta")
            _therapistState.value = TherapistState.Loading

            try {
                val relationshipResult = therapyRepository.getMyRelationship()

                relationshipResult.onSuccess { relationship ->
                    if (relationship != null && relationship.isActive) {
                        Log.d(TAG, "loadTherapistInfo: Relación encontrada, cargando datos del psicólogo ${relationship.psychologistId}")

                        val psychologistResult = searchRepository.getPsychologistById(relationship.psychologistId)

                        psychologistResult.onSuccess { psychologist ->
                            Log.d(TAG, "loadTherapistInfo: ✅ Psicólogo cargado: ${psychologist.fullName}")
                            _therapistState.value = TherapistState.Success(psychologist)
                        }.onFailure { error ->
                            Log.e(TAG, "loadTherapistInfo: ❌ Error al cargar psicólogo: ${error.message}")
                            _therapistState.value = TherapistState.Error(error.message ?: "Error al cargar datos del terapeuta")
                        }
                    } else {
                        Log.d(TAG, "loadTherapistInfo: No hay relación terapéutica activa")
                        _therapistState.value = TherapistState.NoTherapist
                    }
                }.onFailure { error ->
                    Log.e(TAG, "loadTherapistInfo: ❌ Error al obtener relación: ${error.message}")
                    _therapistState.value = TherapistState.Error(error.message ?: "Error al cargar terapeuta")
                }

            } catch (e: Exception) {
                Log.e(TAG, "loadTherapistInfo: ❌ Excepción: ${e.message}", e)
                _therapistState.value = TherapistState.Error(e.message ?: "Error al cargar terapeuta")
            }
        }
    }

    fun loadRecommendations() {
        viewModelScope.launch {
            Log.d(TAG, "loadRecommendations: Iniciando carga de películas y música")
            _recommendationsState.value = RecommendationsState.Loading
            allContent.clear()

            try {
                // Solo buscar películas y música
                searchAndAddContent(ContentType.Movie, getNextKeyword(MOVIE_KEYWORDS))
                searchAndAddContent(ContentType.Music, getNextKeyword(MUSIC_KEYWORDS))

                val contentWithImages = allContent.filter { item ->
                    val hasValidImage = listOfNotNull(
                        item.posterUrl,
                        item.backdropUrl,
                        item.thumbnailUrl,
                        item.photoUrl
                    ).any { url ->
                        url.isNotBlank() && (url.startsWith("http://") || url.startsWith("https://"))
                    }
                    hasValidImage
                }.toMutableList()

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

    private suspend fun searchAndAddContent(contentType: ContentType, keyword: String) {
        try {
            Log.d(TAG, "searchAndAddContent: Buscando $contentType con keyword: $keyword")

            val result = libraryRepository.searchContent(
                query = keyword,
                contentType = contentType,
                limit = RESULTS_PER_SEARCH
            )

            result.onSuccess { content ->
                Log.d(TAG, "searchAndAddContent: ✅ $contentType - Recibidos ${content.size} items con '$keyword'")
                allContent.addAll(content)
            }.onFailure { error ->
                Log.w(TAG, "searchAndAddContent: ⚠️ $contentType - Error con '$keyword': ${error.message}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "searchAndAddContent: ⚠️ Excepción en $contentType: ${e.message}")
        }
    }

    private fun getNextKeyword(keywords: List<String>): String {
        val keyword = keywords[currentKeywordIndex % keywords.size]
        currentKeywordIndex++
        return keyword
    }

    fun retry() {
        loadRecommendations()
    }

    fun retryTherapist() {
        loadTherapistInfo()
    }
}

sealed class TherapistState {
    object Loading : TherapistState()
    data class Success(val psychologist: Psychologist) : TherapistState()
    object NoTherapist : TherapistState()
    data class Error(val message: String) : TherapistState()
}
