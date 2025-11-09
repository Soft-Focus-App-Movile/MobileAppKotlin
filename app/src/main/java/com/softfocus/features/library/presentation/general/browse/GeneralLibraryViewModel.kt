package com.softfocus.features.library.presentation.general.browse

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.domain.models.EmotionalTag
import com.softfocus.features.library.domain.models.MockLibraryData
import com.softfocus.features.library.domain.repositories.LibraryRepository
import com.softfocus.features.library.presentation.general.browse.components.VideoCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeneralLibraryViewModel(
    private val repository: LibraryRepository,
    private val therapyRepository: com.softfocus.features.therapy.domain.repositories.TherapyRepository
) : ViewModel() {

    companion object {
        private const val TAG = "GeneralLibraryVM"
    }

    private val _uiState = MutableStateFlow<GeneralLibraryUiState>(GeneralLibraryUiState.Loading)
    val uiState: StateFlow<GeneralLibraryUiState> = _uiState.asStateFlow()

    private val _selectedType = MutableStateFlow(ContentType.Movie)
    val selectedType: StateFlow<ContentType> = _selectedType.asStateFlow()

    private val _selectedEmotion = MutableStateFlow<EmotionalTag?>(null)
    val selectedEmotion: StateFlow<EmotionalTag?> = _selectedEmotion.asStateFlow()

    private val _selectedVideoCategory = MutableStateFlow<VideoCategory?>(null)
    val selectedVideoCategory: StateFlow<VideoCategory?> = _selectedVideoCategory.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _favoritesMap = MutableStateFlow<Map<String, String>>(emptyMap())

    private val _selectedContentIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedContentIds: StateFlow<Set<String>> = _selectedContentIds.asStateFlow()

    private val _patients = MutableStateFlow<List<com.softfocus.features.therapy.domain.models.PatientDirectory>>(emptyList())
    val patients: StateFlow<List<com.softfocus.features.therapy.domain.models.PatientDirectory>> = _patients.asStateFlow()

    private val _patientsLoading = MutableStateFlow(false)
    val patientsLoading: StateFlow<Boolean> = _patientsLoading.asStateFlow()

    private val _patientsError = MutableStateFlow<String?>(null)
    val patientsError: StateFlow<String?> = _patientsError.asStateFlow()

    init {
        loadAllContent()
        loadFavorites()
    }

    private fun loadAllContent() {
        viewModelScope.launch {
            Log.d(TAG, "loadAllContent: Iniciando carga de contenido")
            _uiState.value = GeneralLibraryUiState.Loading

            try {
                val contentMap = mutableMapOf<ContentType, List<ContentItem>>()
                val errors = mutableListOf<String>()

                for (type in ContentType.values()) {
                    if (type == ContentType.Weather) {
                        continue
                    }
                    Log.d(TAG, "loadAllContent: Cargando contenido para $type")

                    val result = repository.getRecommendedContent(
                        contentType = type,
                        limit = 20
                    )

                    result.onSuccess { content ->
                        Log.d(TAG, "loadAllContent: ✅ $type - Recibidos ${content.size} items")
                        contentMap[type] = content
                    }.onFailure { error ->
                        Log.e(TAG, "loadAllContent: ❌ $type - Error: ${error.message}", error)
                        errors.add("${type.name}: ${error.message}")
                    }
                }

                // Log del resultado final
                val totalItems = contentMap.values.sumOf { it.size }
                Log.d(TAG, "loadAllContent: Total items cargados: $totalItems")
                Log.d(TAG, "loadAllContent: ContentMap keys: ${contentMap.keys}")
                contentMap.forEach { (type, items) ->
                    Log.d(TAG, "loadAllContent:   $type -> ${items.size} items")
                }

                // Si todas las llamadas fallaron, mostrar error
                if (contentMap.isEmpty() && errors.isNotEmpty()) {
                    Log.w(TAG, "loadAllContent: Todas las llamadas fallaron")
                    _uiState.value = GeneralLibraryUiState.Error(
                        "Error al cargar contenido:\n${errors.joinToString("\n")}"
                    )
                } else {
                    Log.d(TAG, "loadAllContent: Estableciendo estado Success")
                    _uiState.value = GeneralLibraryUiState.Success(
                        contentByType = contentMap,
                        selectedType = _selectedType.value
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadAllContent: Excepción no manejada", e)
                _uiState.value = GeneralLibraryUiState.Error(
                    e.message ?: "Error al cargar contenido"
                )
            }
        }
    }

    fun loadContentByEmotion(emotion: EmotionalTag) {
        viewModelScope.launch {
            Log.d(TAG, "loadContentByEmotion: Iniciando carga para emoción $emotion")
            _selectedEmotion.value = emotion
            _uiState.value = GeneralLibraryUiState.Loading

            try {
                val contentMap = mutableMapOf<ContentType, List<ContentItem>>()
                val errors = mutableListOf<String>()

                for (type in ContentType.values()) {
                    if (type == ContentType.Weather) {
                        continue
                    }
                    Log.d(TAG, "loadContentByEmotion: Cargando $type con emoción $emotion")

                    val result = repository.getRecommendedByEmotion(
                        emotion = emotion,
                        contentType = type,
                        limit = 20
                    )

                    result.onSuccess { content ->
                        Log.d(TAG, "loadContentByEmotion: ✅ $type - Recibidos ${content.size} items")
                        contentMap[type] = content
                    }.onFailure { error ->
                        Log.e(TAG, "loadContentByEmotion: ❌ $type - Error: ${error.message}", error)
                        errors.add("${type.name}: ${error.message}")
                    }
                }

                // Log del resultado final
                val totalItems = contentMap.values.sumOf { it.size }
                Log.d(TAG, "loadContentByEmotion: Total items cargados: $totalItems")

                // Si todas las llamadas fallaron, mostrar error
                if (contentMap.isEmpty() && errors.isNotEmpty()) {
                    Log.w(TAG, "loadContentByEmotion: Todas las llamadas fallaron")
                    _uiState.value = GeneralLibraryUiState.Error(
                        "Error al cargar contenido por emoción:\n${errors.joinToString("\n")}"
                    )
                } else {
                    Log.d(TAG, "loadContentByEmotion: Estableciendo estado Success")
                    _uiState.value = GeneralLibraryUiState.Success(
                        contentByType = contentMap,
                        selectedType = _selectedType.value
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadContentByEmotion: Excepción no manejada", e)
                _uiState.value = GeneralLibraryUiState.Error(
                    e.message ?: "Error al cargar contenido"
                )
            }
        }
    }

    fun clearEmotionFilter() {
        _selectedEmotion.value = null
        loadAllContent()
    }

    fun loadContentByVideoCategory(category: VideoCategory) {
        viewModelScope.launch {
            Log.d(TAG, "loadContentByVideoCategory: Iniciando búsqueda para categoría ${category.displayName}")
            _selectedVideoCategory.value = category
            _uiState.value = GeneralLibraryUiState.Loading

            try {
                repository.searchContent(
                    query = category.queryText,
                    contentType = ContentType.Video,
                    emotionFilter = null,
                    limit = 20
                ).onSuccess { videos ->
                    Log.d(TAG, "loadContentByVideoCategory: ✅ Encontrados ${videos.size} videos")
                    val contentMap = mapOf(ContentType.Video to videos)
                    _uiState.value = GeneralLibraryUiState.Success(
                        contentByType = contentMap,
                        selectedType = ContentType.Video
                    )
                }.onFailure { error ->
                    Log.e(TAG, "loadContentByVideoCategory: ❌ Error: ${error.message}", error)
                    _uiState.value = GeneralLibraryUiState.Error(
                        error.message ?: "Error al cargar videos"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadContentByVideoCategory: ❌ Excepción: ${e.message}", e)
                _uiState.value = GeneralLibraryUiState.Error(
                    e.message ?: "Error al cargar videos"
                )
            }
        }
    }

    fun searchContent(query: String) {
        if (query.isBlank()) {
            Log.d(TAG, "searchContent: Query vacío, recargando contenido")
            if (_selectedEmotion.value != null) {
                loadContentByEmotion(_selectedEmotion.value!!)
            } else {
                loadAllContent()
            }
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "searchContent: Buscando '$query' en tipo ${_selectedType.value}")
            _uiState.value = GeneralLibraryUiState.Loading

            try {
                repository.searchContent(
                    query = query,
                    contentType = _selectedType.value,
                    emotionFilter = _selectedEmotion.value,
                    limit = 20
                ).onSuccess { results ->
                    Log.d(TAG, "searchContent: ✅ Encontrados ${results.size} resultados")
                    val contentMap = mapOf(_selectedType.value to results)
                    _uiState.value = GeneralLibraryUiState.Success(
                        contentByType = contentMap,
                        selectedType = _selectedType.value
                    )
                }.onFailure { error ->
                    Log.e(TAG, "searchContent: ❌ Error: ${error.message}", error)
                    _uiState.value = GeneralLibraryUiState.Error(
                        error.message ?: "Error en la búsqueda"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "searchContent: ❌ Excepción: ${e.message}", e)
                _uiState.value = GeneralLibraryUiState.Error(
                    e.message ?: "Error en la búsqueda"
                )
            }
        }
    }

    fun selectContentType(type: ContentType) {
        _selectedType.value = type
        val currentState = _uiState.value
        if (currentState is GeneralLibraryUiState.Success) {
            _uiState.value = currentState.copy(selectedType = type)
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavorites().onSuccess { favorites ->
                _favoriteIds.value = favorites.map { it.content.externalId }.toSet()
                _favoritesMap.value = favorites.associate { it.content.externalId to it.id }
            }
        }
    }

    fun toggleFavorite(content: ContentItem) {
        viewModelScope.launch {
            val isFavorite = _favoriteIds.value.contains(content.externalId)

            if (isFavorite) {
                val favoriteId = _favoritesMap.value[content.externalId]
                favoriteId?.let {
                    repository.deleteFavorite(it).onSuccess {
                        _favoriteIds.value = _favoriteIds.value - content.externalId
                        _favoritesMap.value = _favoritesMap.value - content.externalId
                    }
                }
            } else {
                repository.addFavorite(
                    contentId = content.externalId,
                    contentType = content.type
                ).onSuccess { favorite ->
                    _favoriteIds.value = _favoriteIds.value + content.externalId
                    _favoritesMap.value = _favoritesMap.value + (content.externalId to favorite.id)
                }
            }
        }
    }

    suspend fun getMyRelationship(): Result<com.softfocus.features.therapy.domain.models.TherapeuticRelationship?> {
        return therapyRepository.getMyRelationship()
    }

    fun loadWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                repository.getWeather(latitude, longitude).onSuccess { weather ->
                    val currentState = _uiState.value
                    if (currentState is GeneralLibraryUiState.Success) {
                        _uiState.value = currentState.copy(weatherCondition = weather)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadWeather: Error: ${e.message}", e)
            }
        }
    }

    fun retry() {
        if (_selectedEmotion.value != null) {
            loadContentByEmotion(_selectedEmotion.value!!)
        } else {
            loadAllContent()
        }
    }

    // ====================
    // PSYCHOLOGIST MULTI-SELECT FUNCTIONS
    // ====================

    /**
     * Alterna la selección de un contenido (para psicólogos)
     */
    fun toggleContentSelection(contentId: String) {
        _selectedContentIds.value = if (_selectedContentIds.value.contains(contentId)) {
            _selectedContentIds.value - contentId
        } else {
            _selectedContentIds.value + contentId
        }
        Log.d(TAG, "toggleContentSelection: ${_selectedContentIds.value.size} items seleccionados")
    }

    fun clearSelection() {
        _selectedContentIds.value = emptySet()
        Log.d(TAG, "clearSelection: Selección limpiada")
    }

    fun loadPatients() {
        viewModelScope.launch {
            _patientsLoading.value = true
            _patientsError.value = null

            therapyRepository.getMyPatients().fold(
                onSuccess = { patientList ->
                    _patients.value = patientList
                    _patientsLoading.value = false
                    Log.d(TAG, "loadPatients: ${patientList.size} pacientes cargados")
                },
                onFailure = { error ->
                    _patientsError.value = error.message ?: "Error al cargar pacientes"
                    _patientsLoading.value = false
                    Log.e(TAG, "loadPatients: Error: ${error.message}", error)
                }
            )
        }
    }

    /**
     * Asigna el contenido seleccionado a múltiples pacientes
     */
    fun assignContentToPatients(
        patientIds: List<String>,
        notes: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val selectedIds = _selectedContentIds.value
            Log.d(TAG, "assignContentToPatients: Asignando ${selectedIds.size} items a ${patientIds.size} paciente(s)")

            if (selectedIds.isEmpty()) {
                onError("No hay contenido seleccionado")
                return@launch
            }

            val currentState = _uiState.value
            if (currentState !is GeneralLibraryUiState.Success) {
                onError("Estado inválido")
                return@launch
            }

            val allContent = currentState.contentByType.values.flatten()

            try {
                var successCount = 0
                var errorCount = 0

                for (contentId in selectedIds) {
                    val content = allContent.find { it.id == contentId || it.externalId == contentId }
                    if (content == null) {
                        Log.w(TAG, "assignContentToPatients: Contenido no encontrado: $contentId")
                        errorCount++
                        continue
                    }

                    repository.assignContent(
                        patientIds = patientIds,
                        contentId = content.externalId,
                        contentType = content.type,
                        notes = notes
                    ).fold(
                        onSuccess = { assignmentIds ->
                            Log.d(TAG, "assignContentToPatients: ✅ ${assignmentIds.size} asignaciones creadas para ${content.title}")
                            successCount++
                        },
                        onFailure = { error ->
                            Log.e(TAG, "assignContentToPatients: ❌ Error asignando ${content.title}: ${error.message}")
                            errorCount++
                        }
                    )
                }

                if (errorCount == 0) {
                    clearSelection()
                    onSuccess()
                    Log.d(TAG, "assignContentToPatients: ✅ Todas las asignaciones exitosas ($successCount)")
                } else {
                    onError("$successCount exitosas, $errorCount fallidas")
                }
            } catch (e: Exception) {
                Log.e(TAG, "assignContentToPatients: ❌ Excepción: ${e.message}", e)
                onError(e.message ?: "Error al asignar contenido")
            }
        }
    }
}
