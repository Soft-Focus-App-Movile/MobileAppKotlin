// features/notifications/data/remote/NotificationService.kt
package com.softfocus.features.notifications.data.remote

import com.softfocus.features.notifications.data.models.request.UpdatePreferencesRequestDto
import com.softfocus.features.notifications.data.models.response.*
import retrofit2.Response
import retrofit2.http.*

interface NotificationService {

    // ========== NOTIFICATION ENDPOINTS ==========

    // GET /api/v1/notifications - Obtener notificaciones del usuario actual
    @GET("notifications")
    suspend fun getNotifications(
        @Query("status") status: String? = null,
        @Query("type") type: String? = null,
        @Query("page") page: Int = 1,  // ✅ Valor por defecto positivo
        @Query("size") size: Int = 20  // ✅ Valor por defecto positivo
    ): Response<NotificationListResponseDto>

    // ... mismo cambio para getNotificationsByUserId
    @GET("notifications/{userId}")
    suspend fun getNotificationsByUserId(
        @Path("userId") userId: String,
        @Query("status") status: String? = null,
        @Query("type") type: String? = null,
        @Query("page") page: Int = 1,  // ✅ Valor por defecto positivo
        @Query("size") size: Int = 20  // ✅ Valor por defecto positivo
    ): Response<NotificationListResponseDto>

    // GET /api/v1/notifications/{userId} - Obtener notificaciones de usuario específico
    @GET("notifications/{userId}")
    suspend fun getNotificationsByUserId(
        @Path("userId") userId: String,
        @Query("status") status: String? = null,
        @Query("type") type: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<NotificationListResponseDto>

    // GET /api/v1/notifications/detail/{notificationId} - Detalle de notificación
    @GET("notifications/detail/{notificationId}")
    suspend fun getNotificationById(
        @Path("notificationId") notificationId: String
    ): Response<NotificationResponseDto>

    // POST /api/v1/notifications/{notificationId}/read - Marcar como leída
    @POST("notifications/{notificationId}/read")
    suspend fun markAsRead(
        @Path("notificationId") notificationId: String
    ): Response<Unit>

    // POST /api/v1/notifications/read-all - Marcar todas como leídas
    @POST("notifications/read-all")
    suspend fun markAllAsRead(): Response<Unit>

    // DELETE /api/v1/notifications/{notificationId} - Eliminar notificación
    @DELETE("notifications/{notificationId}")
    suspend fun deleteNotification(
        @Path("notificationId") notificationId: String
    ): Response<Unit>

    // GET /api/v1/notifications/unread-count - Contador de no leídas
    @GET("notifications/unread-count")
    suspend fun getUnreadCount(): Response<UnreadCountResponseDto>

    // ========== PREFERENCES ENDPOINTS ==========

    // GET /api/v1/preferences - Obtener preferencias
    @GET("preferences")
    suspend fun getPreferences(): Response<PreferenceListResponseDto>

    // PUT /api/v1/preferences - Actualizar preferencias
    @PUT("preferences")
    suspend fun updatePreferences(
        @Body request: UpdatePreferencesRequestDto
    ): Response<PreferenceListResponseDto>

    // POST /api/v1/preferences/reset - Resetear preferencias
    @POST("preferences/reset")
    suspend fun resetPreferences(): Response<PreferenceListResponseDto>
}