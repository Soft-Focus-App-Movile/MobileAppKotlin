package com.softfocus.features.notifications.domain.models

import java.time.LocalTime

data class NotificationSchedule(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val daysOfWeek: List<Int> // 1-7 (Lunes a Domingo)
)