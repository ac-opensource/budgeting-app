package dev.pandesal.sbp.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object InAppNotificationCenter {
    private val _notifications = MutableStateFlow<List<String>>(emptyList())
    val notifications: StateFlow<List<String>> = _notifications.asStateFlow()

    fun postNotification(message: String) {
        _notifications.value = _notifications.value + message
    }
}
