package com.softfocus.features.library.presentation.general.browse.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.WeatherCondition
import com.softfocus.ui.theme.*

/**
 * Vista completa de lugares con información del clima
 *
 * @param weather Condiciones climáticas actuales
 * @param places Lista de lugares recomendados
 * @param onPlaceClick Callback al hacer clic en un lugar
 * @param modifier Modificador opcional
 */
@Composable
fun PlacesWeatherView(
    weather: WeatherCondition,
    places: List<ContentItem>,
    onPlaceClick: (ContentItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Header del clima
        item {
            WeatherHeader(weather = weather)
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Imagen del zorrito
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fox_image),
                    contentDescription = "Mascota",
                    modifier = Modifier
                        .size(120.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Título "Recomendaciones"
        item {
            Text(
                text = "Recomendaciones",
                style = CrimsonBold.copy(fontSize = 24.sp),
                color = Green49,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Lista de lugares
        items(places) { place ->
            PlaceCard(
                place = place,
                onClick = { onPlaceClick(place) }
            )
        }

        // Espaciado final
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
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
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Nombre de la ciudad
        Text(
            text = weather.cityName,
            style = SourceSansRegular.copy(fontSize = 18.sp),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Temperatura principal
        Text(
            text = "${weather.temperature.toInt()}°",
            style = CrimsonBold.copy(fontSize = 72.sp),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Descripción del clima
        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            style = SourceSansRegular.copy(fontSize = 16.sp),
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Temperaturas alta y baja (aproximadas)
        // Como el backend solo retorna temperatura actual, mostramos estimaciones
        val highTemp = (weather.temperature + 5).toInt()
        val lowTemp = (weather.temperature - 5).toInt()

        Text(
            text = "H:${highTemp}° L:${lowTemp}°",
            style = SourceSansRegular.copy(fontSize = 14.sp),
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
