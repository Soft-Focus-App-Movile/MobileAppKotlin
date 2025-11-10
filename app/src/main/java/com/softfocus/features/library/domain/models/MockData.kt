package com.softfocus.features.library.domain.models

/**
 * Mock data para desarrollo y testing
 * No usar API, solo datos estáticos
 */
object MockLibraryData {

    /**
     * Contenido asignado por el terapeuta (para vista del paciente)
     * Incluye películas, música y videos mezclados
     */
    val assignedContent = listOf(
        // Película asignada
        ContentItem(
            id = "assigned-movie-1",
            externalId = "tmdb-movie-550",
            type = ContentType.Movie,
            title = "Está bien no estar bien",
            overview = "Drama sobre aceptación personal y salud mental que explora el camino hacia la autoaceptación.",
            posterUrl = "https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w500/rr7E0NoGKxvbkb89eR1GwfoYjpA.jpg",
            rating = 8.4,
            duration = 168,
            releaseDate = "2020-06-20",
            genres = listOf("Drama", "Romance"),
            emotionalTags = listOf(EmotionalTag.Calm, EmotionalTag.Happy),
            externalUrl = "https://www.themoviedb.org/movie/550"
        ),

        // Música asignada
        ContentItem(
            id = "assigned-music-1",
            externalId = "spotify-track-001",
            type = ContentType.Music,
            title = "Música clásica relajante",
            overview = "Pieza de música clásica perfecta para momentos de relajación y meditación.",
            posterUrl = "https://i.scdn.co/image/ab67616d0000b27355a4b0d62d7fd7c13f0e9d6e",
            rating = 9.0,
            duration = 180,
            releaseDate = "2020-03-15",
            genres = listOf("Clásica", "Instrumental"),
            emotionalTags = listOf(EmotionalTag.Calm),
            artist = "Ludwig van Beethoven",
            album = "Sinfonía No. 9",
            previewUrl = "https://p.scdn.co/mp3-preview/...",
            spotifyUrl = "https://open.spotify.com/track/..."
        ),

        // Video asignado
        ContentItem(
            id = "assigned-video-1",
            externalId = "youtube-video-001",
            type = ContentType.Video,
            title = "Meditación Guiada para CALMAR LA MENTE en 10 min",
            overview = "Sesión de meditación guiada para reducir el estrés y encontrar paz interior.",
            thumbnailUrl = "https://i.ytimg.com/vi/example1/maxresdefault.jpg",
            rating = 8.8,
            duration = 10,
            releaseDate = "2023-05-10",
            genres = listOf("Meditación", "Bienestar"),
            emotionalTags = listOf(EmotionalTag.Calm, EmotionalTag.Anxious),
            channelName = "Mindfulness Academy",
            youtubeUrl = "https://www.youtube.com/watch?v=example1"
        ),

        // Segunda película asignada
        ContentItem(
            id = "assigned-movie-2",
            externalId = "tmdb-movie-680",
            type = ContentType.Movie,
            title = "Forrest Gump",
            overview = "Historia inspiradora sobre perseverancia, amor y ver la vida desde una perspectiva única.",
            posterUrl = "https://image.tmdb.org/t/p/w500/arw2vcBveWOVZr6pxd9XTd1TdQa.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w500/3h1JZGDhZ8nzxdgvkxha0qBqi05.jpg",
            rating = 8.8,
            duration = 142,
            releaseDate = "1994-07-06",
            genres = listOf("Drama", "Romance"),
            emotionalTags = listOf(EmotionalTag.Happy, EmotionalTag.Calm),
            externalUrl = "https://www.themoviedb.org/movie/13"
        )
    )

    /**
     * Mock data de películas adicionales para selección del psicólogo
     */
    val mockMovies = listOf(
        ContentItem(
            id = "movie-1",
            externalId = "tmdb-movie-278",
            type = ContentType.Movie,
            title = "El viaje de Chihiro",
            overview = "Aventura fantástica sobre valentía y crecimiento personal de una niña en un mundo mágico.",
            posterUrl = "https://image.tmdb.org/t/p/w500/39wmItIWsg5sZMyRUHLkWBcuVCM.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w500/Ab8mkHmkYADjU7wQiOkia9BzGvS.jpg",
            rating = 8.6,
            duration = 125,
            releaseDate = "2001-07-20",
            genres = listOf("Animación", "Fantasía"),
            emotionalTags = listOf(EmotionalTag.Happy, EmotionalTag.Calm),
            externalUrl = "https://www.themoviedb.org/movie/129"
        ),
        ContentItem(
            id = "movie-2",
            externalId = "tmdb-movie-122",
            type = ContentType.Movie,
            title = "La teoría del todo",
            overview = "Biografía inspiradora de Stephen Hawking sobre superar adversidades.",
            posterUrl = "https://image.tmdb.org/t/p/w500/kq2MHrRfH6RTfkvyDEmYLmGHE3i.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w500/5dMV1w9T6Pv4CdIxQHTGBX5U8KD.jpg",
            rating = 7.9,
            duration = 123,
            releaseDate = "2014-11-26",
            genres = listOf("Drama", "Romance", "Biográfica"),
            emotionalTags = listOf(EmotionalTag.Calm, EmotionalTag.Happy),
            externalUrl = "https://www.themoviedb.org/movie/266856"
        )
    )

    /**
     * Mock data de música adicional
     */
    val mockMusic = listOf(
        ContentItem(
            id = "music-1",
            externalId = "spotify-track-002",
            type = ContentType.Music,
            title = "Weightless",
            overview = "Canción diseñada científicamente para reducir la ansiedad.",
            posterUrl = "https://i.scdn.co/image/ab67616d0000b273f3b6d7b9c1e0e3e5d6f7a8b9",
            rating = 9.2,
            duration = 8,
            releaseDate = "2011-10-17",
            genres = listOf("Ambient", "Relajación"),
            emotionalTags = listOf(EmotionalTag.Calm, EmotionalTag.Anxious),
            artist = "Marconi Union",
            album = "Weightless",
            previewUrl = "https://p.scdn.co/mp3-preview/...",
            spotifyUrl = "https://open.spotify.com/track/..."
        ),
        ContentItem(
            id = "music-2",
            externalId = "spotify-track-003",
            type = ContentType.Music,
            title = "Here Comes the Sun",
            overview = "Canción alegre y optimista de The Beatles.",
            posterUrl = "https://i.scdn.co/image/ab67616d0000b2734ce8b4e1e1e1e2e3e4e5e6e7",
            rating = 9.5,
            duration = 3,
            releaseDate = "1969-09-26",
            genres = listOf("Rock", "Pop"),
            emotionalTags = listOf(EmotionalTag.Happy, EmotionalTag.Energetic),
            artist = "The Beatles",
            album = "Abbey Road",
            previewUrl = "https://p.scdn.co/mp3-preview/...",
            spotifyUrl = "https://open.spotify.com/track/..."
        )
    )

    /**
     * Mock data de videos adicionales
     */
    val mockVideos = listOf(
        ContentItem(
            id = "video-1",
            externalId = "youtube-video-002",
            type = ContentType.Video,
            title = "Respiración 4-7-8 para Dormir Mejor",
            overview = "Técnica de respiración para reducir ansiedad y mejorar el sueño.",
            thumbnailUrl = "https://i.ytimg.com/vi/example2/maxresdefault.jpg",
            rating = 9.0,
            duration = 15,
            releaseDate = "2023-08-20",
            genres = listOf("Meditación", "Respiración"),
            emotionalTags = listOf(EmotionalTag.Calm, EmotionalTag.Anxious),
            channelName = "Breathe with Me",
            youtubeUrl = "https://www.youtube.com/watch?v=example2"
        ),
        ContentItem(
            id = "video-2",
            externalId = "youtube-video-003",
            type = ContentType.Video,
            title = "Yoga Energizante Matutino - 20 min",
            overview = "Rutina de yoga para comenzar el día con energía positiva.",
            thumbnailUrl = "https://i.ytimg.com/vi/example3/maxresdefault.jpg",
            rating = 8.7,
            duration = 20,
            releaseDate = "2023-06-15",
            genres = listOf("Yoga", "Bienestar"),
            emotionalTags = listOf(EmotionalTag.Energetic, EmotionalTag.Happy),
            channelName = "Yoga Daily",
            youtubeUrl = "https://www.youtube.com/watch?v=example3"
        )
    )

    /**
     * Combina todo el contenido disponible (sin lugares para psicólogo)
     */
    val allContent: Map<ContentType, List<ContentItem>> = mapOf(
        ContentType.Movie to mockMovies,
        ContentType.Music to mockMusic,
        ContentType.Video to mockVideos
    )
}

/**
 * Clase para representar a un paciente
 */
data class Patient(
    val id: String,
    val name: String,
    val photoUrl: String? = null,
    val email: String
)

/**
 * Mock data de pacientes para el psicólogo
 */
object MockPatientsData {
    val patients = listOf(
        Patient(
            id = "patient-1",
            name = "Ana García",
            photoUrl = "https://i.pravatar.cc/150?img=1",
            email = "ana.garcia@email.com"
        ),
        Patient(
            id = "patient-2",
            name = "Luis Torres",
            photoUrl = "https://i.pravatar.cc/150?img=2",
            email = "luis.torres@email.com"
        ),
        Patient(
            id = "patient-3",
            name = "María Lopes",
            photoUrl = "https://i.pravatar.cc/150?img=3",
            email = "maria.lopes@email.com"
        ),
        Patient(
            id = "patient-4",
            name = "Carlos Ruiz",
            photoUrl = "https://i.pravatar.cc/150?img=4",
            email = "carlos.ruiz@email.com"
        ),
        Patient(
            id = "patient-5",
            name = "Sofia Mendez",
            photoUrl = "https://i.pravatar.cc/150?img=5",
            email = "sofia.mendez@email.com"
        )
    )
}
