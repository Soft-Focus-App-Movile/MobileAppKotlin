package com.softfocus.features.notifications.presentation.list

import android.os.Build
import androidx.annotation.RequiresApi
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
    val error: String? = null,
    val unreadCount: Int = 0
)

@RequiresApi(Build.VERSION_CODES.O)
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

    init {
        loadNotifications()
        loadUnreadCount()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            // Obtener el userId desde el User completo
            val userId = userSession.getUser()?.id
            if (userId == null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Usuario no autenticado"
                )
                return@launch
            }

            val result = getNotificationsUseCase(
                userId = userId,
                status = currentFilter
            )

            result.fold(
                onSuccess = { notifications ->
                    _state.value = _state.value.copy(
                        notifications = notifications,
                        isLoading = false
                    )
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

    fun filterNotifications(status: DeliveryStatus?) {
        currentFilter = status
        loadNotifications()
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            markAsReadUseCase(notificationId).fold(
                onSuccess = {
                    // Actualizar localmente
                    val updatedList = _state.value.notifications.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(status = DeliveryStatus.READ)
                        } else {
                            notification
                        }
                    }
                    _state.value = _state.value.copy(notifications = updatedList)
                    loadUnreadCount()
                },
                onFailure = { /* Ignorar error silenciosamente */ }
            )
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val userId = userSession.getUser()?.id ?: return@launch

            notificationRepository.markAllAsRead(userId).fold(
                onSuccess = {
                    loadNotifications()
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
                    val updatedList = _state.value.notifications.filter { it.id != notificationId }
                    _state.value = _state.value.copy(notifications = updatedList)
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
}