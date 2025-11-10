package com.softfocus.features.tracking.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.softfocus.R

@Composable
fun MoodSelectionStep(
    selectedMood: Int,
    onMoodSelected: (Int) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cómo te sientes\nel día de hoy?",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Mood face - Changed from Text to Image
        Image(
            painter = painterResource(id = getMoodDrawable(selectedMood)),
            contentDescription = getMoodText(selectedMood), // For accessibility
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Mood slider
        MoodSlider(
            value = selectedMood,
            onValueChange = onMoodSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Calificá tu día",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = getMoodText(selectedMood),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
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
            )
        ) {
            Text("Continuar", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MoodSlider(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Slider(
        value = value.toFloat(),
        onValueChange = { onValueChange(it.toInt()) },
        valueRange = 1f..10f,
        steps = 8,
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.White,
            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
        )
    )
}

@DrawableRes
private fun getMoodDrawable(level: Int): Int {
    return when (level) {
        in 1..2 -> R.drawable.calendar_emoji_angry
        in 3..4 -> R.drawable.calendar_emoji_sad
        in 5..6 -> R.drawable.calendar_emoji_serius
        in 7..8 -> R.drawable.calendar_emoji_happy
        else -> R.drawable.calendar_emoji_joy
    }
}

private fun getMoodText(level: Int): String {
    return when (level) {
        in 1..2 -> "Terrible"
        in 3..4 -> "Mal"
        in 5..6 -> "Regular"
        in 7..8 -> "Bien"
        else -> "Excelente"
    }
}
