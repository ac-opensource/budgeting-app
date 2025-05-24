package dev.pandesal.sbp.domain.model

import java.math.BigDecimal
import java.time.LocalDate

/** Combined reminder model for one-time reminders and recurring transactions */
data class UpcomingReminder(
    val id: String,
    val title: String,
    val dueDate: LocalDate,
    val interval: RecurringInterval? = null,
    val amount: BigDecimal? = null,
    val category: Category? = null
) {
    val isRecurring: Boolean get() = interval != null
}
