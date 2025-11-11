package com.softfocus.features.library.assignments.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.softfocus.features.library.domain.models.Assignment
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.ui.theme.*
import java.time.format.DateTimeFormatter

@Composable
fun AssignmentCard(
    assignment: Assignment,
    onViewClick: () -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onViewClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = assignment.content.getMainImageUrl(),
                contentDescription = assignment.content.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp, 140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Gray828)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = assignment.content.title,
                        style = SourceSansSemiBold.copy(fontSize = 16.sp),
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (assignment.content.type) {
                                ContentType.Movie -> "Película"
                                ContentType.Music -> "Música"
                                ContentType.Video -> "Video"
                                ContentType.Weather -> "Clima"
                            },
                            style = SourceSansRegular.copy(fontSize = 12.sp),
                            color = Gray828,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        assignment.content.duration?.let { duration ->
                            Text(
                                text = "•",
                                style = SourceSansRegular.copy(fontSize = 12.sp),
                                color = Gray828
                            )
                            Text(
                                text = "${duration} min",
                                style = SourceSansRegular.copy(fontSize = 12.sp),
                                color = Gray828,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Text(
                        text = "Asignado: ${assignment.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yy"))}",
                        style = SourceSansRegular.copy(fontSize = 11.sp),
                        color = Gray828,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (assignment.isCompleted) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GreenEB2,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Completado: ${assignment.completedAt?.format(DateTimeFormatter.ofPattern("dd/MM/yy"))}",
                                style = SourceSansRegular.copy(fontSize = 11.sp),
                                color = GreenEB2,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onViewClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green65,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text(
                            text = "Ver",
                            style = SourceSansSemiBold.copy(fontSize = 13.sp),
                            color = GreenF2,
                            maxLines = 1
                        )
                    }

                    if (!assignment.isCompleted) {
                        Button(
                            onClick = onCompleteClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2D2D2D),
                                contentColor = Green65
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text(
                                text = "Completar",
                                style = SourceSansSemiBold.copy(fontSize = 13.sp),
                                color = GreenF2,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
