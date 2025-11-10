package com.softfocus.features.library.presentation.general.browse.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49

/**
 * TopBar de la biblioteca con título y botón de cancelar selección
 *
 * @param isPsychologist Si el usuario es psicólogo
 * @param isSelectionMode Si está en modo selección
 * @param onCancelSelection Callback para cancelar la selección
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryTopBar(
    isPsychologist: Boolean,
    isSelectionMode: Boolean,
    onCancelSelection: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Biblioteca",
                style = CrimsonSemiBold.copy(fontSize = 32.sp),
                color = Green49
            )
        },
        actions = {
            if (isPsychologist && isSelectionMode) {
                androidx.compose.material3.TextButton(
                    onClick = onCancelSelection
                ) {
                    Text("Cancelar", color = Green49)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
