package com.softfocus.features.tracking.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LevelsStep(
    emotionalLevel: Int,
    onEmotionalLevelChanged: (Int) -> Unit,
    energyLevel: Int,
    onEnergyLevelChanged: (Int) -> Unit,
    sleepHours: Int,
    onSleepHoursChanged: (Int) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Últimas preguntas",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Emotional Level
        LevelControl(
            label = "Nivel Emocional",
            value = emotionalLevel,
            onValueChange = onEmotionalLevelChanged,
            minValue = 1,
            maxValue = 10
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Energy Level
        LevelControl(
            label = "Nivel de Energía",
            value = energyLevel,
            onValueChange = onEnergyLevelChanged,
            minValue = 1,
            maxValue = 10
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sleep Hours
        LevelControl(
            label = "Horas de Sueño",
            value = sleepHours,
            onValueChange = onSleepHoursChanged,
            minValue = 0,
            maxValue = 12
        )

        Spacer(modifier = Modifier.height(48.dp))

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

@Composable
private fun LevelControl(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    minValue: Int,
    maxValue: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Minus button
            IconButton(
                onClick = { if (value > minValue) onValueChange(value - 1) },
                enabled = value > minValue
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Decrease",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Value display
            Card(
                modifier = Modifier.size(80.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = value.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Plus button
            IconButton(
                onClick = { if (value < maxValue) onValueChange(value + 1) },
                enabled = value < maxValue
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}