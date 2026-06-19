package com.softfocus.features.therapy.data.repositories

import android.content.Context
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.therapy.data.models.request.InitiateCallRequestDto
import com.softfocus.features.therapy.data.remote.CallService
import com.softfocus.features.therapy.domain.models.CallAccess
import com.softfocus.features.therapy.domain.repositories.CallRepository

class CallRepositoryImpl(
    private val callService: CallService,
    context: Context
) : CallRepository {

    private val userSession = UserSession(context)

    private fun getAuthToken(): String {
        val token = userSession.getUser()?.token
        if (token.isNullOrEmpty()) {
            throw IllegalStateException("Token no disponible. Usuario debe iniciar sesión nuevamente.")
        }
        return "Bearer $token"
    }

    override suspend fun initiateCall(
        callType: String,
        mode: String,
        targetUserId: String?
    ): Result<CallAccess> {
        return try {
            val response = callService.initiateCall(
                token = getAuthToken(),
                request = InitiateCallRequestDto(callType = callType, mode = mode, targetUserId = targetUserId)
            )
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun answerCall(callId: String): Result<CallAccess> {
        return try {
            Result.success(callService.answerCall(getAuthToken(), callId).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejectCall(callId: String): Result<Unit> {
        return try {
            callService.rejectCall(getAuthToken(), callId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun endCall(callId: String): Result<Unit> {
        return try {
            callService.endCall(getAuthToken(), callId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
