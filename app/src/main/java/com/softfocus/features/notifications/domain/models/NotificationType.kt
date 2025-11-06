package com.softfocus.features.notifications.domain.models

enum class NotificationType {
    INFO,           // ← Agrega este
    ALERT,          // ← Agrega este
    WARNING,        // ← Agrega este
    EMERGENCY,
    CHECKIN_REMINDER,
    CRISIS_ALERT,
    MESSAGE_RECEIVED,
    ASSIGNMENT_DUE,
    APPOINTMENT_REMINDER,
    SYSTEM_UPDATE
}