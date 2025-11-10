package com.softfocus.features.home.presentation.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.softfocus.R
import com.softfocus.features.home.presentation.general.RecommendationsState
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray787
import com.softfocus.ui.theme.Green29
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.SourceSansSemiBold
import com.softfocus.ui.theme.YellowCB9D
import kotlinx.coroutines.delay

/**
 * Componente completo de recomendaciones con título, estados y carrusel
 */
@Composable
fun RecommendationsSection(
    recommendationsState: RecommendationsState,
    onNavigateToLibrary: () -> Unit,
    onContentClick: (String) -> Unit,
    onRetry: () -> Unit
) {
    Column {
        // Título y botón "ver todas"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recomendaciones",
                style = CrimsonSemiBold,
                fontSize = 20.sp,
                color = Green65
            )
            TextButton(onClick = onNavigateToLibrary) {
                Text(
                    text = "ver todas",
                    style = SourceSansSemiBold,
                    fontSize = 14.sp,
                    color = Gray787
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Contenido de recomendaciones según el estado
        when (recommendationsState) {
            is RecommendationsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Green49)
                }
            }
            is RecommendationsState.Success -> {
                RecommendationsCarousel(
                    recommendations = recommendationsState.recommendations,
                    onContentClick = onContentClick
                )
            }
            is RecommendationsState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay recomendaciones disponibles",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray787
                    )
                }
            }
            is RecommendationsState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error al cargar recomendaciones",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray787
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onRetry) {
                        Text(
                            text = "Reintentar",
                            style = SourceSansSemiBold,
                            fontSize = 14.sp,
                            color = Green49
                        )
                    }
                }
            }
        }
    }
}

/**
 * Carrusel de recomendaciones con auto-scroll continuo y suave
 */
@Composable
private fun RecommendationsCarousel(
    recommendations: List<ContentItem>,
    onContentClick: (String) -> Unit
) {
    val context = LocalContext.current

    // Estado para controlar el scroll
    val lazyListState = rememberLazyListState()

    // Detectar si el usuario está haciendo scroll manual
    val isUserScrolling = lazyListState.isScrollInProgress
    var lastUserInteraction by remember { mutableStateOf(0L) }

    // Detectar interacción del usuario
    LaunchedEffect(isUserScrolling) {
        if (isUserScrolling) {
            lastUserInteraction = System.currentTimeMillis()
        }
    }

    // Auto-scroll continuo muy lento y suave
    LaunchedEffect(recommendations) {
        if (recommendations.isNotEmpty()) {
            var totalScrollOffset = 0

            while (true) {
                delay(30L) // Delay para suavidad (30ms entre cada movimiento)

                try {
                    // Pausar auto-scroll si el usuario interactuó recientemente (últimos 3 segundos)
                    val timeSinceInteraction = System.currentTimeMillis() - lastUserInteraction
                    if (timeSinceInteraction > 3000 && !isUserScrolling) {
                        totalScrollOffset += 1

                        // Calcular índice y offset basados en el scroll total
                        val itemWidth = 152 // 140 (ancho card) + 12 (spacing)
                        val currentIndex = (totalScrollOffset / itemWidth) % recommendations.size
                        val currentOffset = totalScrollOffset % itemWidth

                        // Scroll suave sin saltos
                        lazyListState.scrollToItem(currentIndex, currentOffset)
                    }
                } catch (e: Exception) {
                    // Ignorar errores de scroll
                }
            }
        }
    }

    LazyRow(
        state = lazyListState,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(recommendations) { content ->
            RecommendationCard(
                content = content,
                onClick = { onContentClick(content.externalId) },
                context = context
            )
        }
    }
}

/**
 * Card de recomendación con imagen real y botón Ver
 */
@Composable
private fun RecommendationCard(
    content: ContentItem,
    onClick: () -> Unit,
    context: Context
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Imagen del contenido
                val imageUrl = content.posterUrl ?: content.backdropUrl ?: content.thumbnailUrl ?: content.photoUrl

                if (imageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = content.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Título del contenido
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = content.title,
                        style = SourceSansRegular,
                        fontSize = if (content.title.length > 30) 10.sp else 12.sp,
                        color = Green29,
                        maxLines = 2,
                        lineHeight = if (content.title.length > 30) 12.sp else 14.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Botón Ver
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YellowCB9D
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Ver",
                            style = SourceSansRegular,
                            fontSize = 11.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
