package com.softfocus.features.therapy.presentation.psychologist.patiendetail.components

import android.graphics.Path
import android.graphics.RectF
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatryk.vico.compose.axis.axisLabelComponent
import com.patrykandpatryk.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.column.columnChart
import com.patrykandpatryk.vico.compose.chart.line.lineChart
import com.patrykandpatryk.vico.compose.chart.line.lineSpec
import com.patrykandpatryk.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatryk.vico.core.axis.AxisPosition
import com.patrykandpatryk.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatryk.vico.core.chart.composed.plus
import com.patrykandpatryk.vico.core.chart.dimensions.HorizontalDimensions
import com.patrykandpatryk.vico.core.chart.line.LineChart
import com.patrykandpatryk.vico.core.component.shape.LineComponent
import com.patrykandpatryk.vico.core.entry.ChartEntryModel
import com.patrykandpatryk.vico.core.entry.entryModelOf
import com.patrykandpatryk.vico.core.entry.composed.ComposedChartEntryModelProducer
import com.patrykandpatryk.vico.core.entry.entriesOf

val lightGreen = Color(0xFFABBC8A)
val lightGrayText = Color(0xFF888888)


@OptIn(ExperimentalMaterial3Api::class)
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
        return // No dibujes el gráfico si está cargando
    }

    // --- 2. ESTADO VACÍO (PERO CON EJE) ---
    // Comprueba si hay datos reales (no solo ceros)
    val hasData = lineData.any { it > 0f }

    if (!hasData) {
        Box(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .padding(horizontal = 2.dp), // Empareja el padding del Chart
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay datos de evolución para esta semana.",
                color = lightGrayText, // Usa un color de texto suave
                fontSize = 14.sp
            )
        }
        // Igualmente dibujamos el gráfico (sin modelo) para mostrar el eje X
    }

    val lineEntryModel: ChartEntryModel = entryModelOf(*lineData.toTypedArray())
    val columnEntryModel: ChartEntryModel = entryModelOf(*columnData.toTypedArray())

    // 5. Define el gráfico de Columna (la barra)
    val columnChart = columnChart(
        columns = listOf(
            LineComponent(
                color = lightGreen.copy(alpha = 0.5f).hashCode(),
                thicknessDp = 6f
            )
        )
    )

    // 6. Define el gráfico de Línea (la línea principal)
    val lineChart = lineChart(
        lines = listOf(
            lineSpec(
                lineColor = lightGreen, // Puedes usar el Color de Compose directamente
                lineThickness = 2.dp,
                lineBackgroundShader = verticalGradient(
                    arrayOf(lightGreen.copy(alpha = 0.4f), lightGreen.copy(alpha = 0.0f)),
                ),
                // Conector: Evita que la línea se dibuje conectando con los días
                // que tienen valor 0 (sin datos).
                pointConnector = object : LineChart.LineSpec.PointConnector {
                    override fun connect(
                        path: Path,
                        prevX: Float,
                        prevY: Float,
                        x: Float,
                        y: Float,
                        horizontalDimensions: HorizontalDimensions,
                        bounds: RectF
                    ) {
                        // Lógica clave: solo dibuja la línea si AMBOS puntos son distintos de 0.
                        if (prevY != 0f && y != 0f) {

                            // Si el path está vacío, es el primer segmento que dibujamos.
                            // Empezamos moviendo el "lápiz" al punto previo.
                            if (path.isEmpty) {
                                path.moveTo(prevX, prevY)
                            }
                            // Luego, dibujamos la línea hasta el punto actual.
                            path.lineTo(x, y)

                        } else {
                            // Si uno de los puntos es 0, no dibujamos.
                            // PERO, si el punto actual (y) NO es 0,
                            // debemos mover el "lápiz" a ese punto,
                            // para que sea el *inicio* del próximo segmento válido.
                            if (y != 0f) {
                                path.moveTo(x, y)
                            }
                        }
                    }
                }
            )
        )
    )

    // 7. Combina ambos gráficos
    val composedChart = columnChart.plus(lineChart)

    // 8. Definir el formateador del eje X
    val days = listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do")
    val bottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, chartValues ->
            // Lo convertimos a Int y lo usamos como índice para nuestra lista de días
            days.getOrNull(value.toInt()) ?: ""
        }

    // --- 6. Crear el Model Producer con 'entriesOf' ---
    val modelProducer = remember(lineData, columnData) {
        ComposedChartEntryModelProducer.build {
            // Usamos 'entriesOf' que devuelve List<ChartEntry>
            add(entriesOf(*columnData.toTypedArray()))
            add(entriesOf(*lineData.toTypedArray()))
        }
    }

    // --- 7. Crear el 'Producer' para el estado vacío (también con 'entriesOf') ---
    val emptyModelProducer = remember {
        val emptyData = List(7) { 0f }.toTypedArray()
        ComposedChartEntryModelProducer.build {
            add(entriesOf(*emptyData))
            add(entriesOf(*emptyData))
        }
    }

    // 9. Muestra el gráfico
    Chart(
        chart = composedChart,
        // Si hay datos, combina los modelos. Si no, pasa un modelo vacío
        // para que Vico dibuje el eje X correctamente.
        chartModelProducer = if (hasData) modelProducer else emptyModelProducer,
        modifier = Modifier
            .height(150.dp)
            .padding(horizontal = 2.dp),
        // Ocultamos el eje Y (startAxis)
        startAxis = null,
        // Eje X (bottomAxis) con nuestro formato personalizado
        bottomAxis = bottomAxis(
            valueFormatter = bottomAxisValueFormatter,
            label = axisLabelComponent(
                color = lightGrayText,
                horizontalPadding = 1.dp
            ),
            // Oculta la línea del eje y los "ticks" (marcas)
            axis = null,
            tick = null,
            guideline = null
        )
    )
}