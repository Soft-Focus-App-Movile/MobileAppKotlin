package com.softfocus.features.notifications.presentation.list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.notifications.domain.models.DeliveryStatus
import com.softfocus.features.notifications.domain.models.Notification
import com.softfocus.features.notifications.domain.usecases.GetNotificationsUseCase
import com.softfocus.features.notifications.domain.usecases.MarkAsReadUseCase
import com.softfocus.features.notifications.domain.repositories.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val unreadCount: Int = 0
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markAsReadUseCase: MarkAsReadUseCase,
    private val notificationRepository: NotificationRepository,
    private val userSession: UserSession
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    private var currentFilter: DeliveryStatus? = null
    private var allNotifications = listOf<Notification>() // ← NUEVO: Guardar todas las notificaciones

    init {
        loadNotifications()
        loadUnreadCount()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val userId = userSession.getUser()?.id
            if (userId == null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Usuario no autenticado"
                )
                return@launch
            }

            // Siempre cargar TODAS las notificaciones sin filtro
            val result = getNotificationsUseCase(
                userId = userId,
                status = null // ← Cambia esto: siempre cargar sin filtro
            )

            result.fold(
                onSuccess = { notifications ->
                    allNotifications = notifications // ← Guardar todas
                    applyFilter() // ← Aplicar filtro actual
                    _state.value = _state.value.copy(isLoading = false)
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar notificaciones"
                    )
                }
            )
        }
    }

    // NUEVA FUNCIÓN: Aplicar filtro a todas las notificaciones
    private fun applyFilter() {
        val filtered = when (currentFilter) {
            DeliveryStatus.DELIVERED -> allNotifications.filter {
                it.readAt == null // "No leídas" = cualquier notificación sin readAt
            }
            else -> allNotifications
        }
        _state.value = _state.value.copy(notifications = filtered)
    }

    fun filterNotifications(status: DeliveryStatus?) {
        currentFilter = status
        applyFilter() // ← Cambia esto: en lugar de recargar, solo aplicar filtro
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            println("🔵 [VIEWMODEL] Marcando como leída: $notificationId")

            markAsReadUseCase(notificationId).fold(
                onSuccess = {
                    println("✅ [VIEWMODEL] Marcado como leída exitosamente en backend")

                    // Actualizar localmente
                    allNotifications = allNotifications.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(
                                status = DeliveryStatus.READ,
                                readAt = java.time.LocalDateTime.now() // ← Agregar esta línea
                            )
                        } else {
                            notification
                        }
                    }
                    applyFilter()
                    loadUnreadCount()
                },
                onFailure = { error ->
                    println("❌ [VIEWMODEL] Error al marcar como leída: ${error.message}")
                }
            )
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val userId = userSession.getUser()?.id ?: return@launch

            notificationRepository.markAllAsRead(userId).fold(
                onSuccess = {
                    // Actualizar TODAS localmente
                    allNotifications = allNotifications.map { notification ->
                        notification.copy(
                            status = DeliveryStatus.READ,
                            readAt = notification.readAt ?: java.time.LocalDateTime.now() // ← Agregar esta línea
                        )
                    }
                    applyFilter() // ← Re-aplicar filtro
                    loadUnreadCount()
                },
                onFailure = { /* Ignorar error */ }
            )
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.deleteNotification(notificationId).fold(
                onSuccess = {
                    // Actualizar TODAS las notificaciones
                    allNotifications = allNotifications.filter { it.id != notificationId }
                    applyFilter() // ← Re-aplicar filtro
                    loadUnreadCount()
                },
                onFailure = { /* Ignorar error */ }
            )
        }
    }

    private fun loadUnreadCount() {
        viewModelScope.launch {
            val userId = userSession.getUser()?.id ?: return@launch

            notificationRepository.getUnreadCount(userId).fold(
                onSuccess = { count ->
                    _state.value = _state.value.copy(unreadCount = count)
                },
                onFailure = { /* Ignorar error */ }
            )
        }
    }

    fun refreshNotifications() {
        viewModelScope.launch {
            println("🔄 [VIEWMODEL] Refresh manual iniciado")
            _state.value = _state.value.copy(isRefreshing = true, error = null)

            val userId = userSession.getUser()?.id ?: return@launch

            val result = notificationRepository.getNotifications(
                userId = userId,
                status = null,
                page = 1,
                size = 20
            )

            result.fold(
                onSuccess = { notifications ->
                    println("✅ [VIEWMODEL] Refresh exitoso: ${notifications.size} notificaciones")

                    // DEBUG: Mostrar todas las notificaciones con sus estados
                    notifications.forEach { notification ->
                        println("🔍 [VIEWMODEL] Notificación: ${notification.id}")
                        println("   📝 Title: ${notification.title}")
                        println("   📊 Status: ${notification.status}")
                        println("   📖 ReadAt: ${notification.readAt}")
                        println("   🕒 CreatedAt: ${notification.createdAt}")
                    }

                    allNotifications = notifications
                    applyFilter()
                    _state.value = _state.value.copy(isRefreshing = false)
                    loadUnreadCount()
                },
                onFailure = { error ->
                    println("❌ [VIEWMODEL] Error en refresh: ${error.message}")
                    _state.value = _state.value.copy(
                        isRefreshing = false,
                        error = error.message ?: "Error al actualizar"
                    )
                }
            )
        }
    }
}