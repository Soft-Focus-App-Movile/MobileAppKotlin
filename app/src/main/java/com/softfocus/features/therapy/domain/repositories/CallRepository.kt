package com.softfocus.features.therapy.domain.repositories

import com.softfocus.features.therapy.domain.models.CallAccess

/**
 * Calls feature (Agora) data operations.
 */
interface CallRepository {

    /**
     * Starts a call.
     * - A patient always calls their psychologist (mode/targetUserId ignored by the backend).
     * - A psychologist can call a single patient (mode = "Direct", targetUserId set) or all of their
     *   active patients (mode = "Group").
     */
    suspend fun initiateCall(
        callType: String,
        mode: String = "Direct",
        targetUserId: String? = null
    ): Result<CallAccess>

    /** Accepts an incoming call and returns the credentials to join the channel. */
    suspend fun answerCall(callId: String): Result<CallAccess>

    /** Declines an incoming call. */
    suspend fun rejectCall(callId: String): Result<Unit>

    /** Ends / leaves a call. */
    suspend fun endCall(callId: String): Result<Unit>
}
