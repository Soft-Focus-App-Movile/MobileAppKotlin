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

@Composable
fun CategorySelectionStep(
    title: String,
    categories: List<String>,
    selectedCategories: List<String>,
    onCategoriesSelected: (List<String>) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Category buttons
        categories.forEach { category ->
            val isSelected = selectedCategories.contains(category)

            OutlinedButton(
                onClick = {
                    onCategoriesSelected(
                        if (isSelected) {
                            selectedCategories - category
                        } else {
                            selectedCategories + category
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) Color.White else Color.Transparent,
                    contentColor = if (isSelected) Color(0xFF6B8E7C) else Color.White
                ),
                border = BorderStroke(2.dp, Color.White)
            ) {
                Text(
                    text = category,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF6B8E7C)
            ),
            enabled = selectedCategories.isNotEmpty()
        ) {
            Text("Continuar", fontWeight = FontWeight.Bold)
        }
    }
}