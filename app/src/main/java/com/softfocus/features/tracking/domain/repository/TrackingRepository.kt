package com.softfocus.features.tracking.domain.repository

import com.softfocus.core.common.result.Result
import com.softfocus.features.tracking.domain.model.*

interface TrackingRepository {

    // ============= CHECK-INS =============

    suspend fun createCheckIn(
        emotionalLevel: Int,
        energyLevel: Int,
        moodDescription: String,
        sleepHours: Int,
        symptoms: List<String>,
        notes: String?
    ): Result<CheckIn>

    suspend fun getCheckIns(
        startDate: String? = null,
        endDate: String? = null,
        pageNumber: Int? = null,
        pageSize: Int? = null
    ): Result<CheckInHistory>

    suspend fun getCheckInById(id: String): Result<CheckIn>

    suspend fun getTodayCheckIn(): Result<TodayCheckIn>

    // ============= EMOTIONAL CALENDAR =============

    suspend fun createEmotionalCalendarEntry(
        timestamp: String,
        emotionalEmoji: String,
        moodLevel: Int,
        emotionalTags: List<String>,
        content: String = "",
        sessionDurationSeconds: Int = 0,
        entryType: String = "spontaneous"
    ): Result<EmotionalCalendarEntry>

    suspend fun createQuickEmotionalEntry(
        timestamp: String,
        emotionalEmoji: String,
        moodLevel: Int,
        content: String = "",
        sessionDurationSeconds: Int = 0,
        entryType: String = "spontaneous"
    ): Result<EmotionalCalendarEntry>

    suspend fun getTodayEmotionalEntries(): Result<List<EmotionalCalendarEntry>>

    suspend fun deleteTodayEmotionalEntries(entryType: String? = null): Result<DeleteTodayEmotionalEntriesResult>

    suspend fun getEmotionalEntriesByDate(date: String): Result<List<EmotionalCalendarEntry>>

    suspend fun getEmotionalCalendar(
        startDate: String? = null,
        endDate: String? = null
    ): Result<EmotionalCalendar>

    suspend fun getEmotionalCalendarByDate(date: String): Result<EmotionalCalendarEntry>

    // ============= DASHBOARD =============

    suspend fun getDashboard(days: Int? = null): Result<TrackingDashboard>

    suspend fun getPatientDashboard(userId: String, days: Int? = null): Result<TrackingDashboard>
}
