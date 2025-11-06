package com.softfocus.features.library.presentation.general.browse.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
 * @param onFavoriteClick Callback al hacer clic en el botón de favorito
 * @param onClick Callback al hacer clic en el card
 * @param modifier Modificador opcional
 */
@Composable
fun ContentCard(
    content: ContentItem,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(129.dp)
            .clickable(onClick = onClick)
    ) {
        // 1. Imagen con botón de favorito
        Box(
            modifier = Modifier
                .width(129.dp)
                .height(134.dp)
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

            // Botón de favorito en esquina superior derecha
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

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Información debajo de la imagen
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Título
            Text(
                text = content.title,
                style = SourceSansRegular.copy(fontSize = 16.sp),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Duración
            content.getFormattedDuration()?.let { duration ->
                Text(
                    text = "Duración  $duration",
                    style = SourceSansLight.copy(fontSize = 14.sp),
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tags emocionales
            if (content.emotionalTags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(content.emotionalTags.take(2)) { tag ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = tag.getDisplayName(),
                                    style = SourceSansRegular.copy(fontSize = 11.sp),
                                    color = Color.Black
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
