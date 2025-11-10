package com.softfocus.features.tracking.domain.usecase

import com.softfocus.features.tracking.domain.repository.TrackingRepository
import javax.inject.Inject

class CreateEmotionalCalendarEntryUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke(
        date: String,
        emotionalEmoji: String,
        moodLevel: Int,
        emotionalTags: List<String>
    ) = repository.createEmotionalCalendarEntry(
        date = date,
        emotionalEmoji = emotionalEmoji,
        moodLevel = moodLevel,
        emotionalTags = emotionalTags
    )
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