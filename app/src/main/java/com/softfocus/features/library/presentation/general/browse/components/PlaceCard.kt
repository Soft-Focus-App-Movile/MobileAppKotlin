package com.softfocus.features.library.presentation.general.browse.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.SourceSansSemiBold

/**
 * Card de lugar para mostrar en la lista de recomendaciones
 *
 * @param place Item de lugar a mostrar
 * @param onClick Callback al hacer clic en el card
 * @param modifier Modificador opcional
 */
@Composable
fun PlaceCard(
    place: ContentItem,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icono del lugar
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2D3748)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Place,
                contentDescription = place.title,
                tint = Color(0xFF718096),
                modifier = Modifier.size(40.dp)
            )
        }

        // Información del lugar
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Título
            Text(
                text = place.title,
                style = SourceSansSemiBold.copy(fontSize = 15.sp),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            // Categoría y dirección
            val description = buildString {
                place.category?.let { append(it) }
                place.address?.let {
                    if (isNotEmpty()) append(" • ")
                    append(it)
                }
            }

            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = SourceSansRegular.copy(fontSize = 11.sp),
                    color = Gray828,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Botón "Cómo llegar?"
            Button(
                onClick = {
                    openInGoogleMaps(
                        context = context,
                        latitude = place.latitude,
                        longitude = place.longitude,
                        placeName = place.title
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green49
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    text = "Cómo llegar?",
                    style = SourceSansSemiBold.copy(fontSize = 11.sp),
                    color = Color.Black
                )
            }
        }
    }
}

/**
 * Abre Google Maps con la ubicación del lugar
 * Si no hay coordenadas, intenta buscar por nombre
 */
private fun openInGoogleMaps(
    context: Context,
    latitude: Double?,
    longitude: Double?,
    placeName: String
) {
    val uri = if (latitude != null && longitude != null) {
        // Abrir con coordenadas
        Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(placeName)})")
    } else {
        // Buscar por nombre si no hay coordenadas
        Uri.parse("geo:0,0?q=${Uri.encode(placeName)}")
    }

    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Si Google Maps no está instalado, intentar con cualquier app de mapas
        val fallbackIntent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(fallbackIntent)
    }
}
