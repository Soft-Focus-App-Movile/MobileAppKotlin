package com.softfocus.features.tracking.data.mapper

import com.softfocus.features.tracking.data.model.*
import com.softfocus.features.tracking.domain.model.*

// ============= CHECK-INS MAPPERS =============

fun CheckInResponse.toDomain(): CheckIn {
    return CheckIn(
        id = id,
        userId = userId,
        emotionalLevel = emotionalLevel,
        energyLevel = energyLevel,
        moodDescription = moodDescription,
        sleepHours = sleepHours,
        symptoms = symptoms,
        notes = notes,
        completedAt = completedAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun PaginationResponse.toDomain(): Pagination {
    return Pagination(
        currentPage = currentPage,
        pageSize = pageSize,
        totalCount = totalCount,
        totalPages = totalPages,
        hasNextPage = hasNextPage,
        hasPreviousPage = hasPreviousPage
    )
}

fun CheckInsHistoryApiResponse.toDomain(): CheckInHistory {
    return CheckInHistory(
        checkIns = data.map { it.toDomain() },
        pagination = pagination.toDomain()
    )
}

fun TodayCheckInApiResponse.toDomain(): TodayCheckIn {
    return TodayCheckIn(
        checkIn = data?.toDomain(),
        hasCompletedToday = hasCompletedToday
    )
}

// ============= EMOTIONAL CALENDAR MAPPERS =============

fun EmotionalCalendarEntryResponse.toDomain(): EmotionalCalendarEntry {
    return EmotionalCalendarEntry(
        id = id,
        userId = userId,
        timestamp = timestamp ?: date,  // Fallback a date para entradas legacy sin timestamp
        date = date,
        emotionalEmoji = emotionalEmoji,
        moodLevel = moodLevel,
        emotionalTags = emotionalTags,
        content = content ?: "",
        sessionDurationSeconds = sessionDurationSeconds ?: 0,
        entryType = entryType ?: "spontaneous",
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun DateRangeResponse.toDomain(): DateRange {
    return DateRange(
        startDate = startDate,
        endDate = endDate
    )
}

fun EmotionalCalendarApiResponse.toDomain(): EmotionalCalendar {
    return EmotionalCalendar(
        entries = data.entries.map { it.toDomain() },
        totalCount = data.totalCount,
        dateRange = data.dateRange.toDomain()
    )
}

fun DeleteTodayEmotionalEntriesApiResponse.toDomain(): DeleteTodayEmotionalEntriesResult {
    return DeleteTodayEmotionalEntriesResult(
        deletedCount = deletedCount,
        failedCount = failedCount,
        totalMatched = totalMatched,
        entryType = entryType
    )
}

// ============= DASHBOARD MAPPERS =============

fun com.softfocus.features.tracking.data.model.DashboardSummary.toDomain(): com.softfocus.features.tracking.domain.model.DashboardSummary {
    return com.softfocus.features.tracking.domain.model.DashboardSummary(
        hasTodayCheckIn = hasTodayCheckIn,
        todayCheckIn = todayCheckIn?.toDomain(),
        totalCheckIns = totalCheckIns,
        totalEmotionalCalendarEntries = totalEmotionalCalendarEntries,
        averageEmotionalLevel = averageEmotionalLevel,
        averageEnergyLevel = averageEnergyLevel,
        averageMoodLevel = averageMoodLevel,
        mostCommonSymptoms = mostCommonSymptoms,
        mostUsedEmotionalTags = mostUsedEmotionalTags
    )
}

fun com.softfocus.features.tracking.data.model.DashboardInsights.toDomain(): com.softfocus.features.tracking.domain.model.DashboardInsights {
    return com.softfocus.features.tracking.domain.model.DashboardInsights(
        messages = messages
    )
}

fun DashboardApiResponse.toDomain(): TrackingDashboard {
    return TrackingDashboard(
        summary = data.summary.toDomain(),
        insights = data.insights.toDomain()
    )
}
