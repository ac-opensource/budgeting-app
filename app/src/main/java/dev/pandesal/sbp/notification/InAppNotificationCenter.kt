package dev.pandesal.sbp.notification

import dev.pandesal.sbp.domain.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object InAppNotificationCenter {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    fun postNotification(message: String, canCreateTransaction: Boolean = false) {
        _notifications.value =
            _notifications.value + Notification(message = message, canCreateTransaction = canCreateTransaction)
    }

    fun markAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun archive(id: String) {
        _notifications.value = _notifications.value.filterNot { it.id == id }
    }
}
