package com.softfocus.features.library.domain.models

/**
 * Etiquetas emocionales para clasificar contenido
 *
 * @property Happy Contenido alegre, optimista, animado
 * @property Sad Contenido melancólico, nostálgico, emotivo
 * @property Anxious Contenido para aliviar ansiedad y estrés
 * @property Calm Contenido tranquilo, pacífico, relajante
 * @property Energetic Contenido energético, motivador, activo
 */
enum class EmotionalTag(val displayName: String) {
    Happy("Alegre"),
    Sad("Triste"),
    Anxious("Ansioso"),
    Calm("Tranquilo"),
    Energetic("Energético");

    companion object {
        /**
         * Convierte un string del backend al enum correspondiente
         * @param value String value from backend
         * @return EmotionalTag enum value
         * @throws IllegalArgumentException if value is not valid
         */
        fun fromString(value: String): EmotionalTag {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid EmotionalTag: $value")
            }
        }

        /**
         * Obtiene todas las etiquetas como lista
         */
        fun getAll(): List<EmotionalTag> = values().toList()
    }
}
