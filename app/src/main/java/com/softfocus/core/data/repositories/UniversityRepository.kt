package com.softfocus.core.data.repositories

import com.softfocus.core.data.remote.UniversityApiService
import com.softfocus.core.data.remote.UniversityDto

class UniversityRepository(private val apiService: UniversityApiService) {

    suspend fun searchUniversities(query: String): Result<List<UniversityInfo>> {
        return try {
            if (query.length < 2) {
                return Result.success(emptyList())
            }

            val universities = apiService.searchUniversities(name = query)
            val mapped = universities.map { dto ->
                UniversityInfo(
                    name = dto.name,
                    region = dto.state_province ?: extractRegionFromName(dto.name)
                )
            }
            Result.success(mapped)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractRegionFromName(name: String): String {
        val regions = mapOf(
            "Lima" to listOf("Lima", "San Marcos", "Católica", "Pacífico", "Cayetano"),
            "Arequipa" to listOf("Arequipa", "Santa María", "San Agustín de Arequipa"),
            "Cusco" to listOf("Cusco", "Andina del Cusco"),
            "Trujillo" to listOf("Trujillo", "Privada Antenor Orrego", "César Vallejo"),
            "Piura" to listOf("Piura"),
            "Ica" to listOf("Ica", "San Luis Gonzaga"),
            "Huancayo" to listOf("Centro del Perú", "Continental"),
            "Chiclayo" to listOf("Chiclayo", "Pedro Ruiz Gallo", "Señor de Sipán"),
            "Tacna" to listOf("Tacna"),
            "Puno" to listOf("Puno", "Altiplano"),
            "Lambayeque" to listOf("Lambayeque"),
            "Cajamarca" to listOf("Cajamarca"),
            "Ayacucho" to listOf("Ayacucho", "San Cristóbal de Huamanga"),
            "Huánuco" to listOf("Huánuco", "Hermilio Valdizán"),
            "Junín" to listOf("Junín"),
            "Loreto" to listOf("Loreto", "Amazonía Peruana"),
            "Ucayali" to listOf("Ucayali"),
            "San Martín" to listOf("San Martín"),
            "Ancash" to listOf("Ancash", "Santiago Antúnez de Mayolo"),
            "Apurímac" to listOf("Apurímac", "Micaela Bastidas"),
            "Huancavelica" to listOf("Huancavelica"),
            "Madre de Dios" to listOf("Madre de Dios"),
            "Moquegua" to listOf("Moquegua"),
            "Pasco" to listOf("Pasco", "Daniel Alcides Carrión"),
            "Tumbes" to listOf("Tumbes"),
            "Amazonas" to listOf("Amazonas")
        )

        for ((region, keywords) in regions) {
            if (keywords.any { keyword -> name.contains(keyword, ignoreCase = true) }) {
                return region
            }
        }

        return "Lima"
    }
}

data class UniversityInfo(
    val name: String,
    val region: String
)
