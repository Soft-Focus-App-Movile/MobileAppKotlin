package com.softfocus.features.library.presentation.general.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import com.softfocus.ui.theme.SourceSansSemiBold
import com.softfocus.ui.theme.YellowCB9D

/**
 * Fila horizontal de contenido relacionado
 *
 * @param relatedContent Lista de contenido relacionado
 * @param onContentClick Callback al hacer clic en un item
 * @param modifier Modificador opcional
 */
@Composable
fun RelatedContentRow(
    relatedContent: List<ContentItem>,
    onContentClick: (ContentItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (relatedContent.isNotEmpty()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            // Título de la sección
            Text(
                text = "Otros",
                style = SourceSansSemiBold.copy(fontSize = 15.sp),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Scroll horizontal de contenido
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(relatedContent) { item ->
                    RelatedContentCard(
                        content = item,
                        onClick = { onContentClick(item) }
                    )
                }
            }
        }
    }
}

/**
 * Card pequeño para contenido relacionado (similar a ContentCard)
 */
@Composable
private fun RelatedContentCard(
    content: ContentItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(129.dp)
            .clickable(onClick = onClick)
    ) {
        // Imagen
        Box(
            modifier = Modifier
                .width(129.dp)
                .height(134.dp)
        ) {
            AsyncImage(
                model = content.getMainImageUrl(),
                contentDescription = content.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Información debajo de la imagen
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Título
            Text(
                text = content.title,
                style = CrimsonBold.copy(fontSize = 13.sp),
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
                    style = SourceSansRegular.copy(fontSize = 11.sp),
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
