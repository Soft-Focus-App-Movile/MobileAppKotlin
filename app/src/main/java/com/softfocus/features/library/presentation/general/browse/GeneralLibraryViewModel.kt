package com.softfocus.features.library.presentation.general.browse

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.common.result.Result
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.domain.models.EmotionalTag
import com.softfocus.features.library.domain.models.MockLibraryData
import com.softfocus.features.library.domain.repositories.LibraryRepository
import com.softfocus.features.library.presentation.general.browse.components.VideoCategory
import com.softfocus.features.tracking.domain.repository.TrackingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeneralLibraryViewModel(
    private val repository: LibraryRepository,
    private val therapyRepository: com.softfocus.features.therapy.domain.repositories.TherapyRepository,
    private val trackingRepository: TrackingRepository
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

    // Filtro de favoritos independiente por tipo de contenido
    private val _showFavoritesByType = MutableStateFlow(
        mapOf(
            ContentType.Movie to false,
            ContentType.Music to false,
            ContentType.Video to false
        )
    )
    val showFavoritesByType: StateFlow<Map<ContentType, Boolean>> = _showFavoritesByType.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _favoriteIdMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val favoriteIdMap: StateFlow<Map<String, String>> = _favoriteIdMap.asStateFlow()

    private val _selectedContentIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedContentIds: StateFlow<Set<String>> = _selectedContentIds.asStateFlow()

    private val _patients = MutableStateFlow<List<com.softfocus.features.therapy.domain.models.PatientDirectory>>(emptyList())
    val patients: StateFlow<List<com.softfocus.features.therapy.domain.models.PatientDirectory>> = _patients.asStateFlow()

    private val _patientsLoading = MutableStateFlow(false)
    val patientsLoading: StateFlow<Boolean> = _patientsLoading.asStateFlow()

    private val _patientsError = MutableStateFlow<String?>(null)
    val patientsError: StateFlow<String?> = _patientsError.asStateFlow()

    init {
        loadTrackingAndRecommendations()
        loadFavorites()
    }

    private fun loadTrackingAndRecommendations() {
        viewModelScope.launch {
            try {
                when (val result = trackingRepository.getTodayCheckIn()) {
                    is Result.Success -> {
                        val todayCheckIn = result.data
                        if (todayCheckIn.hasCompletedToday && todayCheckIn.checkIn != null) {
                            val checkIn = todayCheckIn.checkIn
                            val emotion = mapToEmotionalTag(
                                checkIn.emotionalLevel,
                                checkIn.energyLevel,
                                checkIn.symptoms
                            )
                            if (emotion != null) {
                                loadContentByEmotion(emotion)
                            } else {
                                loadAllContent()
                            }
                        } else {
                            loadAllContent()
                        }
                    }
                    is Result.Error -> {
                        loadAllContent()
                    }
                }
            } catch (e: Exception) {
                loadAllContent()
            }
        }
    }

    private fun mapToEmotionalTag(
        moodLevel: Int,
        energyLevel: Int,
        symptoms: List<String>
    ): EmotionalTag? {
        val symptomsText = symptoms.joinToString(" ").lowercase()

        if (symptomsText.contains("energia") || symptomsText.contains("energético") || symptomsText.contains("energetico")) {
            return EmotionalTag.Energetic
        }
        if (symptomsText.contains("tranquilo") || symptomsText.contains("calma") || symptomsText.contains("relajad")) {
            return EmotionalTag.Calm
        }
        if (symptomsText.contains("feliz") || symptomsText.contains("alegr") || symptomsText.contains("content")) {
            return EmotionalTag.Happy
        }

        return when {
            energyLevel >= 8 -> EmotionalTag.Energetic
            moodLevel >= 7 && energyLevel in 5..7 -> EmotionalTag.Happy
            else -> EmotionalTag.Calm
        }
    }

    private fun loadAllContent() {
        viewModelScope.launch {
            Log.d(TAG, "loadAllContent: Iniciando carga de contenido")

            _uiState.value = GeneralLibraryUiState.Loading

            try {
                val contentMap = mutableMapOf<ContentType, List<ContentItem>>()
                val errors = mutableListOf<String>()

                for (type in ContentType.values()) {
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

                val totalItems = contentMap.values.sumOf { it.size }
                Log.d(TAG, "loadAllContent: Total items cargados: $totalItems")
                Log.d(TAG, "loadAllContent: ContentMap keys: ${contentMap.keys}")
                contentMap.forEach { (type, items) ->
                    Log.d(TAG, "loadAllContent:   $type -> ${items.size} items")
                }

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
            val currentType = _selectedType.value
            Log.d(TAG, "loadContentByEmotion: Iniciando carga para emoción $emotion en tipo $currentType")
            _selectedEmotion.value = emotion
            _uiState.value = GeneralLibraryUiState.Loading

            try {
                val currentState = _uiState.value
                val existingContent = if (currentState is GeneralLibraryUiState.Success) {
                    currentState.contentByType
                } else {
                    emptyMap()
                }

                val contentMap = existingContent.toMutableMap()

                val result = repository.getRecommendedByEmotion(
                    emotion = emotion,
                    contentType = currentType,
                    limit = 20
                )

                result.onSuccess { content ->
                    Log.d(TAG, "loadContentByEmotion: ✅ $currentType - Recibidos ${content.size} items")
                    contentMap[currentType] = content

                    _uiState.value = GeneralLibraryUiState.Success(
                        contentByType = contentMap,
                        selectedType = currentType
                    )
                }.onFailure { error ->
                    Log.e(TAG, "loadContentByEmotion: ❌ $currentType - Error: ${error.message}", error)
                    _uiState.value = GeneralLibraryUiState.Error(
                        error.message ?: "Error al cargar contenido por emoción"
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
        val currentType = _selectedType.value
        _showFavoritesByType.value = _showFavoritesByType.value.toMutableMap().apply {
            put(currentType, false)
        }
        if (currentType == ContentType.Video) {
            _selectedVideoCategory.value = null
        }
        loadAllContent()
    }

    fun loadFavoriteContent() {
        viewModelScope.launch {
            val currentType = _selectedType.value
            Log.d(TAG, "loadFavoriteContent: Cargando favoritos para $currentType")

            // Activar favoritos solo para el tipo actual
            _showFavoritesByType.value = _showFavoritesByType.value.toMutableMap().apply {
                put(currentType, true)
            }
            _selectedEmotion.value = null
            _uiState.value = GeneralLibraryUiState.Loading

            try {
                repository.getFavorites().onSuccess { favorites ->
                    Log.d(TAG, "loadFavoriteContent: ✅ ${favorites.size} favoritos cargados")

                    val allFavoritesMap = favorites
                        .map { it.content }
                        .groupBy { it.type }

                    val currentState = _uiState.value
                    val existingContent = if (currentState is GeneralLibraryUiState.Success) {
                        currentState.contentByType
                    } else {
                        emptyMap()
                    }

                    val updatedContentMap = existingContent.toMutableMap()

                    updatedContentMap[currentType] = allFavoritesMap[currentType] ?: emptyList()

                    val totalItems = updatedContentMap[currentType]?.size ?: 0
                    Log.d(TAG, "loadFavoriteContent: Total items de $currentType: $totalItems")

                    _uiState.value = GeneralLibraryUiState.Success(
                        contentByType = updatedContentMap,
                        selectedType = currentType,
                        showFavorites = _showFavoritesByType.value[currentType] ?: false
                    )
                }.onFailure { error ->
                    Log.e(TAG, "loadFavoriteContent: ❌ Error: ${error.message}", error)
                    _uiState.value = GeneralLibraryUiState.Error(
                        error.message ?: "Error al cargar favoritos"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadFavoriteContent: ❌ Excepción: ${e.message}", e)
                _uiState.value = GeneralLibraryUiState.Error(
                    e.message ?: "Error al cargar favoritos"
                )
            }
        }
    }

    fun clearFavoritesFilter() {
        val currentType = _selectedType.value
        Log.d(TAG, "clearFavoritesFilter: Limpiando filtro de favoritos para $currentType")

        _showFavoritesByType.value = _showFavoritesByType.value.toMutableMap().apply {
            put(currentType, false)
        }

        if (currentType == ContentType.Video) {
            _selectedVideoCategory.value = null
        }

        if (_selectedEmotion.value != null) {
            loadContentByEmotion(_selectedEmotion.value!!)
        } else {
            loadAllContent()
        }
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

                    val currentState = _uiState.value
                    val existingContent = if (currentState is GeneralLibraryUiState.Success) {
                        currentState.contentByType
                    } else {
                        emptyMap()
                    }

                    val updatedContentMap = existingContent.toMutableMap().apply {
                        put(ContentType.Video, videos)
                    }

                    _uiState.value = GeneralLibraryUiState.Success(
                        contentByType = updatedContentMap,
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
            if (_selectedType.value == ContentType.Video) {
                _selectedVideoCategory.value = null
            }
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

            if (_selectedType.value == ContentType.Video) {
                _selectedVideoCategory.value = null
            }

            try {
                repository.searchContent(
                    query = query,
                    contentType = _selectedType.value,
                    emotionFilter = _selectedEmotion.value,
                    limit = 20
                ).onSuccess { results ->
                    Log.d(TAG, "searchContent: ✅ Encontrados ${results.size} resultados")

                    val currentState = _uiState.value
                    val existingContent = if (currentState is GeneralLibraryUiState.Success) {
                        currentState.contentByType
                    } else {
                        emptyMap()
                    }

                    val updatedContentMap = existingContent.toMutableMap().apply {
                        put(_selectedType.value, results)
                    }

                    _uiState.value = GeneralLibraryUiState.Success(
                        contentByType = updatedContentMap,
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
        Log.d(TAG, "selectContentType: Cambiando a $type")
        _selectedType.value = type

        if (type != ContentType.Video) {
            _selectedVideoCategory.value = null
        }

        val currentState = _uiState.value
        val isNewTypeFavoritesActive = _showFavoritesByType.value[type] ?: false

        Log.d(TAG, "selectContentType: Favoritos activos para $type = $isNewTypeFavoritesActive")

        if (currentState is GeneralLibraryUiState.Success) {
            _uiState.value = currentState.copy(
                selectedType = type,
                showFavorites = isNewTypeFavoritesActive
            )

            val hasContent = !currentState.contentByType[type].isNullOrEmpty()

            if (isNewTypeFavoritesActive && !hasContent) {
                Log.d(TAG, "selectContentType: Cargando favoritos para $type")
                loadFavoriteContent()
            } else if (!isNewTypeFavoritesActive && !hasContent) {
                Log.d(TAG, "selectContentType: Tipo $type vacío, cargando contenido normal")
                if (_selectedEmotion.value != null) {
                    loadContentByEmotion(_selectedEmotion.value!!)
                } else {
                    loadAllContent()
                }
            }
        } else {
            when {
                isNewTypeFavoritesActive -> {
                    Log.d(TAG, "selectContentType: Cargando favoritos de $type")
                    loadFavoriteContent()
                }
                _selectedEmotion.value != null -> {
                    Log.d(TAG, "selectContentType: Cargando por emoción")
                    loadContentByEmotion(_selectedEmotion.value!!)
                }
                else -> {
                    Log.d(TAG, "selectContentType: Cargando todo el contenido")
                    loadAllContent()
                }
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavorites().onSuccess { favorites ->
                _favoriteIds.value = favorites.map { it.content.externalId }.toSet()
                _favoriteIdMap.value = favorites.associate { it.content.externalId to it.id }
                Log.d(TAG, "loadFavorites: ${favorites.size} favoritos cargados, map: ${_favoriteIdMap.value}")
            }
        }
    }

    fun toggleFavorite(content: ContentItem) {
        viewModelScope.launch {
            val isFavorite = _favoriteIds.value.contains(content.externalId)
            Log.d(TAG, "toggleFavorite: ${content.title} - isFavorite actual: $isFavorite")

            if (isFavorite) {
                val favoriteId = _favoriteIdMap.value[content.externalId]
                if (favoriteId == null) {
                    Log.e(TAG, "toggleFavorite: ❌ No se encontró favoriteId para ${content.externalId}")
                    return@launch
                }

                _favoriteIds.value = _favoriteIds.value - content.externalId
                Log.d(TAG, "toggleFavorite: [OPTIMISTIC] Corazón desmarcado")
                Log.d(TAG, "toggleFavorite: Removiendo favorito con favoriteId: $favoriteId")

                repository.deleteFavorite(favoriteId).onSuccess {
                    Log.d(TAG, "toggleFavorite: ✅ Favorito removido exitosamente del backend")
                    _favoriteIdMap.value = _favoriteIdMap.value - content.externalId
                    loadFavorites()
                }.onFailure { error ->
                    Log.e(TAG, "toggleFavorite: ❌ Error al remover favorito: ${error.message}", error)
                    _favoriteIds.value = _favoriteIds.value + content.externalId
                    Log.d(TAG, "toggleFavorite: [ROLLBACK] Corazón restaurado")
                }
            } else {
                _favoriteIds.value = _favoriteIds.value + content.externalId
                Log.d(TAG, "toggleFavorite: [OPTIMISTIC] Corazón marcado")
                Log.d(TAG, "toggleFavorite: Agregando a favoritos - contentId: ${content.externalId}, type: ${content.type}")

                repository.addFavorite(
                    contentId = content.externalId,
                    contentType = content.type
                ).onSuccess { favorite ->
                    Log.d(TAG, "toggleFavorite: ✅ Favorito agregado exitosamente - favoriteId: ${favorite.id}")
                    _favoriteIdMap.value = _favoriteIdMap.value + (content.externalId to favorite.id)
                    loadFavorites()
                }.onFailure { error ->
                    Log.e(TAG, "toggleFavorite: ❌ Error al agregar favorito: ${error.message}", error)

                    if (error.message?.contains("ya está en favoritos", ignoreCase = true) == true) {
                        Log.d(TAG, "toggleFavorite: Ya estaba en favoritos, recargando para sincronizar...")
                        loadFavorites()
                    } else {
                        _favoriteIds.value = _favoriteIds.value - content.externalId
                        Log.d(TAG, "toggleFavorite: [ROLLBACK] Corazón desmarcado")
                    }
                }
            }
        }
    }

    suspend fun getMyRelationship(): Result<com.softfocus.features.therapy.domain.models.TherapeuticRelationship?> {
        return try {
            val result = therapyRepository.getMyRelationship()
            result.fold(
                onSuccess = { data -> Result.Success(data) },
                onFailure = { error -> Result.Error(error.message ?: "Error al obtener relación terapéutica") }
            )
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconocido")
        }
    }

    fun retry() {
        if (_selectedEmotion.value != null) {
            loadContentByEmotion(_selectedEmotion.value!!)
        } else {
            loadAllContent()
        }
    }

    fun refreshContent() {
        val currentType = _selectedType.value
        Log.d(TAG, "refreshContent: Refrescando contenido de $currentType")

        val isShowingFavorites = _showFavoritesByType.value[currentType] ?: false
        val hasEmotion = _selectedEmotion.value != null
        val hasVideoCategory = currentType == ContentType.Video && _selectedVideoCategory.value != null

        when {
            hasVideoCategory -> {
                _selectedVideoCategory.value?.let { loadContentByVideoCategory(it) }
            }
            isShowingFavorites -> {
                loadFavoriteContent()
            }
            hasEmotion -> {
                loadContentByEmotion(_selectedEmotion.value!!)
            }
            else -> {
                loadAllContent()
            }
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
