package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.UUID

@Parcelize
data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate,
    val message: String
) : Parcelable
