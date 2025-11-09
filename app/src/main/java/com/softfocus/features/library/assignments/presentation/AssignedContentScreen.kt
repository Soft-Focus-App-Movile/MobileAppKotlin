package com.softfocus.features.library.assignments.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.library.domain.models.Assignment
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.presentation.general.browse.components.ContentCard
import com.softfocus.features.library.presentation.general.browse.components.VideoCard
import com.softfocus.ui.theme.SourceSansBold
import com.softfocus.ui.theme.SourceSansRegular

@Composable
fun AssignedContentScreen(
    viewModel: AssignmentsViewModel,
    onContentClick: (String, ContentType) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    when (val state = uiState) {
        is AssignmentsUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AssignmentsUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = state.message,
                        style = SourceSansRegular.copy(fontSize = 14.sp),
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Button(onClick = { viewModel.retry() }) {
                        Text("Reintentar")
                    }
                }
            }
        }

        is AssignmentsUiState.Success -> {
            if (state.assignments.isEmpty()) {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes contenido asignado",
                        style = SourceSansRegular.copy(fontSize = 14.sp),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            } else {
                AssignedContentList(
                    assignments = state.assignments,
                    onContentClick = onContentClick,
                    onCompleteClick = { assignmentId ->
                        viewModel.completeAssignment(assignmentId)
                    },
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun AssignedContentList(
    assignments: List<Assignment>,
    onContentClick: (String, ContentType) -> Unit,
    onCompleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videoAssignments = assignments.filter { it.content.type == ContentType.Video }
    val otherAssignments = assignments.filter { it.content.type != ContentType.Video }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        if (otherAssignments.isNotEmpty()) {
            item {
                Text(
                    text = "Películas y Música",
                    style = SourceSansBold.copy(fontSize = 18.sp),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    items(otherAssignments) { assignment ->
                        ContentCard(
                            content = assignment.content,
                            isFavorite = false,
                            isSelected = false,
                            isSelectionMode = false,
                            onFavoriteClick = {},
                            onClick = {
                                onContentClick(
                                    assignment.content.id,
                                    assignment.content.type
                                )
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (videoAssignments.isNotEmpty()) {
            item {
                Text(
                    text = "Videos",
                    style = SourceSansBold.copy(fontSize = 18.sp),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(videoAssignments) { assignment ->
                VideoCard(
                    content = assignment.content,
                    isFavorite = false,
                    isSelected = false,
                    isSelectionMode = false,
                    onFavoriteClick = {},
                    onViewClick = {
                        assignment.content.youtubeUrl?.let { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    },
                    onClick = {}
                )
            }
        }
    }
}
