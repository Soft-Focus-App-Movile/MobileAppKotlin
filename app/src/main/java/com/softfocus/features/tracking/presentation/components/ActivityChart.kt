package com.softfocus.features.tracking.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.softfocus.features.tracking.domain.model.CheckIn

@Composable
fun ActivityChart(
    checkIns: List<CheckIn>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Days of week
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sá").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val width = size.width
            val height = size.height
            val pointSpacing = width / 7

            // Draw grid lines
            for (i in 0..4) {
                val y = height * i / 4
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }

            // Draw line chart
            if (checkIns.isNotEmpty()) {
                val path = Path()
                val points = checkIns.takeLast(7).mapIndexed { index, checkIn ->
                    val x = pointSpacing * index
                    val y = height - (height * checkIn.emotionalLevel / 10f)
                    Offset(x, y)
                }

                if (points.isNotEmpty()) {
                    path.moveTo(points[0].x, points[0].y)
                    points.forEach { point ->
                        path.lineTo(point.x, point.y)
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFF6B8E7C),
                        style = Stroke(width = 3f)
                    )

                    // Draw points
                    points.forEach { point ->
                        drawCircle(
                            color = Color(0xFF6B8E7C),
                            radius = 6f,
                            center = point
                        )
                    }
                }
            }
        }
    }
}