package com.softfocus.features.tracking.data.model

import com.google.gson.annotations.SerializedName

// ============= CHECK-INS =============

data class CreateCheckInRequest(
    @SerializedName("emotionalLevel")
    val emotionalLevel: Int,
    @SerializedName("energyLevel")
    val energyLevel: Int,
    @SerializedName("moodDescription")
    val moodDescription: String,
    @SerializedName("sleepHours")
    val sleepHours: Int,
    @SerializedName("symptoms")
    val symptoms: List<String>,
    @SerializedName("notes")
    val notes: String?
)

data class CheckInResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("emotionalLevel")
    val emotionalLevel: Int,
    @SerializedName("energyLevel")
    val energyLevel: Int,
    @SerializedName("moodDescription")
    val moodDescription: String,
    @SerializedName("sleepHours")
    val sleepHours: Int,
    @SerializedName("symptoms")
    val symptoms: List<String>,
    @SerializedName("notes")
    val notes: String?,
    @SerializedName("completedAt")
    val completedAt: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

data class CreateCheckInApiResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: CheckInResponse,
    @SerializedName("timestamp")
    val timestamp: String
)

data class CheckInsHistoryApiResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: List<CheckInResponse>,
    @SerializedName("pagination")
    val pagination: PaginationResponse,
    @SerializedName("timestamp")
    val timestamp: String
)

data class TodayCheckInApiResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: CheckInResponse?,
    @SerializedName("hasCompletedToday")
    val hasCompletedToday: Boolean,
    @SerializedName("timestamp")
    val timestamp: String
)

data class PaginationResponse(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("totalCount")
    val totalCount: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("hasNextPage")
    val hasNextPage: Boolean,
    @SerializedName("hasPreviousPage")
    val hasPreviousPage: Boolean
)

// ============= EMOTIONAL CALENDAR =============

data class CreateEmotionalCalendarRequest(
    @SerializedName("date")
    val date: String, // ISO 8601 format
    @SerializedName("emotionalEmoji")
    val emotionalEmoji: String,
    @SerializedName("moodLevel")
    val moodLevel: Int,
    @SerializedName("emotionalTags")
    val emotionalTags: List<String>
)

data class EmotionalCalendarEntryResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("emotionalEmoji")
    val emotionalEmoji: String,
    @SerializedName("moodLevel")
    val moodLevel: Int,
    @SerializedName("emotionalTags")
    val emotionalTags: List<String>,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

data class CreateEmotionalCalendarApiResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: EmotionalCalendarEntryResponse,
    @SerializedName("timestamp")
    val timestamp: String
)

data class EmotionalCalendarApiResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: EmotionalCalendarDataResponse,
    @SerializedName("timestamp")
    val timestamp: String
)

data class EmotionalCalendarDataResponse(
    @SerializedName("entries")
    val entries: List<EmotionalCalendarEntryResponse>,
    @SerializedName("totalCount")
    val totalCount: Int,
    @SerializedName("dateRange")
    val dateRange: DateRangeResponse
)

data class DateRangeResponse(
    @SerializedName("startDate")
    val startDate: String?,
    @SerializedName("endDate")
    val endDate: String?
)

// ============= DASHBOARD =============

data class DashboardSummary(
    @SerializedName("hasTodayCheckIn")
    val hasTodayCheckIn: Boolean,
    @SerializedName("todayCheckIn")
    val todayCheckIn: CheckInResponse?,
    @SerializedName("totalCheckIns")
    val totalCheckIns: Int,
    @SerializedName("totalEmotionalCalendarEntries")
    val totalEmotionalCalendarEntries: Int,
    @SerializedName("averageEmotionalLevel")
    val averageEmotionalLevel: Double,
    @SerializedName("averageEnergyLevel")
    val averageEnergyLevel: Double,
    @SerializedName("averageMoodLevel")
    val averageMoodLevel: Double,
    @SerializedName("mostCommonSymptoms")
    val mostCommonSymptoms: List<String>,
    @SerializedName("mostUsedEmotionalTags")
    val mostUsedEmotionalTags: List<String>
)

data class DashboardInsights(
    @SerializedName("messages")
    val messages: List<String>
)

data class DashboardDataResponse(
    @SerializedName("summary")
    val summary: DashboardSummary,
    @SerializedName("insights")
    val insights: DashboardInsights
)

data class DashboardApiResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: DashboardDataResponse,
    @SerializedName("timestamp")
    val timestamp: String
)