package com.softfocus.features.tracking.presentation.components

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    emoji = entry?.emotionalEmoji,
                    onClick = { entry?.let { onDateClick(it) } }
                )
            }
        }
    }
}

@Composable
private fun EmotionalCalendarDay(
    day: Int,
    emoji: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (emoji != null) Color(0xFFE8F5E9) else Color.Transparent)
            .clickable(enabled = emoji != null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (emoji != null) {
            Text(
                text = emoji,
                fontSize = 24.sp
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
