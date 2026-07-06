package com.softfocus.features.tracking.domain.usecase

import com.softfocus.features.tracking.domain.repository.TrackingRepository
import javax.inject.Inject

class CreateEmotionalCalendarEntryUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke(
        timestamp: String,
        emotionalEmoji: String,
        moodLevel: Int,
        emotionalTags: List<String>,
        content: String = "",
        sessionDurationSeconds: Int = 0,
        entryType: String = "spontaneous"
    ) = repository.createEmotionalCalendarEntry(
        timestamp = timestamp,
        emotionalEmoji = emotionalEmoji,
        moodLevel = moodLevel,
        emotionalTags = emotionalTags,
        content = content,
        sessionDurationSeconds = sessionDurationSeconds,
        entryType = entryType
    )
}

class CreateQuickEmotionalEntryUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke(
        emotionalEmoji: String,
        moodLevel: Int,
        content: String = "",
        sessionDurationSeconds: Int = 0
    ) = repository.createQuickEmotionalEntry(
        timestamp = java.time.Instant.now().toString(),
        emotionalEmoji = emotionalEmoji,
        moodLevel = moodLevel,
        content = content,
        sessionDurationSeconds = sessionDurationSeconds,
        entryType = "spontaneous"
    )
}

class GetTodayEmotionalEntriesUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke() = repository.getTodayEmotionalEntries()
}

class DeleteTodayEmotionalEntriesUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke(entryType: String? = null) =
        repository.deleteTodayEmotionalEntries(entryType)
}

class GetEmotionalCalendarUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke(
        startDate: String? = null,
        endDate: String? = null
    ) = repository.getEmotionalCalendar(startDate, endDate)
}

class GetEmotionalCalendarByDateUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke(date: String) =
        repository.getEmotionalCalendarByDate(date)
}
