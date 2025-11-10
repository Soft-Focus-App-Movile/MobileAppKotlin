package com.softfocus.features.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
    daysFeelingSad: Int = 3,
    secondButtonText: String = "Buscar Psicólogo",
    onAIChatClick: () -> Unit = {},
    onSecondButtonClick: () -> Unit = {}
) {
    Column {
        // Primera Card: Progreso semanal
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
                Text(
                    text = "Has registrado $daysRegistered días\nesta semana",
                    style = SourceSansRegular,
                    fontSize = 15.sp,
                    color = Color.Black,
                    lineHeight = 18.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                )

                // Círculo de progreso
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: Aquí se agregará el progreso visual cuando haya endpoint
                    CircularProgressIndicator(
                        progress = daysRegistered.toFloat() / totalDays.toFloat(),
                        modifier = Modifier.size(50.dp),
                        color = Green49,
                        strokeWidth = 4.dp,
                        trackColor = Color.LightGray
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
                        Text(
                            text = "Llevas $daysFeelingSad días sintiéndote mal,",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "¿necesitas ayuda?",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Color.Black
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
            daysFeelingSad = 3,
            secondButtonText = "Buscar Psicólogo",
            onAIChatClick = {},
            onSecondButtonClick = {}
        )
    }
}
