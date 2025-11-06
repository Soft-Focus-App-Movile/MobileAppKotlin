package com.softfocus.features.notifications.data.repositories


import com.softfocus.core.data.local.UserSession
import com.softfocus.features.notifications.data.remote.NotificationService
import com.softfocus.features.notifications.domain.models.*
import com.softfocus.features.notifications.domain.repositories.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class NotificationRepositoryImpl @Inject constructor(
    private val notificationService: NotificationService,
    private val userSession: UserSession
) : NotificationRepository {

    override suspend fun getNotifications(
        userId: String,
        status: DeliveryStatus?,
        type: NotificationType?,
        page: Int,
        size: Int
    ): Result<List<Notification>> {
        return try {
            // Validar y asegurar valores positivos
            val validPage = if (page > 0) page else 1
            val validSize = if (size > 0) size else 20

            val response = notificationService.getNotifications(
                status = status?.name?.lowercase(),
                type = type?.name?.lowercase(),
                page = validPage,  // ✅ Siempre positivo
                size = validSize   // ✅ Siempre positivo
            )

            if (response.isSuccessful && response.body() != null) {
                val notifications = response.body()!!.notifications.map { it.toDomain() }
                Result.success(notifications)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Error ${response.code()}: ${errorBody ?: "Error al obtener notificaciones"}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    override suspend fun getNotificationById(id: String): Result<Notification> {
        return try {
            val response = notificationService.getNotificationById(id)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Notificación no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendNotification(notification: Notification): Result<Notification> {
        // Este método generalmente es para uso interno del backend
        return Result.failure(Exception("No implementado en cliente"))
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            val response = notificationService.markAsRead(notificationId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al marcar como leída"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAllAsRead(userId: String): Result<Unit> {
        return try {
            val response = notificationService.markAllAsRead()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al marcar todas como leídas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNotification(notificationId: String): Result<Unit> {
        return try {
            val response = notificationService.deleteNotification(notificationId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar notificación"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeNotifications(userId: String): Flow<List<Notification>> = flow {
        // Implementar con WebSocket o polling
        // Por ahora, emitir lista vacía
        emit(emptyList())
    }

    override suspend fun getUnreadCount(userId: String): Result<Int> {
        return try {
            val response = notificationService.getUnreadCount()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.unreadCount)
            } else {
                Result.failure(Exception("Error al obtener contador"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}