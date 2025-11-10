package com.softfocus.features.library.presentation.general.browse.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansSemiBold

/**
 * Botón flotante (FAB) para asignar tareas a pacientes
 * Solo visible para psicólogos cuando hay contenido seleccionado
 *
 * @param selectedCount Cantidad de contenido seleccionado
 * @param onClick Callback al hacer clic en el botón
 * @param modifier Modificador opcional
 */
@Composable
fun AssignTaskButton(
    selectedCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Mostrar solo si hay selección
    AnimatedVisibility(
        visible = selectedCount > 0,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = Green49,
            contentColor = Color.Black,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .height(56.dp)
                .widthIn(min = 180.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Asignar",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Asignar Tarea ($selectedCount)",
                    style = SourceSansSemiBold.copy(fontSize = 16.sp),
                    color = Color.Black
                )
            }
        }
    }
}
