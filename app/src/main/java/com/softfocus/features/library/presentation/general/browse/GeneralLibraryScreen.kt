package com.softfocus.features.library.presentation.general.browse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.presentation.di.libraryViewModel
import com.softfocus.features.library.presentation.general.browse.components.ContentCard
import com.softfocus.features.library.presentation.general.browse.components.FilterBottomSheet
import com.softfocus.features.library.presentation.general.browse.components.SearchBarWithFilter
import com.softfocus.features.library.presentation.shared.getDisplayName
import com.softfocus.features.library.presentation.shared.getEmoji
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralLibraryScreen(
    onContentClick: (ContentItem) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: GeneralLibraryViewModel = libraryViewModel { GeneralLibraryViewModel(it) }
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedEmotion by viewModel.selectedEmotion.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery) {
        kotlinx.coroutines.delay(500)
        viewModel.searchContent(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Biblioteca",
                        style = CrimsonBold.copy(fontSize = 24.sp),
                        color = Green29
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = YellowE8.copy(alpha = 0.1f)
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de búsqueda con filtro
            SearchBarWithFilter(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onFilterClick = { showFilterSheet = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tabs de tipos de contenido
            ScrollableTabRow(
                selectedTabIndex = ContentType.values().indexOf(selectedType),
                containerColor = Color.White,
                contentColor = Green29,
                edgePadding = 16.dp
            ) {
                ContentType.values().forEach { type ->
                    Tab(
                        selected = selectedType == type,
                        onClick = { viewModel.selectContentType(type) },
                        text = {
                            Text(
                                text = "${type.getEmoji()} ${type.getDisplayName()}",
                                style = SourceSansSemiBold.copy(fontSize = 14.sp)
                            )
                        }
                    )
                }
            }

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
                    val content = (uiState as GeneralLibraryUiState.Success).getSelectedContent()

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
                                    onFavoriteClick = { viewModel.toggleFavorite(item) },
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
                                text = (uiState as GeneralLibraryUiState.Error).message,
                                style = SourceSansRegular.copy(fontSize = 14.sp),
                                color = Gray828,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.retry() },
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
            onEmotionSelected = { emotion ->
                if (emotion != null) {
                    viewModel.loadContentByEmotion(emotion)
                } else {
                    viewModel.clearEmotionFilter()
                }
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}
