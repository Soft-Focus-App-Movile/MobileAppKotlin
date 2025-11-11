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
    val date: String,
    val emotionalEmoji: String,
    val moodLevel: Int,
    val emotionalTags: List<String>,
    val createdAt: String,
    val updatedAt: String
)

data class EmotionalCalendar(
    val entries: List<EmotionalCalendarEntry>,
    val totalCount: Int,
    val dateRange: DateRange
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