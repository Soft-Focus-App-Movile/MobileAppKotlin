package com.softfocus.features.tracking.domain.usecase

import com.softfocus.features.tracking.domain.repository.TrackingRepository
import javax.inject.Inject

class GetTrackingDashboardUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke(days: Int? = null) = repository.getDashboard(days)
}

class GetPatientDashboardUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke(userId: String, days: Int? = null) =
        repository.getPatientDashboard(userId, days)
}