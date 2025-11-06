package com.softfocus.features.library.presentation.general.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.domain.models.EmotionalTag
import com.softfocus.features.library.presentation.di.libraryViewModel
import com.softfocus.features.library.presentation.general.browse.components.ContentCard
import com.softfocus.features.library.presentation.general.browse.components.FilterBottomSheet
import com.softfocus.features.library.presentation.general.browse.components.SearchBarWithFilter
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
    viewModel: GeneralLibraryViewModel = libraryViewModel { GeneralLibraryViewModel(it) },
    onContentClick: (ContentItem) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedEmotion by viewModel.selectedEmotion.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        kotlinx.coroutines.delay(500)
        viewModel.searchContent(searchQuery)
    }

    GeneralLibraryScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedType = selectedType,
        selectedEmotion = selectedEmotion,
        favoriteIds = favoriteIds,
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        onTabSelected = { viewModel.selectContentType(it) },
        onFilterClear = { viewModel.clearEmotionFilter() },
        onEmotionSelected = { viewModel.loadContentByEmotion(it) },
        onRetry = { viewModel.retry() },
        onFavoriteClick = { viewModel.toggleFavorite(it) },
        onContentClick = onContentClick
    )
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
    onFilterClear: () -> Unit = {},
    onEmotionSelected: (EmotionalTag) -> Unit = {},
    onRetry: () -> Unit = {}
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Biblioteca",
                        style = CrimsonSemiBold.copy(fontSize = 32.sp),
                        color = Green49
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        modifier = modifier,
        containerColor = Color.Black // Fondo de pantalla negro
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs de tipos de contenido
            val selectedTabIndex = ContentType.entries.indexOf(selectedType)
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent, // Fondo transparente para los tabs
                contentColor = Green65,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Green65 // Color del indicador
                    )
                },
                divider = { } // Sin línea divisora
            ) {
                ContentType.entries.forEach { type ->
                    val isSelected = selectedType == type
                    Tab(
                        selected = isSelected,
                        onClick = { onTabSelected(type) },
                        text = {
                            Text(
                                text = type.getDisplayName(),
                                style = SourceSansRegular.copy(fontSize = 15.sp),
                                color = if (isSelected) Green65 else Color.White
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de búsqueda con filtro
            SearchBarWithFilter(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onFilterClick = { showFilterSheet = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido según estado
            when (uiState) {
                is GeneralLibraryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Green29)
                    }
                }

                is GeneralLibraryUiState.Success -> {
                    val content = uiState.getSelectedContent()

                    if (content.isEmpty()) {
                        // Mensaje cuando no hay contenido
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text(
                                    text = "No se encontró contenido",
                                    style = SourceSansSemiBold.copy(fontSize = 18.sp),
                                    color = Gray828,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank())
                                        "Intenta con otra búsqueda"
                                    else
                                        "Intenta con otro tipo de contenido o filtro",
                                    style = SourceSansRegular.copy(fontSize = 14.sp),
                                    color = Gray828,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        // Grilla de contenido
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(content) { item ->
                                ContentCard(
                                    content = item,
                                    isFavorite = favoriteIds.contains(item.externalId),
                                    onFavoriteClick = { onFavoriteClick(item) },
                                    onClick = { onContentClick(item) }
                                )
                            }
                        }
                    }
                }

                is GeneralLibraryUiState.Error -> {
                    // Estado de error
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "Error",
                                style = CrimsonBold.copy(fontSize = 20.sp),
                                color = Green29
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.message,
                                style = SourceSansRegular.copy(fontSize = 14.sp),
                                color = Gray828,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onRetry() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Green29
                                )
                            ) {
                                Text(
                                    text = "Reintentar",
                                    style = SourceSansSemiBold
                                )
                            }
                        }
                    }
                }
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
                    ContentType.Place to emptyList()
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
            uiState = GeneralLibraryUiState.Success(
                contentByType = mapOf(
                    ContentType.Movie to emptyList(),
                    ContentType.Music to emptyList(),
                    ContentType.Video to emptyList(),
                    ContentType.Place to emptyList()
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
            uiState = GeneralLibraryUiState.Error("No se pudo cargar el contenido. Verifica tu conexión a internet."),
            favoriteIds = emptySet(),
            onContentClick = {},
            onFavoriteClick = {}
        )
    }
}
