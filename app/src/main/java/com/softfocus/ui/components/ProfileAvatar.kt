package com.softfocus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansSemiBold

/**
 * Componente reutilizable para mostrar avatares de perfil.
 * Si hay imagen, la muestra. Si no, muestra un círculo con la inicial del nombre.
 *
 * @param imageUrl URL de la imagen de perfil (puede ser null)
 * @param fullName Nombre completo del usuario para obtener la inicial
 * @param size Tamaño del avatar (default 48.dp)
 * @param fontSize Tamaño de la fuente de la inicial (default 18.sp)
 * @param backgroundColor Color de fondo (default White)
 * @param textColor Color del texto de la inicial (default Green49)
 * @param shape Forma del avatar (default CircleShape, puede ser RoundedCornerShape, etc.)
 */
@Composable
fun ProfileAvatar(
    imageUrl: String?,
    fullName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    fontSize: TextUnit = 18.sp,
    backgroundColor: Color = Color.White,
    textColor: Color = Green49,
    shape: Shape = CircleShape
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            // Si hay imagen, mostrarla
            AsyncImage(
                model = imageUrl,
                contentDescription = "Foto de perfil de $fullName",
                modifier = Modifier
                    .size(size)
                    .clip(shape),
                contentScale = ContentScale.Crop
            )
        } else {
            // Si no hay imagen, mostrar inicial
            Text(
                text = fullName.firstOrNull()?.toString()?.uppercase() ?: "?",
                style = SourceSansSemiBold,
                fontSize = fontSize,
                color = textColor
            )
        }
    }
}
