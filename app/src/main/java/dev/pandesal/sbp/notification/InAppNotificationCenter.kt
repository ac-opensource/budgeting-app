package dev.pandesal.sbp.notification

import dev.pandesal.sbp.domain.model.Notification
import dev.pandesal.sbp.domain.model.NotificationType
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object InAppNotificationCenter {
    private val _notifications = MutableStateFlow<List<Notification>>(
        listOf(
            Notification(
                message = "Electric bill due tomorrow",
                type = NotificationType.BILL_REMINDER,
                timestamp = LocalDateTime.now()
            ),
            Notification(
                message = "Did you spend with GCash today?",
                type = NotificationType.TRANSACTION_SUGGESTION,
                canCreateTransaction = true,
                timestamp = LocalDateTime.now()
            )
        )
    )
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    fun postNotification(
        message: String,
        type: NotificationType = NotificationType.GENERAL,
        canCreateTransaction: Boolean = false,
        timestamp: LocalDateTime = LocalDateTime.now()
    ) {
        _notifications.value =
            _notifications.value + Notification(
                message = message,
                type = type,
                canCreateTransaction = canCreateTransaction,
                timestamp = timestamp
            )
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
