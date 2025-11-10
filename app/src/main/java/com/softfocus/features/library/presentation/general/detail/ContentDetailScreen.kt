package com.softfocus.features.library.presentation.general.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.presentation.di.libraryViewModel
import com.softfocus.features.library.presentation.general.detail.components.*
import com.softfocus.ui.theme.*

/**
 * Pantalla de detalle de contenido
 *
 * Muestra información completa de una película, música, video o lugar:
 * - Imagen hero
 * - Título, año, rating, duración
 * - Botón de trailer
 * - Descripción
 * - Contenido relacionado
 *
 * @param contentId ID del contenido a mostrar
 * @param onNavigateBack Callback para volver
 * @param onRelatedContentClick Callback al hacer clic en contenido relacionado
 * @param modifier Modificador opcional
 */
@Composable
fun ContentDetailScreen(
    contentId: String,
    onNavigateBack: () -> Unit = {},
    onRelatedContentClick: (ContentItem) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ContentDetailViewModel = libraryViewModel {
        ContentDetailViewModel(contentId, it)
    }
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    when (uiState) {
        is ContentDetailUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Green29)
            }
        }

        is ContentDetailUiState.Success -> {
            val state = uiState as ContentDetailUiState.Success
            val content = state.content
            var showTrailer by remember { mutableStateOf(false) }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .verticalScroll(scrollState)
            ) {
                // Imagen hero con botón de volver (o reproductor de trailer)
                ContentHeroImage(
                    imageUrl = content.backdropUrl ?: content.posterUrl,
                    contentDescription = content.title,
                    onBackClick = onNavigateBack,
                    showVideoPlayer = showTrailer,
                    trailerUrl = content.trailerUrl
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Header con información principal
                ContentInfoHeader(
                    title = content.title,
                    year = content.getReleaseYear(),
                    rating = content.rating,
                    duration = content.getFormattedDuration()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de trailer (solo si hay trailerUrl válida)
                if (!content.trailerUrl.isNullOrBlank()) {
                    TrailerButton(
                        onClick = {
                            showTrailer = true
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Sección de descripción
                DescriptionSection(
                    description = content.overview
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Contenido relacionado
                RelatedContentRow(
                    relatedContent = state.relatedContent,
                    onContentClick = onRelatedContentClick
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        is ContentDetailUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
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
                        text = (uiState as ContentDetailUiState.Error).message,
                        style = SourceSansRegular.copy(fontSize = 14.sp),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            )
                        ) {
                            Text(
                                text = "Volver",
                                style = SourceSansSemiBold
                            )
                        }
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
