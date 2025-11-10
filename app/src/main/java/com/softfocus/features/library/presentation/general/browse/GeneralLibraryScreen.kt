package com.softfocus.features.library.presentation.general.browse

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.library.assignments.presentation.AssignedContentScreen
import com.softfocus.features.library.assignments.presentation.AssignmentsViewModel
import com.softfocus.features.library.assignments.presentation.di.AssignmentsPresentationModule
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.domain.models.EmotionalTag
import com.softfocus.features.library.presentation.di.libraryViewModelWithTherapy
import com.softfocus.features.library.presentation.general.browse.components.AssignPatientBottomSheet
import com.softfocus.features.library.presentation.general.browse.components.AssignTaskButton
import com.softfocus.features.library.presentation.general.browse.components.CategoryIcons
import com.softfocus.features.library.presentation.general.browse.components.FilterBottomSheet
import com.softfocus.features.library.presentation.general.browse.components.LibraryContent
import com.softfocus.features.library.presentation.general.browse.components.LibraryTabs
import com.softfocus.features.library.presentation.general.browse.components.LibraryTopBar
import com.softfocus.features.library.presentation.general.browse.components.SearchBarWithFilter
import com.softfocus.features.library.presentation.general.browse.components.VideoCategory
import com.softfocus.features.library.presentation.shared.getDisplayName
import com.softfocus.ui.theme.*

/**
 * Pantalla principal de biblioteca para usuarios General
 *
 * Muestra tabs con diferentes tipos de contenido:
 * - Películas
 * - Música
 * - Videos
 * - Lugares
 *
 * @param onContentClick Callback al hacer clic en un contenido
 * @param modifier Modificador opcional
 * @param viewModel ViewModel de la pantalla
 */
@Composable
fun GeneralLibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: GeneralLibraryViewModel = libraryViewModelWithTherapy { libRepo, therapyRepo ->
        GeneralLibraryViewModel(libRepo, therapyRepo)
    },
    onContentClick: (ContentItem) -> Unit = {},
) {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val userType = remember { userSession.getUser()?.userType }

    val uiState by viewModel.uiState.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedEmotion by viewModel.selectedEmotion.collectAsState()
    val selectedVideoCategory by viewModel.selectedVideoCategory.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val selectedContentIds by viewModel.selectedContentIds.collectAsState()
    val patients by viewModel.patients.collectAsState()
    val patientsLoading by viewModel.patientsLoading.collectAsState()
    val patientsError by viewModel.patientsError.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAssignBottomSheet by remember { mutableStateOf(false) }

    val isPsychologist = userType == UserType.PSYCHOLOGIST
    val isSelectionMode = isPsychologist && selectedContentIds.isNotEmpty()

    val hasTherapist = remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        if (!isPsychologist) {
            val relationshipResult = viewModel.getMyRelationship()
            relationshipResult.onSuccess { relationship ->
                hasTherapist.value = relationship != null && relationship.isActive
            }.onFailure {
                hasTherapist.value = false
            }
        }
    }

    LaunchedEffect(selectedType) {
        if (selectedType == ContentType.Weather) {
            val location = com.softfocus.core.utils.LocationHelper.getCurrentLocation(context)
            val latitude = location?.latitude ?: -12.0464
            val longitude = location?.longitude ?: -77.0428
            viewModel.loadWeather(latitude, longitude)
        }
    }

    val isPatient by remember { androidx.compose.runtime.derivedStateOf { hasTherapist.value == true } }

    val assignmentsViewModel: AssignmentsViewModel? = if (isPatient) {
        val retrofit = remember { com.softfocus.features.library.presentation.di.getRetrofitInstance() }
        val factory = remember { AssignmentsPresentationModule.provideAssignmentsViewModelFactory(context, retrofit) }
        viewModel(factory = factory)
    } else null


    LaunchedEffect(searchQuery) {
        kotlinx.coroutines.delay(500)
        viewModel.searchContent(searchQuery)
    }

    LaunchedEffect(isPatient, assignmentsViewModel) {
        if (isPatient && assignmentsViewModel != null) {
            assignmentsViewModel.loadAssignedContent(completed = null)
        }
    }

    GeneralLibraryScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedType = selectedType,
        selectedEmotion = selectedEmotion,
        selectedVideoCategory = selectedVideoCategory,
        favoriteIds = favoriteIds,
        searchQuery = searchQuery,
        userType = userType,
        isPatient = isPatient,
        isSelectionMode = isSelectionMode,
        selectedContentIds = selectedContentIds,
        assignmentsViewModel = assignmentsViewModel,
        onSearchQueryChange = { searchQuery = it },
        onTabSelected = { viewModel.selectContentType(it) },
        onFilterClear = { viewModel.clearEmotionFilter() },
        onEmotionSelected = { viewModel.loadContentByEmotion(it) },
        onVideoCategorySelected = { viewModel.loadContentByVideoCategory(it) },
        onRetry = { viewModel.retry() },
        onFavoriteClick = { viewModel.toggleFavorite(it) },
        onContentClick = {
            if (isSelectionMode) {
                viewModel.toggleContentSelection(it.id)
            } else {
                onContentClick(it)
            }
        },
        onContentSelectionToggle = {
            viewModel.toggleContentSelection(it.id)
        },
        onAssignTaskClick = {
            viewModel.loadPatients()
            showAssignBottomSheet = true
        }
    )

    if (showAssignBottomSheet && isPsychologist) {
        AssignPatientBottomSheet(
            selectedCount = selectedContentIds.size,
            patients = patients,
            isLoading = patientsLoading,
            errorMessage = patientsError,
            onPatientSelected = { patientId, patientName ->
                viewModel.assignContentToPatients(
                    patientIds = listOf(patientId),
                    notes = null,
                    onSuccess = {
                        Toast.makeText(context, "Contenido asignado a $patientName", Toast.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onDismiss = { showAssignBottomSheet = false },
            onRetry = { viewModel.loadPatients() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralLibraryScreenContent(
    uiState: GeneralLibraryUiState,
    selectedType: ContentType,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (ContentType) -> Unit,
    favoriteIds: Set<String>,
    onContentClick: (ContentItem) -> Unit,
    onFavoriteClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier,
    selectedEmotion: EmotionalTag? = null,
    selectedVideoCategory: VideoCategory? = null,
    onFilterClear: () -> Unit = {},
    onEmotionSelected: (EmotionalTag) -> Unit = {},
    onVideoCategorySelected: (VideoCategory) -> Unit = {},
    onRetry: () -> Unit = {},
    userType: UserType? = null,
    isPatient: Boolean = false,
    isSelectionMode: Boolean = false,
    selectedContentIds: Set<String> = emptySet(),
    assignmentsViewModel: AssignmentsViewModel? = null,
    onContentSelectionToggle: (ContentItem) -> Unit = {},
    onAssignTaskClick: () -> Unit = {}
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(if (isPatient) "assignments" else "content") }

    val isPsychologist = userType == UserType.PSYCHOLOGIST

    val availableTabs = remember(userType) {
        when (userType) {
            UserType.PSYCHOLOGIST -> listOf(ContentType.Movie, ContentType.Music, ContentType.Video)
            else -> ContentType.entries.toList()
        }
    }

    Scaffold(
        topBar = {
            LibraryTopBar(
                isPsychologist = isPsychologist,
                isSelectionMode = isSelectionMode,
                onCancelSelection = {
                    // Clear all selections
                    selectedContentIds.toList().forEach { id ->
                        uiState.let { state ->
                            if (state is GeneralLibraryUiState.Success) {
                                state.getSelectedContent().find { it.id == id }?.let {
                                    onContentSelectionToggle(it)
                                }
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (isPsychologist && isSelectionMode) {
                AssignTaskButton(
                    selectedCount = selectedContentIds.size,
                    onClick = onAssignTaskClick
                )
            }
        },
        modifier = modifier,
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LibraryTabs(
                isPatient = isPatient,
                currentTab = currentTab,
                onTabChange = { currentTab = it },
                selectedType = selectedType,
                availableTabs = availableTabs,
                onContentTypeSelected = onTabSelected
            )

            when (selectedType) {
                ContentType.Video -> {
                    CategoryIcons(
                        selectedCategory = selectedVideoCategory,
                        onCategoryClick = onVideoCategorySelected,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                ContentType.Weather -> {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                else -> {
                    SearchBarWithFilter(
                        searchQuery = searchQuery,
                        onSearchQueryChange = onSearchQueryChange,
                        onFilterClick = { showFilterSheet = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isPatient && currentTab == "assignments" && assignmentsViewModel != null) {
                AssignedContentScreen(
                    viewModel = assignmentsViewModel,
                    onContentClick = { contentId, contentType ->
                        onContentClick(ContentItem(
                            id = contentId,
                            externalId = contentId,
                            type = contentType,
                            title = "",
                            overview = null,
                            posterUrl = null,
                            rating = null,
                            duration = null,
                            genres = emptyList()
                        ))
                    }
                )
            } else {
                LibraryContent(
                    uiState = uiState,
                    selectedType = selectedType,
                    searchQuery = searchQuery,
                    favoriteIds = favoriteIds,
                    selectedContentIds = selectedContentIds,
                    isSelectionMode = isSelectionMode,
                    isPsychologist = isPsychologist,
                    onContentClick = onContentClick,
                    onContentLongClick = onContentSelectionToggle,
                    onFavoriteClick = onFavoriteClick,
                    onRetry = onRetry
                )
            }
        }
    }

    // Bottom sheet de filtros
    if (showFilterSheet) {
        FilterBottomSheet(
            selectedEmotion = selectedEmotion,
            onEmotionSelected = {
                if (it != null) {
                    onEmotionSelected(it)
                } else {
                    onFilterClear()
                }
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Preview(
    name = "General Library Screen - Success",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun GeneralLibraryScreenPreview() {
    SoftFocusMobileTheme {
        GeneralLibraryScreenContent(
            selectedType = ContentType.Movie,
            searchQuery = "",
            onSearchQueryChange = {},
            onTabSelected = {},
            selectedVideoCategory = null,
            uiState = GeneralLibraryUiState.Success(
                contentByType = mapOf(
                    ContentType.Movie to listOf(
                        ContentItem(
                            id = "1",
                            externalId = "tmdb-movie-550",
                            type = ContentType.Movie,
                            title = "El Origen",
                            overview = "Un ladrón que roba secretos corporativos",
                            posterUrl = null,
                            rating = 8.8,
                            duration = 148,
                            genres = listOf("Ciencia ficción", "Acción")
                        ),
                        ContentItem(
                            id = "2",
                            externalId = "tmdb-movie-551",
                            type = ContentType.Movie,
                            title = "Interestelar",
                            overview = "Un equipo de exploradores viaja en el espacio",
                            posterUrl = null,
                            rating = 8.6,
                            duration = 169,
                            genres = listOf("Ciencia ficción", "Drama")
                        ),
                        ContentItem(
                            id = "3",
                            externalId = "tmdb-movie-552",
                            type = ContentType.Movie,
                            title = "Matrix",
                            overview = "La realidad no es lo que parece",
                            posterUrl = null,
                            rating = 8.7,
                            duration = 136,
                            genres = listOf("Acción", "Sci-Fi")
                        ),
                        ContentItem(
                            id = "4",
                            externalId = "tmdb-movie-553",
                            type = ContentType.Movie,
                            title = "Blade Runner 2049",
                            overview = "Un descubrimiento que cambia todo",
                            posterUrl = null,
                            rating = 8.0,
                            duration = 164,
                            genres = listOf("Ciencia ficción")
                        )
                    ),
                    ContentType.Music to emptyList(),
                    ContentType.Video to emptyList(),
                    ContentType.Weather to emptyList()
                ),
                selectedType = ContentType.Movie
            ),
            favoriteIds = setOf("tmdb-movie-550", "tmdb-movie-552"),
            onContentClick = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(
    name = "General Library Screen - Loading",
    showBackground = true
)
@Composable
private fun GeneralLibraryScreenLoadingPreview() {
    SoftFocusMobileTheme {
        GeneralLibraryScreenContent(
            selectedType = ContentType.Movie,
            searchQuery = "",
            onSearchQueryChange = {},
            onTabSelected = {},
            selectedVideoCategory = null,
            uiState = GeneralLibraryUiState.Loading,
            favoriteIds = emptySet(),
            onContentClick = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(
    name = "General Library Screen - Empty",
    showBackground = true
)
@Composable
private fun GeneralLibraryScreenEmptyPreview() {
    SoftFocusMobileTheme {
        GeneralLibraryScreenContent(
            selectedType = ContentType.Movie,
            searchQuery = "",
            onSearchQueryChange = {},
            onTabSelected = {},
            selectedVideoCategory = null,
            uiState = GeneralLibraryUiState.Success(
                contentByType = mapOf(
                    ContentType.Movie to emptyList(),
                    ContentType.Music to emptyList(),
                    ContentType.Video to emptyList(),
                    ContentType.Weather to emptyList()
                ),
                selectedType = ContentType.Movie
            ),
            favoriteIds = emptySet(),
            onContentClick = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(
    name = "General Library Screen - Error",
    showBackground = true
)
@Composable
private fun GeneralLibraryScreenErrorPreview() {
    SoftFocusMobileTheme {
        GeneralLibraryScreenContent(
            selectedType = ContentType.Movie,
            searchQuery = "",
            onSearchQueryChange = {},
            onTabSelected = {},
            selectedVideoCategory = null,
            uiState = GeneralLibraryUiState.Error("No se pudo cargar el contenido. Verifica tu conexión a internet."),
            favoriteIds = emptySet(),
            onContentClick = {},
            onFavoriteClick = {}
        )
    }
}
