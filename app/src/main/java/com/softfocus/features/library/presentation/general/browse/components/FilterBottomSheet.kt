package com.softfocus.features.library.presentation.general.browse.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.library.domain.models.EmotionalTag
import com.softfocus.features.library.presentation.shared.EmotionChip
import com.softfocus.ui.theme.CrimsonBold
import com.softfocus.ui.theme.Green29
import com.softfocus.ui.theme.SourceSansSemiBold

/**
 * Bottom sheet para filtrar por emoción
 *
 * @param selectedEmotion Emoción actualmente seleccionada
 * @param onEmotionSelected Callback cuando se selecciona una emoción
 * @param onDismiss Callback para cerrar el bottom sheet
 * @param modifier Modificador opcional
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    selectedEmotion: EmotionalTag?,
    onEmotionSelected: (EmotionalTag?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Título
            Text(
                text = "Filtrar por emoción",
                style = CrimsonBold.copy(fontSize = 20.sp),
                color = Green29,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Chips de emociones
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(EmotionalTag.values()) { emotion ->
                    EmotionChip(
                        emotion = emotion,
                        selected = selectedEmotion == emotion,
                        onClick = {
                            onEmotionSelected(
                                if (selectedEmotion == emotion) null else emotion
                            )
                        }
                    )
                }
            }

            // Botón para limpiar filtros
            if (selectedEmotion != null) {
                TextButton(
                    onClick = {
                        onEmotionSelected(null)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Limpiar filtro",
                        style = SourceSansSemiBold,
                        color = Green29
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
