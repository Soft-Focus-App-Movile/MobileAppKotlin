package com.softfocus.features.notifications.domain.models

enum class Priority(val deliveryTimeMinutes: Int) {
    LOW(60),
    NORMAL(30),
    HIGH(10),
    CRITICAL(0)
}