package com.softfocus.features.tracking.domain.usecase

import com.softfocus.features.tracking.domain.repository.TrackingRepository
import javax.inject.Inject

class CreateCheckInUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke(
        emotionalLevel: Int,
        energyLevel: Int,
        moodDescription: String,
        sleepHours: Int,
        symptoms: List<String>,
        notes: String?
    ) = repository.createCheckIn(
        emotionalLevel = emotionalLevel,
        energyLevel = energyLevel,
        moodDescription = moodDescription,
        sleepHours = sleepHours,
        symptoms = symptoms,
        notes = notes
    )
}

class GetCheckInsUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke(
        startDate: String? = null,
        endDate: String? = null,
        pageNumber: Int? = null,
        pageSize: Int? = null
    ) = repository.getCheckIns(startDate, endDate, pageNumber, pageSize)
}

class GetCheckInByIdUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke(id: String) = repository.getCheckInById(id)
}

class GetTodayCheckInUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke() = repository.getTodayCheckIn()
}