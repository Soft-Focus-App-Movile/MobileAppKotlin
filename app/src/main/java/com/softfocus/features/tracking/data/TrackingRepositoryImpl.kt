package com.softfocus.features.tracking.data

import com.softfocus.core.common.result.Result
import com.softfocus.features.tracking.data.mapper.toDomain
import com.softfocus.features.tracking.data.model.CreateCheckInRequest
import com.softfocus.features.tracking.data.model.CreateEmotionalCalendarRequest
import com.softfocus.features.tracking.data.model.CreateQuickEmotionalEntryRequest
import com.softfocus.features.tracking.data.remote.TrackingApi
import com.softfocus.features.tracking.domain.model.*
import com.softfocus.features.tracking.domain.repository.TrackingRepository
import javax.inject.Inject

class TrackingRepositoryImpl @Inject constructor(
    private val api: TrackingApi
) : TrackingRepository {

    override suspend fun createCheckIn(
        emotionalLevel: Int,
        energyLevel: Int,
        moodDescription: String,
        sleepHours: Int,
        symptoms: List<String>,
        notes: String?
    ): Result<CheckIn> {
        return try {
            val request = CreateCheckInRequest(
                emotionalLevel = emotionalLevel,
                energyLevel = energyLevel,
                moodDescription = moodDescription,
                sleepHours = sleepHours,
                symptoms = symptoms,
                notes = notes
            )

            val response = api.createCheckIn(request)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.data.toDomain())
            } else {
                Result.Error(response.message() ?: "Error creating check-in")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getCheckIns(
        startDate: String?,
        endDate: String?,
        pageNumber: Int?,
        pageSize: Int?
    ): Result<CheckInHistory> {
        return try {
            val response = api.getCheckIns(startDate, endDate, pageNumber, pageSize)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.toDomain())
            } else {
                Result.Error(response.message() ?: "Error fetching check-ins")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getCheckInById(id: String): Result<CheckIn> {
        return try {
            val response = api.getCheckInById(id)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.data.toDomain())
            } else {
                Result.Error(response.message() ?: "Error fetching check-in")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getTodayCheckIn(): Result<TodayCheckIn> {
        return try {
            val response = api.getTodayCheckIn()

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.toDomain())
            } else {
                Result.Error(response.message() ?: "Error fetching today's check-in")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun createEmotionalCalendarEntry(
        timestamp: String,
        emotionalEmoji: String,
        moodLevel: Int,
        emotionalTags: List<String>,
        content: String,
        sessionDurationSeconds: Int,
        entryType: String
    ): Result<EmotionalCalendarEntry> {
        return try {
            val request = CreateEmotionalCalendarRequest(
                timestamp = timestamp,
                emotionalEmoji = emotionalEmoji,
                moodLevel = moodLevel,
                emotionalTags = emotionalTags,
                content = content,
                sessionDurationSeconds = sessionDurationSeconds,
                entryType = entryType
            )

            val response = api.createEmotionalCalendarEntry(request)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.data.toDomain())
            } else {
                Result.Error(response.message() ?: "Error creating emotional calendar entry")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun createQuickEmotionalEntry(
        timestamp: String,
        emotionalEmoji: String,
        moodLevel: Int,
        content: String,
        sessionDurationSeconds: Int,
        entryType: String
    ): Result<EmotionalCalendarEntry> {
        return try {
            val request = CreateQuickEmotionalEntryRequest(
                timestamp = timestamp,
                emotionalEmoji = emotionalEmoji,
                moodLevel = moodLevel,
                content = content,
                sessionDurationSeconds = sessionDurationSeconds
            )

            val response = api.createQuickEmotionalEntry(request)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.data.toDomain())
            } else {
                Result.Error(response.message() ?: "Error creating quick emotional entry")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getTodayEmotionalEntries(): Result<List<EmotionalCalendarEntry>> {
        return try {
            val response = api.getTodayEmotionalEntries()

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.entries.map { it.toDomain() })
            } else {
                Result.Error(response.message() ?: "Error fetching today's emotional entries")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun deleteTodayEmotionalEntries(entryType: String?): Result<DeleteTodayEmotionalEntriesResult> {
        return try {
            val response = api.deleteTodayEmotionalEntries(entryType)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.toDomain())
            } else {
                Result.Error(response.message() ?: "Error deleting today's emotional entries")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getEmotionalEntriesByDate(date: String): Result<List<EmotionalCalendarEntry>> {
        return try {
            val response = api.getEmotionalEntriesByDate(date)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.entries.map { it.toDomain() })
            } else {
                Result.Error(response.message() ?: "Error fetching emotional entries by date")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getEmotionalCalendar(
        startDate: String?,
        endDate: String?
    ): Result<EmotionalCalendar> {
        return try {
            val response = api.getEmotionalCalendar(startDate, endDate)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.toDomain())
            } else {
                Result.Error(response.message() ?: "Error fetching emotional calendar")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getEmotionalCalendarByDate(date: String): Result<EmotionalCalendarEntry> {
        return try {
            val response = api.getEmotionalCalendarByDate(date)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.data.toDomain())
            } else {
                Result.Error(response.message() ?: "Error fetching emotional calendar entry")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getDashboard(days: Int?): Result<TrackingDashboard> {
        return try {
            val response = api.getDashboard(days)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.toDomain())
            } else {
                Result.Error(response.message() ?: "Error fetching dashboard")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getPatientDashboard(userId: String, days: Int?): Result<TrackingDashboard> {
        return try {
            val response = api.getPatientDashboard(userId, days)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.toDomain())
            } else {
                Result.Error(response.message() ?: "Error fetching patient dashboard")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }
}
