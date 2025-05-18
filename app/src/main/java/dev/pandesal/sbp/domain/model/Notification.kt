package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Parcelize
data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val isRead: Boolean = false,
    val canCreateTransaction: Boolean = false
) : Parcelable
