package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import dev.pandesal.sbp.extensions.LocalTimeSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
@Parcelize
data class AttendanceRecord(
    val name: String,
    @Serializable(with = LocalTimeSerializer::class)
    val checkIn: LocalTime? = null,
    val lateCount: Int = 0
) : Parcelable
