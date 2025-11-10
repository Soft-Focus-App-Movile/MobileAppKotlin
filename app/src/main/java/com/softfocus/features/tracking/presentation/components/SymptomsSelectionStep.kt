package com.softfocus.features.tracking.presentation.components

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

@Composable
fun SymptomsSelectionStep(
    symptoms: List<String>,
    selectedSymptoms: List<String>,
    onSymptomsSelected: (List<String>) -> Unit,
    onNext: () -> Unit
) {
    var symptomsText by remember { mutableStateOf(selectedSymptoms.joinToString(", ")) }

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

        Spacer(modifier = Modifier.height(48.dp))

        // Text field para escribir síntomas
        OutlinedTextField(
            value = symptomsText,
            onValueChange = { symptomsText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            placeholder = {
                Text(
                    "Ejemplo: mucha energia, felicidad, ansiedad...",
                    color = Color.White.copy(alpha = 0.6f)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.2f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            maxLines = 8
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Convertir el texto en lista de síntomas
                val symptomsList = if (symptomsText.isNotBlank()) {
                    listOf(symptomsText)
                } else {
                    emptyList()
                }
                onSymptomsSelected(symptomsList)
                onNext()
            },
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