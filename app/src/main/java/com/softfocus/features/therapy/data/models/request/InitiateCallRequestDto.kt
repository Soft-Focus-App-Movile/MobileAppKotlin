package com.softfocus.features.therapy.data.models.request

/**
 * Body for POST /calls/initiate.
 * - callType: "Audio" | "Video"
 * - mode: "Direct" | "Group" (patients always do Direct; ignored for them)
 * - targetUserId: only used by a psychologist placing a Direct call to a specific patient
 */
data class InitiateCallRequestDto(
    val callType: String,
    val mode: String = "Direct",
    val targetUserId: String? = null
)
