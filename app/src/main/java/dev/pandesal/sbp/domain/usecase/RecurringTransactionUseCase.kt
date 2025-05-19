package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.Notification
import dev.pandesal.sbp.domain.model.NotificationType
import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.domain.repository.RecurringTransactionRepositoryInterface
import dev.pandesal.sbp.domain.model.RecurringTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class RecurringTransactionUseCase @Inject constructor(
    private val repository: RecurringTransactionRepositoryInterface
) {
    fun getRecurringTransactionById(id: String): Flow<RecurringTransaction> =
        repository.getRecurringTransactionById(id)
    fun getUpcomingNotifications(
        currentDate: LocalDate = LocalDate.now(),
        withinDays: Long = 7
    ): Flow<List<Notification>> {
        val endDate = currentDate.plusDays(withinDays)
        return repository.getRecurringTransactions().map { list ->
            list.mapNotNull { rec ->
                val next = nextDueDate(rec, currentDate)
                if (!next.isAfter(endDate)) {
                    Notification(
                        message = "${rec.transaction.name} due on $next",
                        type = NotificationType.BILL_REMINDER
                    )
                } else {
                    null
                }
            }
        }
    }

    private fun nextDueDate(rec: dev.pandesal.sbp.domain.model.RecurringTransaction, fromDate: LocalDate): LocalDate {
        var due = rec.startDate
        while (!due.isAfter(fromDate)) {
            due = when (rec.interval) {
                RecurringInterval.DAILY -> due.plusDays(1)
                RecurringInterval.WEEKLY -> due.plusWeeks(1)
                RecurringInterval.MONTHLY -> due.plusMonths(1)
                RecurringInterval.AFTER_CUTOFF -> due.plusMonths(1).withDayOfMonth(rec.cutoffDays)
            }
        }
        return due
    }

    suspend fun addRecurringTransaction(transaction: RecurringTransaction) {
        repository.addRecurringTransaction(transaction)
    }

    suspend fun removeRecurringTransaction(transaction: RecurringTransaction) {
        repository.removeRecurringTransaction(transaction)
    }
}
