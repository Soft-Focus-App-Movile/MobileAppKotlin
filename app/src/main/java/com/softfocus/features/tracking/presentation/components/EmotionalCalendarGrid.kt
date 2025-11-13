package com.softfocus.features.tracking.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.features.tracking.domain.model.EmotionalCalendarEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun EmotionalCalendarGrid(
    entries: List<EmotionalCalendarEntry>,
    onDateClick: (EmotionalCalendarEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sá").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        val currentMonth = LocalDate.now().withDayOfMonth(1)
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayOfWeek = currentMonth.dayOfWeek.value % 7
        val totalCells = firstDayOfWeek + daysInMonth

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Empty cells before first day
            items(firstDayOfWeek) {
                Box(modifier = Modifier.size(48.dp))
            }

            // Days of month
            items(daysInMonth) { dayIndex ->
                val day = dayIndex + 1
                val date = currentMonth.withDayOfMonth(day)
                val entry = entries.find {
                    LocalDate.parse(it.date.substringBefore("T")) == date
                }

                EmotionalCalendarDay(
                    day = day,
                    moodLevel = entry?.moodLevel,
                    onClick = { entry?.let { onDateClick(it) } }
                )
            }
        }
    }
}

@Composable
private fun EmotionalCalendarDay(
    day: Int,
    moodLevel: Int?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (moodLevel != null) Color(0xFFE8F5E9) else Color.Transparent)
            .clickable(enabled = moodLevel != null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (moodLevel != null) {
            // NUEVO: Usar imagen según el mood level
            Image(
                painter = painterResource(id = getMoodImageResource(moodLevel)),
                contentDescription = "Mood level $moodLevel",
                modifier = Modifier.size(32.dp)
            )
        } else {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

// NUEVA FUNCIÓN: Mapear mood level a recurso de imagen
private fun getMoodImageResource(moodLevel: Int): Int {
    return when (moodLevel) {
        in 1..2 -> R.drawable.calendar_emoji_angry     // Muy triste
        in 3..4 -> R.drawable.calendar_emoji_sad          // Triste
        in 5..6 -> R.drawable.calendar_emoji_serius     // Neutral
        in 7..8 -> R.drawable.calendar_emoji_happy        // Feliz
        else -> R.drawable.calendar_emoji_joy     // Muy feliz
    }
}