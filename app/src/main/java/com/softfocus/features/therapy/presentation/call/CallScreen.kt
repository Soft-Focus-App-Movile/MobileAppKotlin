package com.softfocus.features.therapy.presentation.call

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.softfocus.ui.components.ProfileAvatar

/**
 * Active-call screen (Agora). Requests camera/mic permission, starts the outgoing call, renders the
 * local/remote video (or an avatar for audio calls) and the in-call controls.
 */
@Composable
fun CallScreen(
    viewModel: CallViewModel,
    calleeAvatarUrl: String?,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val requiredPermissions = remember(uiState.isVideo) {
        if (uiState.isVideo)
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        else
            arrayOf(Manifest.permission.RECORD_AUDIO)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.all { it }) {
            viewModel.start()
        } else {
            Toast.makeText(context, "Se necesitan permisos de cámara y micrófono", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        val allGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) viewModel.start() else permissionLauncher.launch(requiredPermissions)
    }

    // Leave the screen when the call ends.
    LaunchedEffect(uiState.phase) {
        if (uiState.phase == CallPhase.Ended) {
            navController.popBackStack()
        }
    }

    // Back button hangs up properly (ends the call on the backend) instead of just leaving,
    // so the user isn't left "in a call" and blocked from starting new ones.
    BackHandler(enabled = uiState.phase != CallPhase.Ended) {
        viewModel.hangUp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1F17))
    ) {
        val showRemoteVideo = uiState.isVideo && uiState.remoteUid != null && uiState.phase == CallPhase.InCall

        if (showRemoteVideo) {
            // Remote video fills the screen.
            key(uiState.remoteUid) {
                AndroidView(
                    factory = {
                        viewModel.createRendererView().also { view ->
                            viewModel.setupRemoteVideo(view, uiState.remoteUid!!)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // Ringing / audio call / connecting: show the callee avatar and status.
            CallStatusContent(
                calleeName = uiState.calleeName,
                calleeAvatarUrl = calleeAvatarUrl,
                statusText = statusTextFor(uiState),
                isError = uiState.phase == CallPhase.Error
            )
        }

        // Local video preview (small, top-end) for video calls once the camera is on.
        if (uiState.isVideo && uiState.cameraOn && uiState.phase != CallPhase.Error && uiState.phase != CallPhase.Connecting) {
            AndroidView(
                factory = {
                    viewModel.createRendererView().also { view ->
                        viewModel.setupLocalVideo(view)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .size(width = 110.dp, height = 160.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        // When showing remote video, overlay the name + status at the top.
        if (showRemoteVideo) {
            Text(
                text = uiState.calleeName,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    // padding-end grande para no quedar debajo del preview de la cámara (arriba a la derecha)
                    .padding(start = 20.dp, top = 24.dp, end = 150.dp)
            )
        }

        CallControls(
            uiState = uiState,
            onToggleMic = viewModel::toggleMic,
            onToggleCamera = viewModel::toggleCamera,
            onSwitchCamera = viewModel::switchCamera,
            onToggleSpeaker = viewModel::toggleSpeaker,
            onHangUp = viewModel::hangUp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 40.dp)
        )
    }
}

private fun statusTextFor(state: CallUiState): String = when (state.phase) {
    CallPhase.Connecting -> "Conectando..."
    CallPhase.Ringing -> "Llamando..."
    CallPhase.InCall -> "En llamada"
    CallPhase.Ended -> "Llamada finalizada"
    CallPhase.Error -> state.errorMessage ?: "No se pudo iniciar la llamada"
}

@Composable
private fun BoxScope.CallStatusContent(
    calleeName: String,
    calleeAvatarUrl: String?,
    statusText: String,
    isError: Boolean
) {
    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileAvatar(
            imageUrl = calleeAvatarUrl?.takeIf { it.isNotBlank() && it != "null" },
            fullName = calleeName,
            size = 120.dp,
            fontSize = 48.sp,
            backgroundColor = Color(0xFF2E4A3B),
            textColor = Color.White,
            shape = CircleShape
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = calleeName,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = statusText,
            color = if (isError) Color(0xFFFF8A80) else Color(0xFFB8C9BF),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun CallControls(
    uiState: CallUiState,
    onToggleMic: () -> Unit,
    onToggleCamera: () -> Unit,
    onSwitchCamera: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onHangUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mic
        CallControlButton(
            icon = if (uiState.micMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
            description = "Micrófono",
            background = if (uiState.micMuted) Color.White else Color(0x33FFFFFF),
            tint = if (uiState.micMuted) Color.Black else Color.White,
            onClick = onToggleMic
        )

        if (uiState.isVideo) {
            // Camera on/off
            CallControlButton(
                icon = if (uiState.cameraOn) Icons.Filled.Videocam else Icons.Filled.VideocamOff,
                description = "Cámara",
                background = if (!uiState.cameraOn) Color.White else Color(0x33FFFFFF),
                tint = if (!uiState.cameraOn) Color.Black else Color.White,
                onClick = onToggleCamera
            )
            // Switch camera
            CallControlButton(
                icon = Icons.Filled.Cameraswitch,
                description = "Cambiar cámara",
                background = Color(0x33FFFFFF),
                tint = Color.White,
                onClick = onSwitchCamera
            )
        } else {
            // Speaker (audio calls): always a clean speaker icon (no "slash"), like WhatsApp.
            // On/off is shown by the highlight (filled white when the loudspeaker is active).
            CallControlButton(
                icon = Icons.Filled.VolumeUp,
                description = "Altavoz",
                background = if (uiState.speakerOn) Color.White else Color(0x33FFFFFF),
                tint = if (uiState.speakerOn) Color.Black else Color.White,
                onClick = onToggleSpeaker
            )
        }

        // Hang up
        CallControlButton(
            icon = Icons.Filled.CallEnd,
            description = "Colgar",
            background = Color(0xFFE53935),
            tint = Color.White,
            onClick = onHangUp
        )
    }
}

@Composable
private fun CallControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    background: Color,
    tint: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(background)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
    }
}
