package com.softfocus.features.tracking.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.tracking.presentation.components.getMoodImageResource
import com.softfocus.features.tracking.presentation.state.QuickMoodState
import com.softfocus.features.tracking.presentation.viewmodel.TrackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickMoodEntrySheet(
    onDismiss: () -> Unit,
    onSubmit: (emoji: String, level: Int, content: String, durationSeconds: Int) -> Unit = { _, _, _, _ -> },
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val quickMoodState by viewModel.quickMoodState.collectAsState()
    var selectedEmoji by remember { mutableStateOf("😐") }
    var selectedLevel by remember { mutableStateOf(5) }
    var content by remember { mutableStateOf("") }
    val startTime = remember { System.currentTimeMillis() }

    LaunchedEffect(quickMoodState) {
        if (quickMoodState is QuickMoodState.Success) {
            val durationSeconds = ((System.currentTimeMillis() - startTime) / 1000).toInt()
            onSubmit(selectedEmoji, selectedLevel, content, durationSeconds)
            viewModel.resetQuickMoodState()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.resetQuickMoodState()
            onDismiss()
        },
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "¿Cómo te sientes ahora?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            // Emoji selector
            EmojiSelector(
                selectedLevel = selectedLevel,
                onSelect = { emoji, level ->
                    selectedEmoji = emoji
                    selectedLevel = level
                }
            )

            // Slider level 1-10
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nivel: $selectedLevel/10",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF333333)
                )
                Slider(
                    value = selectedLevel.toFloat(),
                    onValueChange = {
                        selectedLevel = it.toInt().coerceIn(1, 10)
                        selectedEmoji = emojiForMoodLevel(selectedLevel)
                    },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF6B8E7C),
                        activeTrackColor = Color(0xFF6B8E7C)
                    )
                )
            }

            // Text field para nota opcional
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("¿Qué pasó? (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // Error message
            if (quickMoodState is QuickMoodState.Error) {
                Text(
                    text = (quickMoodState as QuickMoodState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.resetQuickMoodState()
                        onDismiss()
                    }
                ) {
                    Text("Cancelar", color = Color(0xFF6B8E7C))
                }
                Button(
                    onClick = {
                        val durationSeconds =
                            ((System.currentTimeMillis() - startTime) / 1000).toInt()
                        viewModel.createQuickEmotionalEntry(
                            emoji = selectedEmoji,
                            level = selectedLevel,
                            content = content,
                            durationSeconds = durationSeconds
                        )
                    },
                    enabled = quickMoodState !is QuickMoodState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B8E7C)
                    )
                ) {
                    if (quickMoodState is QuickMoodState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
fun EmojiSelector(
    selectedLevel: Int,
    onSelect: (String, Int) -> Unit
) {
    val moods = listOf(
        "😢" to 2,
        "😕" to 4,
        "😐" to 5,
        "🙂" to 8,
        "😄" to 10
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        moods.forEach { (emoji, level) ->
            val selected = selectedLevel.toMoodBucket() == level.toMoodBucket()
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .clickable { onSelect(emoji, level) }
                    .alpha(if (selected) 1f else 0.45f),
                shape = CircleShape,
                color = if (selected) Color(0xFFE8F5E9) else Color.Transparent
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = getMoodImageResource(level)),
                        contentDescription = "Nivel emocional $level",
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }
    }
}

private fun Int.toMoodBucket(): Int {
    return when (this) {
        in 1..2 -> 2
        in 3..4 -> 4
        in 5..6 -> 5
        in 7..8 -> 8
        else -> 10
    }
}

private fun emojiForMoodLevel(level: Int): String {
    return when (level) {
        in 1..2 -> "😢"
        in 3..4 -> "😕"
        in 5..6 -> "😐"
        in 7..8 -> "🙂"
        else -> "😄"
    }
}
