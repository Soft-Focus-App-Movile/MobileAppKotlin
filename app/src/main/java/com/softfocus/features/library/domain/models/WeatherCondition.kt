package com.softfocus.features.library.domain.models

/**
 * Categorías de recomendaciones que se emparejan con GIFs/imágenes
 */
enum class RecommendationCategory {
    SLEEP,      // dormir.png
    COLD,       // frio.png
    PLAY,       // jugar.png
    OTTER,      // nutria.png
    MUSIC,      // music.gif
    CLOTHES,    // ropa.gif
    FOOD,       // comida.gif
    EXERCISE    // ejercicio.gif
}

/**
 * Representa una recomendación con su categoría asociada
 */
data class WeatherRecommendation(
    val text: String,
    val category: RecommendationCategory
)

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
     * Obtiene una recomendación basada en el clima y la hora del día
     * con su categoría asociada para mostrar el GIF/imagen correcto
     */
    fun getRecommendationWithCategory(): WeatherRecommendation {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val isNight = hour >= 20 || hour < 6

        // Lista de todas las posibles recomendaciones según el contexto
        val possibleRecommendations = mutableListOf<WeatherRecommendation>()

        when {
            // Es de noche (8 PM - 6 AM) - priorizar descanso
            isNight -> {
                possibleRecommendations.add(
                    WeatherRecommendation("Es hora de descansar, que tengas dulces sueños", RecommendationCategory.SLEEP)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Noche perfecta para dormir y recargar energías", RecommendationCategory.SLEEP)
                )

                // Si hace frío de noche, agregar opciones de frío
                if (temperature <= 17.0) {
                    possibleRecommendations.add(
                        WeatherRecommendation("Hace frío, abrígate bien para dormir calentito", RecommendationCategory.COLD)
                    )
                    possibleRecommendations.add(
                        WeatherRecommendation("Noche fría, ponte ropa abrigadora", RecommendationCategory.CLOTHES)
                    )
                }

                // Antes de medianoche (8 PM - 12 AM) - opciones para cenar y prepararse
                if (hour < 24) {
                    possibleRecommendations.add(
                        WeatherRecommendation("No olvides cenar algo delicioso antes de dormir", RecommendationCategory.FOOD)
                    )
                    possibleRecommendations.add(
                        WeatherRecommendation("Buen momento para preparar algo rico para cenar", RecommendationCategory.FOOD)
                    )
                }

                // Opciones para relajarse de noche
                possibleRecommendations.add(
                    WeatherRecommendation("Escucha música relajante antes de dormir", RecommendationCategory.MUSIC)
                )

                // Opciones variadas de noche
                possibleRecommendations.add(
                    WeatherRecommendation("Ponte ropa cómoda para descansar mejor", RecommendationCategory.CLOTHES)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Noche tranquila, disfruta de la calma", RecommendationCategory.OTTER)
                )
            }

            // Clima muy frío (menos de 10°C)
            temperature < 10 -> {
                possibleRecommendations.add(
                    WeatherRecommendation("Hace mucho frío, abrígate muy bien si sales", RecommendationCategory.COLD)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Temperatura muy baja, ponte ropa abrigadora", RecommendationCategory.CLOTHES)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Mucho frío, buen momento para comida caliente", RecommendationCategory.FOOD)
                )
            }

            // Clima frío (10-17°C)
            temperature in 10.0..17.0 -> {
                possibleRecommendations.add(
                    WeatherRecommendation("Hace frío, lleva una chaqueta si sales", RecommendationCategory.COLD)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Clima fresco, abrígate bien para el día", RecommendationCategory.CLOTHES)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Temperatura fresca, perfecto para comida caliente", RecommendationCategory.FOOD)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Clima fresco pero bueno para jugar abrigado", RecommendationCategory.PLAY)
                )
            }

            // Lluvia o tormenta
            condition.lowercase() in listOf("rain", "thunderstorm", "drizzle") -> {
                possibleRecommendations.add(
                    WeatherRecommendation("Está lloviendo, escucha música mientras descansas", RecommendationCategory.MUSIC)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Lluvia afuera, perfecto para preparar algo rico", RecommendationCategory.FOOD)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Día lluvioso, ponte ropa cómoda y relájate", RecommendationCategory.CLOTHES)
                )
            }

            // Clima soleado/despejado
            condition.lowercase() == "clear" -> {
                possibleRecommendations.add(
                    WeatherRecommendation("¡Día soleado! Perfecto para salir a jugar", RecommendationCategory.PLAY)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Hermoso día, sal a hacer ejercicio", RecommendationCategory.EXERCISE)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Día radiante, disfruta la naturaleza", RecommendationCategory.OTTER)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Clima perfecto, ponte ropa cómoda y sal", RecommendationCategory.CLOTHES)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Buen clima para un picnic o comer afuera", RecommendationCategory.FOOD)
                )
            }

            // Clima nublado
            condition.lowercase() == "clouds" -> {
                possibleRecommendations.add(
                    WeatherRecommendation("Día nublado, buen momento para caminar", RecommendationCategory.PLAY)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Cielo nublado, ideal para hacer ejercicio sin mucho sol", RecommendationCategory.EXERCISE)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Clima nublado, disfruta escuchando música", RecommendationCategory.MUSIC)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Día nublado, prepara algo delicioso", RecommendationCategory.FOOD)
                )
            }

            // Clima templado agradable (por defecto)
            else -> {
                possibleRecommendations.add(
                    WeatherRecommendation("Buen clima para salir a jugar", RecommendationCategory.PLAY)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Clima agradable para hacer ejercicio", RecommendationCategory.EXERCISE)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Temperatura perfecta para disfrutar la naturaleza", RecommendationCategory.OTTER)
                )
                possibleRecommendations.add(
                    WeatherRecommendation("Buen día para escuchar música al aire libre", RecommendationCategory.MUSIC)
                )
            }
        }

        // Retornar una recomendación aleatoria de las disponibles
        return possibleRecommendations.random()
    }

    /**
     * Obtiene solo el texto de la recomendación (para compatibilidad)
     */
    fun getRecommendation(): String {
        return getRecommendationWithCategory().text
    }
}
