package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import dev.pandesal.sbp.extensions.LocalDateSerializer
import dev.pandesal.sbp.extensions.LocalDateTimeSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

enum class NotificationType {
    GENERAL,
    BILL_REMINDER,
    TRANSACTION_SUGGESTION
}

@Serializable
@Parcelize
data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val type: NotificationType = NotificationType.GENERAL,
    val isRead: Boolean = false,
    val canCreateTransaction: Boolean = false,
    @Serializable(with = LocalDateTimeSerializer::class)
    val timestamp: LocalDateTime = LocalDateTime.now()
) : Parcelable
