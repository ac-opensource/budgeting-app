package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
enum class RecurringInterval : Parcelable {
    DAILY,
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    HALF_YEARLY,
    YEARLY,
    AFTER_CUTOFF
}

@Parcelize
data class RecurringTransaction(
    val transaction: Transaction,
    val interval: RecurringInterval,
    val cutoffDays: Int = 21,
    val startDate: LocalDate = LocalDate.now(),
    val reminderEnabled: Boolean = false
) : Parcelable
