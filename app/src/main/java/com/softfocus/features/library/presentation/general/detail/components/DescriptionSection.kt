package com.softfocus.features.library.presentation.general.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.ui.theme.SourceSansLight
import com.softfocus.ui.theme.SourceSansSemiBold

/**
 * Sección de descripción del contenido
 *
 * @param description Texto de la descripción
 * @param modifier Modificador opcional
 */
@Composable
fun DescriptionSection(
    description: String?,
    modifier: Modifier = Modifier
) {
    if (description != null) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Título de la sección
            Text(
                text = "Descripción",
                style = SourceSansSemiBold.copy(fontSize = 15.sp),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(
                text = description,
                style = SourceSansLight.copy(fontSize = 14.sp),
                color = Color.White,
                lineHeight = 22.sp
            )
        }
    }
}
