package com.softfocus.features.therapy.presentation.call

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.softfocus.core.navigation.Route
import com.softfocus.features.therapy.domain.models.IncomingCallInfo
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.ui.components.ProfileAvatar
import kotlinx.coroutines.launch

/**
 * Global overlay that rings the user when a call comes in over /callHub. Hosted above the NavHost so
 * it appears on top of whatever screen is showing. Accept navigates to the in-call screen in
 * "answer" mode; reject notifies the backend and dismisses.
 */
@Composable
fun IncomingCallHost(navController: NavHostController) {
    val context = LocalContext.current
    val service = remember {
        // Ensure the module has a context even if this composes before the Splash screen runs.
        TherapyPresentationModule.init(context)
        TherapyPresentationModule.getCallSignalRService()
    }
    val incoming by service.incomingCall.collectAsState()
    val scope = rememberCoroutineScope()

    val call = incoming ?: return

    IncomingCallOverlay(
        info = call,
        onAccept = {
            service.clearIncomingCall()
            navController.navigate(
                Route.Call.createAnswerRoute(
                    callId = call.callId,
                    callType = call.callType,
                    calleeName = call.callerName
                )
            )
        },
        onReject = {
            service.clearIncomingCall()
            scope.launch {
                TherapyPresentationModule.getRejectCallUseCase().invoke(call.callId)
            }
        }
    )
}

@Composable
private fun IncomingCallOverlay(
    info: IncomingCallInfo,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF20E1F17)) // near-opaque to cover whatever is underneath
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileAvatar(
                imageUrl = null,
                fullName = info.callerName,
                size = 120.dp,
                fontSize = 48.sp,
                backgroundColor = Color(0xFF2E4A3B),
                textColor = Color.White,
                shape = CircleShape
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = info.callerName,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (info.isVideo) "Videollamada entrante..." else "Llamada entrante...",
                color = Color(0xFFB8C9BF),
                fontSize = 16.sp
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 56.dp, start = 48.dp, end = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reject
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onReject,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935))
                ) {
                    Icon(Icons.Filled.CallEnd, contentDescription = "Rechazar", tint = Color.White, modifier = Modifier.size(30.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text("Rechazar", color = Color.White, fontSize = 13.sp)
            }

            // Accept
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onAccept,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF43A047))
                ) {
                    Icon(
                        imageVector = if (info.isVideo) Icons.Filled.Videocam else Icons.Filled.Call,
                        contentDescription = "Aceptar",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text("Aceptar", color = Color.White, fontSize = 13.sp)
            }
        }
    }
}
