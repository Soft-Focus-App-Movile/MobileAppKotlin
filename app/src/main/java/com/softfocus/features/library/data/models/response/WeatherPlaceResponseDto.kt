package com.softfocus.features.library.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.library.domain.models.WeatherCondition

/**
 * DTO para respuesta de recomendación de lugares con información del clima
 */
data class WeatherPlaceResponseDto(
    @SerializedName("weather")
    val weather: WeatherDto,

    @SerializedName("places")
    val places: List<ContentItemResponseDto>,

    @SerializedName("totalPlaces")
    val totalPlaces: Int? = null,

    @SerializedName("location")
    val location: LocationDto? = null,

    @SerializedName("emotionFilter")
    val emotionFilter: String? = null
) {
    /**
     * DTO para información de ubicación
     */
    data class LocationDto(
        @SerializedName("latitude")
        val latitude: Double,

        @SerializedName("longitude")
        val longitude: Double
    )
    /**
     * DTO interno para información del clima
     */
    data class WeatherDto(
        @SerializedName("condition")
        val condition: String,

        @SerializedName("description")
        val description: String,

        @SerializedName("temperature")
        val temperature: Double,

        @SerializedName("humidity")
        val humidity: Int,

        @SerializedName("cityName")
        val cityName: String
    ) {
        /**
         * Mapea el DTO a la entidad de dominio
         */
        fun toDomain(): WeatherCondition {
            return WeatherCondition(
                condition = condition,
                description = description,
                temperature = temperature,
                humidity = humidity,
                cityName = cityName
            )
        }
    }
}

/**
 * DTO para respuesta simple de lista de contenido (usado en recomendaciones)
 */
data class ContentListResponseDto(
    @SerializedName("content")
    val content: List<ContentItemResponseDto>
)

/**
 * DTO para respuesta de asignación exitosa (devuelve IDs)
 */
data class AssignmentCreatedResponseDto(
    @SerializedName("assignmentIds")
    val assignmentIds: List<String>,

    @SerializedName("message")
    val message: String? = null
)

/**
 * DTO para respuesta de completar asignación
 */
data class AssignmentCompletedResponseDto(
    @SerializedName("assignmentId")
    val assignmentId: String,

    @SerializedName("completedAt")
    val completedAt: String,

    @SerializedName("message")
    val message: String? = null
)
