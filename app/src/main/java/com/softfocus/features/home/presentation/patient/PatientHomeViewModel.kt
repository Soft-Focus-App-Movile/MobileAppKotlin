package com.softfocus.features.home.presentation.patient

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.home.presentation.general.RecommendationsState
import com.softfocus.features.library.assignments.domain.repositories.AssignmentsRepository
import com.softfocus.features.library.assignments.presentation.AssignmentsUiState
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PatientHomeViewModel(
    private val libraryRepository: LibraryRepository,
    private val therapyRepository: TherapyRepository,
    private val searchRepository: SearchRepository,
    private val assignmentsRepository: AssignmentsRepository
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

    private val _assignmentsState = MutableStateFlow<AssignmentsUiState>(AssignmentsUiState.Loading)
    val assignmentsState: StateFlow<AssignmentsUiState> = _assignmentsState.asStateFlow()

    private var currentKeywordIndex = 0
    private val allContent = mutableListOf<ContentItem>()

    init {
        loadRecommendations()
        loadTherapistInfo()
        loadAssignments()
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

                            // Cargar el último mensaje recibido
                            val lastMessageResult = therapyRepository.getLastReceivedMessage()

                            lastMessageResult.onSuccess { chatMessage ->
                                if (chatMessage != null) {
                                    val formattedTime = formatMessageTime(chatMessage.timestamp)
                                    Log.d(TAG, "loadTherapistInfo: ✅ Último mensaje: ${chatMessage.content}")
                                    _therapistState.value = TherapistState.Success(
                                        psychologist = psychologist,
                                        lastMessage = chatMessage.content,
                                        lastMessageTime = formattedTime
                                    )
                                } else {
                                    Log.d(TAG, "loadTherapistInfo: No hay mensajes aún")
                                    _therapistState.value = TherapistState.Success(psychologist)
                                }
                            }.onFailure { error ->
                                Log.w(TAG, "loadTherapistInfo: ⚠️ Error al cargar último mensaje: ${error.message}")
                                // Aún así mostramos el psicólogo aunque falle el mensaje
                                _therapistState.value = TherapistState.Success(psychologist)
                            }
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

    private fun loadAssignments() {
        viewModelScope.launch {
            _assignmentsState.value = AssignmentsUiState.Loading

            assignmentsRepository.getAssignedContent(completed = false).fold(
                onSuccess = { assignments ->
                    val pending = assignments.count { !it.isCompleted }
                    val completedCount = assignments.count { it.isCompleted }

                    _assignmentsState.value = AssignmentsUiState.Success(
                        assignments = assignments,
                        pendingCount = pending,
                        completedCount = completedCount
                    )
                },
                onFailure = { exception ->
                    _assignmentsState.value = AssignmentsUiState.Error(
                        message = exception.message ?: "Error al cargar asignaciones"
                    )
                }
            )
        }
    }

    fun retryAssignments() {
        loadAssignments()
    }

    private fun formatMessageTime(timestamp: String): String {
        return try {
            // El timestamp viene en formato ISO-8601: "2025-01-13T17:30:00"
            val dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val timeFormatter = DateTimeFormatter.ofPattern("h:mma")
            dateTime.format(timeFormatter).lowercase()
        } catch (e: Exception) {
            Log.w(TAG, "formatMessageTime: Error al parsear timestamp: $timestamp", e)
            "Ahora"
        }
    }
}

sealed class TherapistState {
    object Loading : TherapistState()
    data class Success(
        val psychologist: Psychologist,
        val lastMessage: String? = null,
        val lastMessageTime: String? = null
    ) : TherapistState()
    object NoTherapist : TherapistState()
    data class Error(val message: String) : TherapistState()
}
