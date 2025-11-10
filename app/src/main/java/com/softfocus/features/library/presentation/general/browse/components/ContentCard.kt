package com.softfocus.features.library.presentation.general.browse.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.presentation.shared.getDisplayName
import com.softfocus.ui.theme.CrimsonBold
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.YellowCB9D
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansLight

/**
 * Card de contenido para mostrar en la grilla
 *
 * @param content Item de contenido a mostrar
 * @param isFavorite Si el contenido está marcado como favorito
 * @param isSelected Si el contenido está seleccionado (modo psicólogo)
 * @param isSelectionMode Si está en modo selección (mostrar overlay)
 * @param onFavoriteClick Callback al hacer clic en el botón de favorito
 * @param onClick Callback al hacer clic en el card
 * @param onLongClick Callback al mantener presionado el card (para activar modo selección)
 * @param modifier Modificador opcional
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentCard(
    content: ContentItem,
    isFavorite: Boolean = false,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(160.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        // 1. Imagen con botón de favorito o indicador de selección
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(180.dp)
        ) {
            // Imagen del contenido
            AsyncImage(
                model = content.getMainImageUrl(),
                contentDescription = content.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            )

            // Overlay de selección cuando está en modo selección
            if (isSelectionMode && isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Green49.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Seleccionado",
                        tint = Green49,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Botón de favorito en esquina superior derecha (solo si NO está en modo selección)
            if (!isSelectionMode) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (isFavorite) Green49 else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Información debajo de la imagen
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Título
            Text(
                text = content.title,
                style = SourceSansRegular.copy(fontSize = 14.sp),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Duración
            content.getFormattedDuration()?.let { duration ->
                Text(
                    text = "Duración  $duration",
                    style = SourceSansLight.copy(fontSize = 12.sp),
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Tags emocionales (solo 1 tag)
            if (content.emotionalTags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(content.emotionalTags.take(1)) { tag ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = tag.getDisplayName(),
                                    style = SourceSansRegular.copy(fontSize = 11.sp),
                                    color = Color.White
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = YellowCB9D,
                                labelColor = Color.Black
                            ),
                            border = null,
                            modifier = Modifier.height(26.dp)
                        )
                    }
                }
            }
        }
    }
}
