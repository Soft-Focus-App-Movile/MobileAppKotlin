package com.softfocus.features.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.GreenDD
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.YellowCB9D

/**
 * Componente que muestra el tracking del estado de ánimo y opciones de ayuda
 */
@Composable
fun TrackingHome(
    daysRegistered: Int = 4,
    totalDays: Int = 7,
    daysFeelingSad: Int = 0, // MODIFICADO: default 0
    averageEmotionalLevel: Double? = null, // NUEVO
    insightMessage: String? = null, // NUEVO
    secondButtonText: String = "Buscar Psicólogo",
    onAIChatClick: () -> Unit = {},
    onSecondButtonClick: () -> Unit = {},
    onCardClick: () -> Unit = {} // NUEVO: Para hacer click en el card verde
) {
    Column {
        // Primera Card: Progreso semanal (CLICKEABLE)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable(onClick = onCardClick), // NUEVO
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = YellowCB9D)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (daysRegistered > 0) {
                            "Has registrado $daysRegistered ${if (daysRegistered == 1) "días" else "días"}"
                        } else {
                            "Empieza a registrar\ntus días"
                        },
                        style = SourceSansRegular,
                        fontSize = 15.sp,
                        color = Color.Black,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )

                    // NUEVO: Mostrar nivel emocional promedio
                    if (averageEmotionalLevel != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nivel emocional: ${String.format("%.1f", averageEmotionalLevel)}/10",
                            style = SourceSansRegular,
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                // Círculo de progreso
                Box(
                    modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Círculo de fondo (gris claro)
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(60.dp),
                        color = Color(0xFFE0E0E0),
                        strokeWidth = 6.dp,
                        trackColor = Color.Transparent
                    )

                    // Círculo de progreso (verde)
                    // El progreso se calcula: (días % 7) / 7, pero si es múltiplo de 7 mostramos círculo completo
                    val progress = if (daysRegistered == 0) {
                        0f
                    } else {
                        val daysInWeek = daysRegistered % 7
                        if (daysInWeek == 0) 1f else daysInWeek / 7f
                    }

                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(60.dp),
                        color = Green49,
                        strokeWidth = 6.dp,
                        trackColor = Color.Transparent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Segunda Card: Ayuda con IA o Psicólogo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GreenDD)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(start = 80.dp), // Espacio para la imagen
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // MODIFICADO: Usar mensaje del backend o mensaje por defecto
                        Text(
                            text = insightMessage ?: if (daysFeelingSad > 0) {
                                "Llevas $daysFeelingSad días sintiéndote mal,\n¿necesitas ayuda?"
                            } else {
                                "¿Necesitas hablar con alguien?"
                            },
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Color.Black,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onAIChatClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YellowCB9D
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Hablar con IA",
                                style = SourceSansRegular,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onSecondButtonClick,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YellowCB9D
                            )
                        ) {
                            Text(
                                text = secondButtonText,
                                style = SourceSansRegular,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Imagen del osito overlapping la card a la izquierda
            Image(
                painter = painterResource(id = R.drawable.fuzzy_focus),
                contentDescription = "Ayuda emocional",
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = (-50).dp, y = 0.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackingHomePreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        TrackingHome(
            daysRegistered = 4,
            totalDays = 7,
            daysFeelingSad = 0,
            averageEmotionalLevel = 7.5,
            insightMessage = "Your emotional levels have been lower than usual. Consider reaching out for support.",
            secondButtonText = "Buscar Psicólogo",
            onAIChatClick = {},
            onSecondButtonClick = {},
            onCardClick = {}
        )
    }
}