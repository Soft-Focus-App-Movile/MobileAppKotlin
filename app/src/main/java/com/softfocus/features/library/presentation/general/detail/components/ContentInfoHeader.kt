package com.softfocus.features.library.presentation.general.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.ui.theme.SourceSansSemiBold

/**
 * Header con información del contenido (título, año, rating, duración)
 *
 * @param title Título del contenido
 * @param year Año de lanzamiento
 * @param rating Rating (0-10)
 * @param duration Duración formateada
 * @param modifier Modificador opcional
 */
@Composable
fun ContentInfoHeader(
    title: String,
    year: String? = null,
    rating: Double? = null,
    duration: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = title,
            style = SourceSansSemiBold.copy(fontSize = 24.sp),
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Info: Año, Rating, Duración
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Año
            year?.let {
                Text(
                    text = it,
                    style = SourceSansSemiBold.copy(fontSize = 14.sp),
                    color = Color.White
                )
            }

            // Rating
            rating?.let {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700), // Gold color
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = String.format("%.1f/10", it),
                        style = SourceSansSemiBold.copy(fontSize = 14.sp),
                        color = Color.White
                    )
                }
            }

            // Duración
            duration?.let {
                Text(
                    text = it,
                    style = SourceSansSemiBold.copy(fontSize = 14.sp),
                    color = Color.White
                )
            }
        }
    }
}
