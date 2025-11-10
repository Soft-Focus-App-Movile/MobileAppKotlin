package com.softfocus.features.library.presentation.general.browse.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.ui.theme.SourceSansSemiBold
import com.softfocus.ui.theme.SourceSansLight

/**
 * Vista del tab "Asignado por mi terapeuta" para pacientes
 * Muestra contenido asignado mezclado (películas, música, videos)
 *
 * @param assignedContent Lista de contenido asignado por el terapeuta
 * @param favoriteIds Set de IDs de contenido marcado como favorito
 * @param onFavoriteClick Callback al hacer clic en favorito
 * @param onContentClick Callback al hacer clic en un contenido
 * @param onViewVideoClick Callback al hacer clic en "Ver" en un video
 * @param modifier Modificador opcional
 */
@Composable
fun AssignedContentView(
    assignedContent: List<ContentItem>,
    favoriteIds: Set<String>,
    onFavoriteClick: (ContentItem) -> Unit,
    onContentClick: (ContentItem) -> Unit,
    onViewVideoClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (assignedContent.isEmpty()) {
        // Estado vacío
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Sin contenido asignado",
                    style = SourceSansSemiBold.copy(fontSize = 18.sp),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tu terapeuta aún no te ha asignado contenido",
                    style = SourceSansLight.copy(fontSize = 14.sp),
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Asignado por mi terapeuta",
                        style = SourceSansSemiBold.copy(fontSize = 20.sp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${assignedContent.size} contenidos asignados",
                        style = SourceSansLight.copy(fontSize = 14.sp),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Lista de contenido asignado
            items(assignedContent) { content ->
                when (content.type) {
                    ContentType.Video -> {
                        VideoCard(
                            content = content,
                            isFavorite = favoriteIds.contains(content.externalId),
                            isSelected = false,
                            isSelectionMode = false,
                            onFavoriteClick = { onFavoriteClick(content) },
                            onViewClick = { onViewVideoClick(content) },
                            onClick = { onContentClick(content) }
                        )
                    }
                    ContentType.Movie, ContentType.Music -> {
                        // Para películas y música usamos ContentCard en un Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            ContentCard(
                                content = content,
                                isFavorite = favoriteIds.contains(content.externalId),
                                isSelected = false,
                                isSelectionMode = false,
                                onFavoriteClick = { onFavoriteClick(content) },
                                onClick = { onContentClick(content) }
                            )
                        }
                    }
                    else -> { /* Ignorar otros tipos */ }
                }
            }

            // Espacio final
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
