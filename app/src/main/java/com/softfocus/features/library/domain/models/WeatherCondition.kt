package com.softfocus.features.library.domain.models

/**
 * Entidad de dominio que representa las condiciones climáticas actuales
 *
 * @property condition Condición principal (Clear, Clouds, Rain, etc.)
 * @property description Descripción detallada del clima
 * @property temperature Temperatura en grados Celsius
 * @property humidity Humedad en porcentaje (0-100)
 * @property cityName Nombre de la ciudad
 */
data class WeatherCondition(
    val condition: String,
    val description: String,
    val temperature: Double,
    val humidity: Int,
    val cityName: String
) {
    /**
     * Verifica si el clima es favorable para actividades al aire libre
     */
    fun isOutdoorFriendly(): Boolean = when (condition.lowercase()) {
        "clear" -> true
        "clouds" -> temperature > 15.0
        "rain", "thunderstorm", "snow", "drizzle" -> false
        else -> temperature > 15.0
    }

    /**
     * Verifica si se recomiendan actividades en interiores
     */
    fun isIndoorRecommended(): Boolean = !isOutdoorFriendly()

    /**
     * Obtiene la temperatura formateada
     */
    fun getFormattedTemperature(): String = "${temperature.toInt()}°C"

    /**
     * Obtiene la humedad formateada
     */
    fun getFormattedHumidity(): String = "$humidity%"

    /**
     * Obtiene un emoji representativo del clima
     */
    fun getWeatherEmoji(): String = when (condition.lowercase()) {
        "clear" -> "☀️"
        "clouds" -> "☁️"
        "rain" -> "🌧️"
        "thunderstorm" -> "⛈️"
        "snow" -> "❄️"
        "drizzle" -> "🌦️"
        "mist", "fog" -> "🌫️"
        else -> "🌤️"
    }

    /**
     * Obtiene una recomendación basada en el clima
     */
    fun getRecommendation(): String = when {
        isOutdoorFriendly() -> "Buen clima para actividades al aire libre"
        temperature < 10 -> "Hace frío, mejor actividades en interiores"
        condition.lowercase() in listOf("rain", "thunderstorm") -> "Está lloviendo, recomendamos lugares cubiertos"
        else -> "Considera actividades en interiores"
    }
}
