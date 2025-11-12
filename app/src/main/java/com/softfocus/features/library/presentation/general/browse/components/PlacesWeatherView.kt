package com.softfocus.features.library.presentation.general.browse.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.softfocus.R
import com.softfocus.features.library.domain.models.RecommendationCategory
import com.softfocus.features.library.domain.models.WeatherCondition
import com.softfocus.features.library.domain.models.WeatherRecommendation
import com.softfocus.ui.theme.*
import java.util.Calendar
import kotlin.random.Random

/**
 * Vista de clima con zorrito mascota
 *
 * @param weather Condiciones climáticas actuales
 * @param modifier Modificador opcional
 */
@Composable
fun PlacesWeatherView(
    weather: WeatherCondition,
    modifier: Modifier = Modifier
) {
    // Obtener la recomendación con su categoría
    val recommendation = remember(weather) { weather.getRecommendationWithCategory() }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Fila horizontal centrada: Zorrito + Clima
            Row(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .offset(x = (-20).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Zorrito a la izquierda
                Image(
                    painter = painterResource(id = R.drawable.fox_image),
                    contentDescription = "Mascota",
                    modifier = Modifier.size(152.dp)
                )

                // Información del clima a la derecha
                WeatherHeader(weather = weather)
            }

            // Recomendación basada en el clima con fondo oscuro semi-transparente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = recommendation.text,
                    style = SourceSansRegular.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                )
            }

            // GIF o Imagen decorativa según la categoría de la recomendación
            WeatherVisualContent(recommendation = recommendation)
        }

        // Nutria en el borde de la pantalla solo cuando la categoría es OTTER
        if (recommendation.category == RecommendationCategory.OTTER) {
            OtterImageView()
        }

        // Burbujas flotantes con palabras positivas
        FloatingBubblesOverlay(weather = weather)
    }
}

/**
 * Header con información del clima
 */
@Composable
private fun WeatherHeader(
    weather: WeatherCondition,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Nombre de la ciudad
        Text(
            text = weather.cityName,
            style = SourceSansSemiBold.copy(fontSize = 34.sp),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        // Temperatura principal
        Text(
            text = "${weather.temperature.toInt()}°",
            style = CrimsonBold.copy(fontSize = 66.sp),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        // Descripción del clima
        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            style = SourceSansRegular.copy(fontSize = 18.sp),
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        // Temperaturas alta y baja (aproximadas)
        // Como el backend solo retorna temperatura actual, mostramos estimaciones
        val highTemp = (weather.temperature + 5).toInt()
        val lowTemp = (weather.temperature - 5).toInt()

        Text(
            text = "H:${highTemp}° L:${lowTemp}°",
            style = SourceSansRegular.copy(fontSize = 15.sp),
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Componente que muestra el contenido visual (GIF o imagen)
 * según la categoría de la recomendación
 */
@Composable
private fun WeatherVisualContent(recommendation: WeatherRecommendation) {
    when (recommendation.category) {
        // GIFs
        RecommendationCategory.MUSIC -> {
            Image(
                painter = rememberAsyncImagePainter(model = R.drawable.music),
                contentDescription = "Música",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        RecommendationCategory.CLOTHES -> {
            Image(
                painter = rememberAsyncImagePainter(model = R.drawable.ropa),
                contentDescription = "Ropa",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(200.dp)
                    .clip(RoundedCornerShape(22.dp)),
                contentScale = ContentScale.Crop
            )
        }

        RecommendationCategory.FOOD -> {
            Image(
                painter = rememberAsyncImagePainter(model = R.drawable.comida),
                contentDescription = "Comida",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        RecommendationCategory.EXERCISE -> {
            Image(
                painter = rememberAsyncImagePainter(model = R.drawable.ejercicio),
                contentDescription = "Ejercicio",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Imágenes PNG
        RecommendationCategory.SLEEP -> {
            Image(
                painter = painterResource(id = R.drawable.dormir1),
                contentDescription = "Dormir",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )
        }

        RecommendationCategory.COLD -> {
            Image(
                painter = painterResource(id = R.drawable.frio1),
                contentDescription = "Frío",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )
        }

        RecommendationCategory.PLAY -> {
            Image(
                painter = painterResource(id = R.drawable.jugar1),
                contentDescription = "Jugar",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )
        }

        // OTTER no muestra imagen aquí (se muestra en el borde)
        RecommendationCategory.OTTER -> {
            // No mostrar nada aquí, la nutria aparece en el borde
        }
    }
}

/**
 * Componente que muestra la nutria en el borde de la pantalla
 */
@Composable
private fun OtterImageView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painterResource(id = R.drawable.nutria1),
            contentDescription = "Nutria",
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-8).dp, y = 0.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomEnd
        )
    }
}

/**
 * Mensajes motivacionales directos
 */
private val motivationalMessages = listOf(
    "Tú puedes",
    "Eres el mejor",
    "Lo lograrás",
    "Eres increíble",
    "Vas a brillar",
    "Eres capaz",
    "Sigue así",
    "Lo harás genial",
    "Eres fuerte",
    "Hoy brillarás"
)

/**
 * Mensajes de ánimo y bienestar
 */
private val wellnessMessages = listOf(
    "No te estreses",
    "Cuídate hoy",
    "Respira hondo",
    "Mereces descansar",
    "Todo estará bien",
    "Eres especial",
    "Date un respiro",
    "Relájate",
    "Cree en ti",
    "Eres amado"
)

/**
 * Obtiene un mensaje contextual según el clima y la hora
 */
private fun getContextualMessage(weather: WeatherCondition): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    return when {
        // Noche (8 PM - 6 AM)
        hour >= 20 || hour < 6 -> {
            listOf(
                "Dulces sueños",
                "Buenas noches",
                "Descansa bien",
                "Que duermas bien"
            ).random()
        }

        // Mañana (6 AM - 12 PM)
        hour in 6..11 -> {
            listOf(
                "Buenos días",
                "Buen día",
                "Hoy te ves mejor",
                "Comienza bien"
            ).random()
        }

        // Tarde (12 PM - 8 PM) - considerar clima
        else -> {
            when {
                // Hace frío
                weather.temperature <= 17.0 -> {
                    listOf(
                        "Abrígate bien",
                        "Ponte abrigo",
                        "Caliéntate",
                        "Cuida tu salud"
                    ).random()
                }

                // Lluvia
                weather.condition.lowercase() in listOf("rain", "thunderstorm", "drizzle") -> {
                    listOf(
                        "Descansa hoy",
                        "Relájate",
                        "Quédate tranquilo",
                        "Disfruta en casa"
                    ).random()
                }

                // Clima cálido/soleado
                weather.temperature > 25.0 -> {
                    listOf(
                        "Hidrátate bien",
                        "Toma agua",
                        "Cuídate del sol",
                        "Refréscate"
                    ).random()
                }

                // Clima agradable
                else -> {
                    listOf(
                        "Disfruta el día",
                        "Sal a pasear",
                        "Aprovecha el día",
                        "Buenas tardes",
                        "Come bien"
                    ).random()
                }
            }
        }
    }
}

/**
 * Overlay con múltiples burbujas flotantes
 * Cada burbuja tiene un propósito: motivación, contextual, ánimo
 * Aparecen 6 burbujas en total (2 sets de 3)
 */
@Composable
private fun FloatingBubblesOverlay(weather: WeatherCondition) {
    // Generar todas las 6 burbujas de una vez para que fluyan continuamente
    val allBubbles = remember {
        // Primer set de 3 burbujas
        val firstSet = listOf(
            BubbleConfig(
                word = motivationalMessages.random(),
                startDelay = 0,
                xPosition = Random.nextFloat() * 0.6f + 0.15f,
                startYOffset = Random.nextInt(200, 500),
                duration = 12000 // 12 segundos (más lento)
            ),
            BubbleConfig(
                word = getContextualMessage(weather),
                startDelay = 1000,
                xPosition = Random.nextFloat() * 0.6f + 0.15f,
                startYOffset = Random.nextInt(200, 500),
                duration = 12000
            ),
            BubbleConfig(
                word = wellnessMessages.random(),
                startDelay = 2000,
                xPosition = Random.nextFloat() * 0.6f + 0.15f,
                startYOffset = Random.nextInt(200, 500),
                duration = 12000
            )
        )

        // Segundo set de 3 burbujas (comienzan 6 segundos después del primer set)
        val secondSet = listOf(
            BubbleConfig(
                word = motivationalMessages.random(),
                startDelay = 6000,
                xPosition = Random.nextFloat() * 0.6f + 0.15f,
                startYOffset = Random.nextInt(200, 500),
                duration = 12000
            ),
            BubbleConfig(
                word = getContextualMessage(weather),
                startDelay = 7000,
                xPosition = Random.nextFloat() * 0.6f + 0.15f,
                startYOffset = Random.nextInt(200, 500),
                duration = 12000
            ),
            BubbleConfig(
                word = wellnessMessages.random(),
                startDelay = 8000,
                xPosition = Random.nextFloat() * 0.6f + 0.15f,
                startYOffset = Random.nextInt(200, 500),
                duration = 12000
            )
        )

        firstSet + secondSet
    }

    Box(modifier = Modifier.fillMaxSize()) {
        allBubbles.forEach { config ->
            FloatingBubble(config = config)
        }
    }
}

/**
 * Configuración para una burbuja individual
 */
private data class BubbleConfig(
    val word: String,
    val startDelay: Int,
    val xPosition: Float,
    val startYOffset: Int,
    val duration: Int,
    val onComplete: () -> Unit = {}
)

/**
 * Burbuja individual con animación flotante
 * Solo se anima una vez (no infinito)
 */
@Composable
private fun FloatingBubble(config: BubbleConfig) {
    // Estado para controlar cuándo iniciar la animación
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(config.startDelay.toLong())
        startAnimation = true
    }

    // Animación de float hacia arriba (solo una vez)
    val offsetY by animateFloatAsState(
        targetValue = if (startAnimation) -900f else config.startYOffset.toFloat(),
        animationSpec = tween(
            durationMillis = config.duration,
            easing = FastOutSlowInEasing // Easing más fluido
        ),
        label = "offsetY_${config.word}",
        finishedListener = { config.onComplete() }
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 1f,
        animationSpec = keyframes {
            durationMillis = config.duration
            if (startAnimation) {
                1f at 0 using LinearEasing
                0.8f at (config.duration * 0.15f).toInt() using LinearEasing
                0.8f at (config.duration * 0.7f).toInt() using LinearEasing
                0f at config.duration using FastOutSlowInEasing
            } else {
                0f at 0 using LinearEasing
                0.7f at 300 using FastOutSlowInEasing
            }
        },
        label = "alpha_${config.word}"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = (config.xPosition * 300).dp,
                    y = offsetY.dp
                )
                .alpha(alpha)
                .size(100.dp), // Agrandado de 80dp a 100dp
            contentAlignment = Alignment.Center
        ) {
            // Imagen de burbuja
            Image(
                painter = painterResource(id = R.drawable.burbuja1),
                contentDescription = "Burbuja",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // Texto dentro de la burbuja
            Text(
                text = config.word,
                style = SourceSansSemiBold.copy(
                    fontSize = 10.sp, // Reducido de 12sp a 10sp
                    lineHeight = 12.sp // Espacio entre líneas
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp) // Más padding interno
            )
        }
    }
}
