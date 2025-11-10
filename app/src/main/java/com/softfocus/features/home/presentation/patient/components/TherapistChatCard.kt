package com.softfocus.features.home.presentation.patient.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.home.presentation.patient.TherapistState
import com.softfocus.ui.components.ProfileAvatar
import com.softfocus.ui.theme.Gray787
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.SourceSansSemiBold
import com.softfocus.ui.theme.White
import com.softfocus.ui.theme.YellowCB9D

@Composable
fun TherapistChatCard(
    therapistState: TherapistState,
    onRetry: () -> Unit,
    onChatClick: () -> Unit = {}
) {
    when (therapistState) {
        is TherapistState.Loading -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = YellowCB9D)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Green49,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        is TherapistState.Success -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable { onChatClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = YellowCB9D)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    ProfileAvatar(
                        imageUrl = therapistState.psychologist.profileImageUrl,
                        fullName = therapistState.psychologist.fullName,
                        size = 48.dp,
                        fontSize = 18.sp,
                        backgroundColor = Color.White,
                        textColor = Green49
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = therapistState.psychologist.fullName,
                            style = SourceSansSemiBold,
                            fontSize = 14.sp,
                            color = White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Burbuja de chat para el último mensaje
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(
                                topStart = 2.dp,
                                topEnd = 12.dp,
                                bottomEnd = 12.dp,
                                bottomStart = 12.dp
                            ),
                            color = Color.White
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "Buenas tardes Laura, no te olvides de realizar...",
                                    style = SourceSansRegular,
                                    fontSize = 15.sp,
                                    color = Color.DarkGray,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "5:00pm",
                                    style = SourceSansRegular,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }
        }
        is TherapistState.NoTherapist -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = YellowCB9D)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes un terapeuta asignado",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray787
                    )
                }
            }
        }
        is TherapistState.Error -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = YellowCB9D)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error al cargar terapeuta",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray787
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onRetry) {
                        Text(
                            text = "Reintentar",
                            style = SourceSansSemiBold,
                            fontSize = 14.sp,
                            color = Green49
                        )
                    }
                }
            }
        }
    }
}
