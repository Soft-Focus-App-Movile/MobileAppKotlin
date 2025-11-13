package com.softfocus.features.ai.presentation.emotion

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil3.compose.rememberAsyncImagePainter
import com.softfocus.R
import com.softfocus.features.ai.presentation.di.AIPresentationModule
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.Green37
import com.softfocus.ui.theme.GreenEC
import com.softfocus.ui.theme.White
import com.softfocus.ui.theme.SourceSansBold
import com.softfocus.ui.theme.SourceSansRegular
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionDetectionScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = remember { AIPresentationModule.getEmotionDetectionViewModel(context) }
    val state by viewModel.state.collectAsState()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var photoFile by remember { mutableStateOf<File?>(null) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoFile != null && photoFile!!.exists()) {
            imageUri = tempCameraUri
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            val inputStream = context.contentResolver.openInputStream(it)
            val tempFile = File.createTempFile("gallery_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            photoFile = tempFile
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text ="Detección de Emoción",
                        style = CrimsonSemiBold.copy(fontSize = 25.sp),
                        color = Green49
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Green49
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = White,
                    navigationIconContentColor = White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // GIF del panda como fondo cuando no hay imagen seleccionada
            if (imageUri == null && state.emotionAnalysis == null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = R.drawable.soft_panda
                    ),
                    contentDescription = "Panda animado",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!hasCameraPermission) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Se necesita permiso de cámara para usar esta función",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Spacer para posicionar los botones más arriba
                if (imageUri == null && state.emotionAnalysis == null && !state.isLoading) {
                    Spacer(modifier = Modifier.weight(0.6f))
                }

            // Preview de la imagen cuando hay una capturada (tanto antes como después del análisis)
            if (imageUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.45f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri),
                        contentDescription = "Imagen capturada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Mostrar resultado del análisis justo después de la imagen
            state.emotionAnalysis?.let { analysis ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = GreenEC
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Resultado del Análisis",
                            style = CrimsonSemiBold,
                            fontSize = 20.sp,
                            color = Green37
                        )

                        HorizontalDivider(color = Green49.copy(alpha = 0.3f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = getEmotionEmojiResource(analysis.emotion)),
                                    contentDescription = "Emoji de emoción",
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = translateEmotion(analysis.emotion),
                                    style = CrimsonSemiBold,
                                    fontSize = 22.sp,
                                    color = Green49
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Confianza:",
                                style = SourceSansRegular,
                                fontSize = 16.sp,
                                color = Gray828
                            )
                            Text(
                                text = "${(analysis.confidence * 100).toInt()}%",
                                style = CrimsonSemiBold,
                                fontSize = 22.sp,
                                color = Green37
                            )
                        }

                        if (analysis.checkInCreated) {
                            HorizontalDivider(color = Green49.copy(alpha = 0.3f))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Check",
                                    tint = Green37,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Registro automático en tu calendario emocional",
                                    style = SourceSansRegular,
                                    fontSize = 14.sp,
                                    color = Green37,
                                    softWrap = true
                                )
                            }
                        }

                        if (analysis.allEmotions.isNotEmpty()) {
                            HorizontalDivider(color = Green49.copy(alpha = 0.3f))
                            Text(
                                text = "Todas las Emociones:",
                                style = SourceSansBold,
                                fontSize = 16.sp,
                                color = Green37
                            )
                            analysis.allEmotions.forEach { (emotion, confidence) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = getEmotionEmojiResource(emotion)),
                                            contentDescription = "Emoji de emoción",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = translateEmotion(emotion),
                                            style = SourceSansRegular,
                                            fontSize = 14.sp,
                                            color = Gray828
                                        )
                                    }
                                    Text(
                                        text = "${(confidence * 100).toInt()}%",
                                        style = SourceSansBold,
                                        fontSize = 14.sp,
                                        color = Green49
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.reset()
                                imageUri = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Green49
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Analizar Otra Foto",
                                style = SourceSansBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Mostrar opciones de análisis solo cuando hay imagen pero no hay resultado
            if (imageUri != null && state.emotionAnalysis == null && !state.isCheckingTodayCheckIn) {
                if (state.hasCheckInToday) {

                    Text(
                        text = "Ya creaste tu registro diario",
                        style = CrimsonSemiBold,
                        fontSize = 23.sp,
                        color = Green49,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Solo analizaremos tu emoción en esta foto",
                        style = SourceSansRegular,
                        fontSize = 16.sp,
                        color = Gray828,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            photoFile?.let { viewModel.analyzeEmotion(it, autoCheckIn = false) }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !state.isLoading && photoFile != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green49
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Analizar Emoción",
                            style = SourceSansBold,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    // Primera vez del día - puede elegir
                    Text(
                        text = "¿Crear registro diario?",
                        style = CrimsonSemiBold,
                        fontSize = 22.sp,
                        color = Gray828,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "¿Guardar estado en tu calendario ?",
                        style = SourceSansRegular,
                        fontSize = 16.sp,
                        color = Gray828,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            photoFile?.let { viewModel.analyzeEmotion(it, autoCheckIn = true) }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !state.isLoading && photoFile != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = com.softfocus.ui.theme.Green49
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Sí, crear registro",
                            style = com.softfocus.ui.theme.SourceSansBold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            photoFile?.let { viewModel.analyzeEmotion(it, autoCheckIn = false) }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !state.isLoading && photoFile != null,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Green49
                        )
                    ) {
                        Text(
                            text = "Solo analizar",
                            style = SourceSansBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            if (imageUri == null || state.emotionAnalysis != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (hasCameraPermission) {
                                val file = createImageFile(context)
                                photoFile = file
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                tempCameraUri = uri
                                imageUri = null // Limpiar la imagen anterior
                                cameraLauncher.launch(uri)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = !state.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green49
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cámara",
                            style = SourceSansBold,
                            fontSize = 16.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = White,
                            contentColor = Green49
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Galería",
                            style = SourceSansBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator()
                Text("Analizando emoción...")
            }

            state.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = com.softfocus.ui.theme.RedE8.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Error",
                            style = CrimsonSemiBold,
                            fontSize = 18.sp,
                            color = com.softfocus.ui.theme.RedE8
                        )
                        Text(
                            text = error,
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Gray828,
                            modifier = Modifier.fillMaxWidth(),
                            softWrap = true
                        )
                        Button(
                            onClick = { viewModel.clearError() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.softfocus.ui.theme.RedE8
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Cerrar",
                                style = SourceSansBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

                // Spacer para centrar los botones verticalmente (parte inferior)
                if (imageUri == null && state.emotionAnalysis == null && !state.isLoading) {
                    Spacer(modifier = Modifier.weight(1.4f))
                }
            }
        }
    }
}

private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.cacheDir
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}

private fun translateEmotion(emotion: String): String {
    return when (emotion.lowercase()) {
        "angry" -> "Enojado/a"
        "disgust" -> "Disgustado/a"
        "fear" -> "Asustado/a"
        "happy" -> "Feliz"
        "sad" -> "Triste"
        "surprise" -> "Sorprendido/a"
        "neutral" -> "Neutral"
        "joy" -> "Alegría"
        "anxious" -> "Ansioso/a"
        "calm" -> "Calmado/a"
        "excited" -> "Emocionado/a"
        "worried" -> "Preocupado/a"
        "stressed" -> "Estresado/a"
        "depressed" -> "Deprimido/a"
        "frustrated" -> "Frustrado/a"
        else -> emotion.replaceFirstChar { it.uppercase() }
    }
}

private fun getEmotionEmojiResource(emotion: String): Int {
    return when (emotion.lowercase()) {
        "angry" -> R.drawable.calendar_emoji_angry
        "happy" -> R.drawable.calendar_emoji_happy
        "joy" -> R.drawable.calendar_emoji_joy
        "sad" -> R.drawable.calendar_emoji_sad
        "neutral" -> R.drawable.calendar_emoji_serius
        "disgust" -> R.drawable.calendar_emoji_angry
        "fear" -> R.drawable.calendar_emoji_sad
        "surprise" -> R.drawable.calendar_emoji_happy
        "anxious" -> R.drawable.calendar_emoji_sad
        "calm" -> R.drawable.calendar_emoji_serius
        "excited" -> R.drawable.calendar_emoji_joy
        "worried" -> R.drawable.calendar_emoji_sad
        "stressed" -> R.drawable.calendar_emoji_angry
        "depressed" -> R.drawable.calendar_emoji_sad
        "frustrated" -> R.drawable.calendar_emoji_angry
        else -> R.drawable.calendar_emoji_serius
    }
}

// Previews
@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Estado Inicial")
private fun EmotionDetectionScreenPreview() {
    Surface {
        EmotionDetectionScreenContent(
            state = EmotionDetectionState(),
            onNavigateBack = {},
            onTakePhoto = {},
            onSelectFromGallery = {},
            onAnalyze = {},
            onClearError = {},
            onReset = {}
        )
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Con Resultado - Primera vez")
private fun EmotionDetectionWithResultPreview() {
    Surface {
        EmotionDetectionScreenContent(
            state = EmotionDetectionState(
                emotionAnalysis = com.softfocus.features.ai.domain.models.EmotionAnalysis(
                    analysisId = "123",
                    emotion = "happy",
                    confidence = 0.85,
                    allEmotions = mapOf(
                        "happy" to 0.85,
                        "neutral" to 0.10,
                        "surprise" to 0.05
                    ),
                    analyzedAt = java.time.LocalDateTime.now(),
                    checkInCreated = true,
                    checkInId = "check123"
                ),
                hasCheckInToday = false
            ),
            onNavigateBack = {},
            onTakePhoto = {},
            onSelectFromGallery = {},
            onAnalyze = {},
            onClearError = {},
            onReset = {}
        )
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Ya tiene check-in hoy")
private fun EmotionDetectionAlreadyCheckedInPreview() {
    Surface {
        EmotionDetectionScreenContent(
            state = EmotionDetectionState(
                hasCheckInToday = true,
                isCheckingTodayCheckIn = false
            ),
            onNavigateBack = {},
            onTakePhoto = {},
            onSelectFromGallery = {},
            onAnalyze = {},
            onClearError = {},
            onReset = {}
        )
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Con Error")
private fun EmotionDetectionWithErrorPreview() {
    Surface {
        EmotionDetectionScreenContent(
            state = EmotionDetectionState(
                error = "Facial analysis usage limit exceeded. Please upgrade to Premium or wait for weekly reset."
            ),
            onNavigateBack = {},
            onTakePhoto = {},
            onSelectFromGallery = {},
            onAnalyze = {},
            onClearError = {},
            onReset = {}
        )
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Cargando")
private fun EmotionDetectionLoadingPreview() {
    Surface {
        EmotionDetectionScreenContent(
            state = EmotionDetectionState(
                isLoading = true
            ),
            onNavigateBack = {},
            onTakePhoto = {},
            onSelectFromGallery = {},
            onAnalyze = {},
            onClearError = {},
            onReset = {}
        )
    }
}

@Composable
private fun EmotionDetectionScreenContent(
    state: EmotionDetectionState,
    onNavigateBack: () -> Unit,
    onTakePhoto: () -> Unit,
    onSelectFromGallery: () -> Unit,
    onAnalyze: (Boolean) -> Unit,
    onClearError: () -> Unit,
    onReset: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // GIF del panda como fondo cuando no hay análisis
        if (state.emotionAnalysis == null && !state.isLoading) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = R.drawable.soft_panda
                ),
                contentDescription = "Panda animado",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.3f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Spacer para posicionar los botones más arriba
            if (state.emotionAnalysis == null && !state.isLoading) {
                Spacer(modifier = Modifier.weight(0.6f))
            }

            // Botones de Cámara y Galería
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTakePhoto,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Green49),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cámara", style = SourceSansBold, fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = onSelectFromGallery,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = White,
                        contentColor = Green49
                    )
                ) {
                    Icon(Icons.Default.Image, null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galería", style = SourceSansBold, fontSize = 16.sp)
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(color = Green49)
                Text("Analizando emoción...", style = SourceSansRegular, color = Gray828)
            }

            state.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = com.softfocus.ui.theme.RedE8.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Error",
                            style = CrimsonSemiBold,
                            fontSize = 18.sp,
                            color = com.softfocus.ui.theme.RedE8
                        )
                        Text(
                            text = error,
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Gray828,
                            modifier = Modifier.fillMaxWidth(),
                            softWrap = true
                        )
                        Button(
                            onClick = onClearError,
                            colors = ButtonDefaults.buttonColors(containerColor = com.softfocus.ui.theme.RedE8),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cerrar", style = SourceSansBold, fontSize = 14.sp)
                        }
                    }
                }
            }

            state.emotionAnalysis?.let { analysis ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GreenEC),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Resultado del Análisis",
                            style = CrimsonSemiBold,
                            fontSize = 20.sp,
                            color = Green37
                        )
                        HorizontalDivider(color = Green49.copy(alpha = 0.3f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = getEmotionEmojiResource(analysis.emotion)),
                                    contentDescription = "Emoji de emoción",
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = translateEmotion(analysis.emotion),
                                    style = CrimsonSemiBold,
                                    fontSize = 22.sp,
                                    color = Green49
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Confianza:",
                                style = SourceSansRegular,
                                fontSize = 16.sp,
                                color = Gray828
                            )
                            Text(
                                "${(analysis.confidence * 100).toInt()}%",
                                style = CrimsonSemiBold,
                                fontSize = 22.sp,
                                color = Green37
                            )
                        }

                        if (analysis.checkInCreated) {
                            HorizontalDivider(color = Green49.copy(alpha = 0.3f))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Check",
                                    tint = Green37,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Nuevo registro automático en tu calendario emocional",
                                    style = SourceSansRegular,
                                    fontSize = 14.sp,
                                    color = Green37,
                                    softWrap = true
                                )
                            }
                        }

                        if (analysis.allEmotions.isNotEmpty()) {
                            HorizontalDivider(color = Green49.copy(alpha = 0.3f))
                            Text(
                                "Todas las Emociones:",
                                style = SourceSansBold,
                                fontSize = 16.sp,
                                color = Green37
                            )
                            analysis.allEmotions.forEach { (emotion, confidence) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = getEmotionEmojiResource(
                                                    emotion
                                                )
                                            ),
                                            contentDescription = "Emoji de emoción",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = translateEmotion(emotion),
                                            style = SourceSansRegular,
                                            fontSize = 14.sp,
                                            color = Gray828
                                        )
                                    }
                                    Text(
                                        "${(confidence * 100).toInt()}%",
                                        style = SourceSansBold,
                                        fontSize = 14.sp,
                                        color = Green49
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = onReset,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Green49),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Analizar Otra Foto", style = SourceSansBold, fontSize = 16.sp)
                        }
                    }
                }

                // Spacer para centrar los botones verticalmente (parte inferior)
                if (state.emotionAnalysis == null && !state.isLoading) {
                    Spacer(modifier = Modifier.weight(1.4f))
                }
            }
        }
    }
}
