package com.softfocus.features.tracking.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SymptomsSelectionStep(
    symptoms: List<String>,
    selectedSymptoms: List<String>,
    onSymptomsSelected: (List<String>) -> Unit,
    onNext: () -> Unit
) {
    // Estado para controlar qué tipo de síntomas mostrar
    var showPositiveSymptoms by remember { mutableStateOf(false) }

    // Lista de síntomas negativos
    val negativeSymptoms = listOf(
        "😰 Ansiedad" to "Ansiedad",
        "😫 Cansancio" to "Cansancio",
        "😠 Irritabilidad" to "Irritabilidad",
        "😢 Tristeza" to "Tristeza",
        "😴 Estrés" to "Estrés",
        "😌 Insomnio" to "Insomnio",
        "🤕 Dolor físico" to "Dolor físico",
        "⚪ Cambio de apetito" to "Cambio de apetito"
    )

    // Lista de síntomas positivos
    val positiveSymptoms = listOf(
        "😊 Felicidad" to "Felicidad",
        "⚡ Energía" to "Energía",
        "🎯 Motivación" to "Motivación",
        "😌 Tranquilidad" to "Tranquilidad",
        "🤗 Optimismo" to "Optimismo",
        "💪 Confianza" to "Confianza",
        "🧘 Relajación" to "Relajación",
        "✨ Creatividad" to "Creatividad"
    )

    // Seleccionar lista según el toggle
    val currentSymptomsList = if (showPositiveSymptoms) positiveSymptoms else negativeSymptoms

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Tienes alguno de estos\nsíntomas?",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // NUEVO: Toggle entre negativos y positivos
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.2f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón Negativos
                Button(
                    onClick = { showPositiveSymptoms = false },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!showPositiveSymptoms) Color.White else Color.Transparent,
                        contentColor = if (!showPositiveSymptoms) Color(0xFF6B8E7C) else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Negativos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botón Positivos
                Button(
                    onClick = { showPositiveSymptoms = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showPositiveSymptoms) Color.White else Color.Transparent,
                        contentColor = if (showPositiveSymptoms) Color(0xFF6B8E7C) else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Positivos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de síntomas
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            currentSymptomsList.forEach { (displayText, value) ->
                val isSelected = selectedSymptoms.contains(value)

                OutlinedButton(
                    onClick = {
                        onSymptomsSelected(
                            if (isSelected) {
                                selectedSymptoms - value
                            } else {
                                selectedSymptoms + value
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) Color.White else Color.Transparent,
                        contentColor = if (isSelected) Color(0xFF6B8E7C) else Color.White
                    ),
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Radio button visual
                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Surface(
                                    modifier = Modifier.size(24.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF6B8E7C)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Surface(
                                            modifier = Modifier.size(12.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color.White
                                        ) {}
                                    }
                                }
                            } else {
                                Surface(
                                    modifier = Modifier.size(24.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Transparent,
                                    border = BorderStroke(2.dp, Color.White)
                                ) {}
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = displayText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar conteo de síntomas seleccionados
        if (selectedSymptoms.isNotEmpty()) {
            Text(
                text = "${selectedSymptoms.size} síntoma(s) seleccionado(s)",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón de continuar
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF6B8E7C)
            )
        ) {
            Text("Continuar", fontWeight = FontWeight.Bold)
        }
    }
}