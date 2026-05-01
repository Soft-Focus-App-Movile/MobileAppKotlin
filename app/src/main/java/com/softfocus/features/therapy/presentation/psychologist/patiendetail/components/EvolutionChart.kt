package com.softfocus.features.therapy.presentation.psychologist.patiendetail.components

import android.graphics.Typeface
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.layer.continuous
import com.patrykandpatrick.vico.compose.cartesian.layer.point
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.softfocus.ui.theme.Gray89
import com.softfocus.ui.theme.GreenAB
import com.softfocus.ui.theme.SourceSansSemiBold

@Composable
fun EvolutionChart(
    lineData: List<Float>,
    columnData: List<Float>,
    isLoading: Boolean
) {
    // --- 1. ESTADO DE CARGA ---
    if (isLoading) {
        Box(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // --- 2. ESTADO VACÍO (PERO CON EJE) ---
    // Comprueba si hay datos reales (no solo ceros)
    val hasData = lineData.any { it > 0f }

    if (!hasData) {
        Box(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay datos de evolución para esta semana.",
                color = Gray89,
                style = SourceSansSemiBold.copy(fontSize = 10.sp)
            )
        }
        return
    }

    // --- 1. MODELO DE DATOS ---
    val model = remember(lineData, columnData) {
        CartesianChartModel(
            ColumnCartesianLayerModel.build { series(columnData) },
            LineCartesianLayerModel.build { series(lineData) }
        )
    }

    // --- 2. CONFIGURACIÓN DE CAPAS ---

    // Capa de Columnas
    val columnLayer = rememberColumnCartesianLayer(
        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
            rememberLineComponent(
                fill = fill(GreenAB.copy(alpha = 0.5f)),
                thickness = 6.dp,
                shape = CorneredShape.rounded(topLeftPercent = 20, topRightPercent = 20)
            )
        )
    )

    // Capa de Líneas
    val lineLayer = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(
            LineCartesianLayer.rememberLine(
                // fill: Le damos el color a la línea
                // stroke: definimos el grosor de la línea
                fill = LineCartesianLayer.LineFill.single(
                    fill(GreenAB)
                ),
                stroke = LineCartesianLayer.LineStroke.continuous(
                    thickness = 3.dp
                ),
                // DEGRADADO BAJO LA LÍNEA (areaFill)
                // Usamos verticalGradient con el color lightGreen.
                // Va desde una opacidad media (0.4f o 0.5f) arriba hasta transparente (0.0f) abajo.
                areaFill = LineCartesianLayer.AreaFill.single(
                    fill(
                        ShaderProvider.verticalGradient(
                            colors = arrayOf(
                                GreenAB.copy(alpha = 0.5f), // Ajusta este valor (ej. 0.4f a 0.6f) para la intensidad superior
                                GreenAB.copy(alpha = 0.0f)  // Transparente abajo
                            ).map { it.toArgb() }.toIntArray()
                        ))
                ),
                pointProvider = LineCartesianLayer.PointProvider.single(
                    LineCartesianLayer.point(
                        component = rememberShapeComponent(
                            shape = CorneredShape.rounded(allPercent = 50),
                            fill = fill(GreenAB),
                            strokeThickness = 0.dp
                        ),
                        size = 8.dp
                    )
                ),
                pointConnector = LineCartesianLayer.PointConnector.cubic(curvature = 0.3f)
            )
        )
    )

    // --- 3. EJES ---
    val days = listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do")

    val bottomAxis = HorizontalAxis.rememberBottom(
        valueFormatter = CartesianValueFormatter { _, value, _ ->
            days.getOrNull(value.toInt()) ?: ""
        },
        label = rememberTextComponent(
            color = Gray89,
            padding = Insets(horizontalDp = 1f, verticalDp = 0f),
            margins = Insets(horizontalDp = 0f, verticalDp = 5f)
        ),
        tick = null,
        guideline = null
    )

    val marker = rememberMarker()

    CartesianChartHost(
        chart = rememberCartesianChart(
            columnLayer,
            lineLayer,
            startAxis = null,
            bottomAxis = bottomAxis,
            marker = marker
        ),
        model = model,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    )
}

// --- MARKER (TOOLTIP) ---
@Composable
fun rememberMarker(): CartesianMarker {
    val labelBackground = rememberShapeComponent(
        shape = CorneredShape.rounded(allPercent = 50),
        fill = fill(GreenAB)
    )

    val label = rememberTextComponent(
        background = labelBackground,
        lineCount = 1,
        padding = Insets(horizontalDp = 8f, verticalDp = 4f),
        typeface = Typeface.DEFAULT_BOLD,
        color = Color.White,
        margins = Insets(horizontalDp = 0f, verticalDp = 6f)
    )

    val indicator = rememberShapeComponent(
        shape = CorneredShape.rounded(allPercent = 50),
        fill = fill(GreenAB),
        strokeThickness = 2.dp,
        strokeFill = fill(Color.White)
    )

    return rememberDefaultCartesianMarker(
        label = label,
        indicator = { indicator },
        indicatorSize = 12.dp,
        guideline = null,
        valueFormatter = { _, targets ->
            // Buscamos primero si hay un target de LÍNEA (prioridad)
            val lineTarget = targets.filterIsInstance<LineCartesianLayerMarkerTarget>().firstOrNull()
            // Buscamos si hay un target de COLUMNA
            val columnTarget = targets.filterIsInstance<ColumnCartesianLayerMarkerTarget>().firstOrNull()

            // Extraemos el valor Y accediendo a 'entry' primero
            val value = lineTarget?.points?.firstOrNull()?.entry?.y
                ?: columnTarget?.columns?.firstOrNull()?.entry?.y

            // Formateamos: Si hay valor lo convertimos a Int, si no, devolvemos vacío
            value?.toInt()?.toString() ?: ""
        }
    )
}