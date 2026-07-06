package com.softfocus.features.tracking.domain.model

// ============= CHECK-INS =============

data class CheckIn(
    val id: String,
    val userId: String,
    val emotionalLevel: Int,
    val energyLevel: Int,
    val moodDescription: String,
    val sleepHours: Int,
    val symptoms: List<String>,
    val notes: String?,
    val completedAt: String,
    val createdAt: String,
    val updatedAt: String
)

data class CheckInHistory(
    val checkIns: List<CheckIn>,
    val pagination: Pagination
)

data class TodayCheckIn(
    val checkIn: CheckIn?,
    val hasCompletedToday: Boolean
)

data class Pagination(
    val currentPage: Int,
    val pageSize: Int,
    val totalCount: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
)

// ============= EMOTIONAL CALENDAR =============

data class EmotionalCalendarEntry(
    val id: String,
    val userId: String,
    val timestamp: String,  // NUEVO: ISO-8601 con hora/minuto (reemplaza a date como fuente principal)
    val date: String,  // Se mantiene por compatibilidad con vistas de calendario existentes
    val emotionalEmoji: String,
    val moodLevel: Int,
    val emotionalTags: List<String>,
    val content: String = "",  // NUEVO
    val sessionDurationSeconds: Int = 0,  // NUEVO
    val entryType: String = "spontaneous",  // NUEVO: "scheduled" | "spontaneous"
    val createdAt: String,
    val updatedAt: String
)

data class EmotionalCalendar(
    val entries: List<EmotionalCalendarEntry>,
    val totalCount: Int,
    val dateRange: DateRange
)

data class DeleteTodayEmotionalEntriesResult(
    val deletedCount: Int,
    val failedCount: Int,
    val totalMatched: Int,
    val entryType: String
)

data class DateRange(
    val startDate: String?,
    val endDate: String?
)

// ============= DASHBOARD =============

data class DashboardSummary(
    val hasTodayCheckIn: Boolean,
    val todayCheckIn: CheckIn?,
    val totalCheckIns: Int,
    val totalEmotionalCalendarEntries: Int,
    val averageEmotionalLevel: Double,
    val averageEnergyLevel: Double,
    val averageMoodLevel: Double,
    val mostCommonSymptoms: List<String>,
    val mostUsedEmotionalTags: List<String>
)

data class DashboardInsights(
    val messages: List<String>
)

data class TrackingDashboard(
    val summary: DashboardSummary,
    val insights: DashboardInsights
)
