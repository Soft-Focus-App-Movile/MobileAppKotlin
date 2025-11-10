package com.softfocus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.softfocus.R
import com.softfocus.ui.theme.Gray828
import kotlin.math.roundToInt

@Composable
fun DraggableAIButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }


    val buttonSizePx = with(density) { 64.dp.toPx() }


    val initialX = screenWidthPx - buttonSizePx - with(density) { 16.dp.toPx() }
    val initialY = screenHeightPx - buttonSizePx - with(density) { 150.dp.toPx() }

    var offsetX by remember { mutableStateOf(initialX) }
    var offsetY by remember { mutableStateOf(initialY) }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newOffsetX = offsetX + dragAmount.x
                    val newOffsetY = offsetY + dragAmount.y

                    // Limitar el movimiento dentro de los bordes de la pantalla
                    offsetX = newOffsetX.coerceIn(0f, screenWidthPx - buttonSizePx)
                    offsetY = newOffsetY.coerceIn(0f, screenHeightPx - buttonSizePx)
                }
            }
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(64.dp)
                .background(Gray828, CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ia_button),
                contentDescription = "Asistente IA",
                tint = Color.Unspecified,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DraggableAIButtonPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        DraggableAIButton(onClick = {})
    }
}
