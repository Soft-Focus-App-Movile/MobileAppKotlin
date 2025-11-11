package com.softfocus.features.library.presentation.general.browse.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.domain.models.EmotionalTag
import com.softfocus.features.library.presentation.general.browse.GeneralLibraryUiState
import com.softfocus.ui.theme.*

/**
 * Componente que muestra el contenido de la biblioteca
 * Maneja los diferentes estados: Loading, Success, Error
 * Y renderiza el contenido según el tipo seleccionado
 *
 * @param uiState Estado actual de la biblioteca
 * @param selectedType Tipo de contenido seleccionado
 * @param searchQuery Query de búsqueda actual
 * @param favoriteIds IDs de contenidos favoritos
 * @param selectedContentIds IDs de contenidos seleccionados (modo psicólogo)
 * @param isSelectionMode Si está en modo selección
 * @param isPsychologist Si el usuario es psicólogo (puede seleccionar contenido)
 * @param onContentClick Callback al hacer click en contenido
 * @param onContentLongClick Callback al mantener presionado (solo psicólogos)
 * @param onFavoriteClick Callback al marcar favorito
 * @param onRetry Callback para reintentar carga
 */
@Composable
fun LibraryContent(
    uiState: GeneralLibraryUiState,
    selectedType: ContentType,
    searchQuery: String,
    favoriteIds: Set<String>,
    selectedContentIds: Set<String>,
    isSelectionMode: Boolean,
    isPsychologist: Boolean,
    onContentClick: (ContentItem) -> Unit,
    onContentLongClick: (ContentItem) -> Unit,
    onFavoriteClick: (ContentItem) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    selectedEmotion: EmotionalTag? = null
) {
    when (uiState) {
        is GeneralLibraryUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
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
                    modifier = modifier.fillMaxSize(),
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
                            else if (selectedEmotion != null)
                                "Prueba otro filtro"
                            else if (selectedType == ContentType.Video)
                                "Selecciona una categoría para ver videos"
                            else
                                "Intenta con otro tipo de contenido o filtro",
                            style = SourceSansRegular.copy(fontSize = 14.sp),
                            color = Gray828,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Renderizar contenido según tipo
                when (selectedType) {
                    ContentType.Video -> {
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = modifier.fillMaxSize()
                        ) {
                            items(content) { item ->
                                VideoCard(
                                    content = item,
                                    isFavorite = favoriteIds.contains(item.externalId),
                                    isSelected = selectedContentIds.contains(item.id),
                                    isSelectionMode = isSelectionMode,
                                    onFavoriteClick = { onFavoriteClick(item) },
                                    onViewClick = { onContentClick(item) },
                                    onClick = { onContentClick(item) },
                                    onLongClick = if (isPsychologist) {
                                        { onContentLongClick(item) }
                                    } else {
                                        {} // No hace nada para pacientes
                                    }
                                )
                            }
                        }
                    }
                    ContentType.Weather -> {
                        // Vista simple de clima y ubicación
                        Box(
                            modifier = modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "☀️ Clima",
                                    style = SourceSansSemiBold.copy(fontSize = 32.sp),
                                    color = Green29
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Información del clima en tu ubicación actual",
                                    style = SourceSansRegular.copy(fontSize = 16.sp),
                                    color = Gray828,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    else -> {
                        // Grid para Movies y Music
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = modifier.fillMaxSize()
                        ) {
                            items(content) { item ->
                                ContentCard(
                                    content = item,
                                    isFavorite = favoriteIds.contains(item.externalId),
                                    isSelected = selectedContentIds.contains(item.id),
                                    isSelectionMode = isSelectionMode,
                                    onFavoriteClick = { onFavoriteClick(item) },
                                    onClick = { onContentClick(item) },
                                    onLongClick = if (isPsychologist) {
                                        { onContentLongClick(item) }
                                    } else {
                                        {} // No hace nada para pacientes
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        is GeneralLibraryUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth()
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
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
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
