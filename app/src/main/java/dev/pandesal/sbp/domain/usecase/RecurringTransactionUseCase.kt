package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.Notification
import dev.pandesal.sbp.domain.model.NotificationType
import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.domain.model.UpcomingReminder
import dev.pandesal.sbp.domain.repository.RecurringTransactionRepositoryInterface
import dev.pandesal.sbp.domain.repository.ReminderRepositoryInterface
import dev.pandesal.sbp.domain.model.RecurringTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class RecurringTransactionUseCase @Inject constructor(
    private val repository: RecurringTransactionRepositoryInterface,
    private val reminderRepository: ReminderRepositoryInterface
) {

    fun getRecurringTransactionById(id: String): Flow<RecurringTransaction> =
        repository.getRecurringTransactionById(id)

    fun getRecurringTransactions(): Flow<List<RecurringTransaction>> =
        repository.getRecurringTransactions()

    fun getUpcomingNotifications(
        currentDate: LocalDate = LocalDate.now(),
        withinDays: Long = 7
    ): Flow<List<Notification>> {
        val endDate = currentDate.plusDays(withinDays)
        return combine(
            repository.getRecurringTransactions(),
            reminderRepository.getReminders()
        ) { recList, reminders ->
            val recurringNotifs = recList.filter { it.reminderEnabled }.mapNotNull { rec ->
                val next = nextDueDate(rec, currentDate)
                if (!next.isAfter(endDate)) {
                    Notification(
                        message = "${rec.transaction.name} due on $next",
                        type = NotificationType.BILL_REMINDER,
                        timestamp = next.atStartOfDay()
                    )
                } else {
                    null
                }
            }
            val reminderNotifs = reminders.filter {
                it.shouldNotify && it.date.isAfter(currentDate)
            }.map { rem ->
                Notification(
                    message = "Reminder: ${rem.message} on ${rem.date}",
                    type = NotificationType.GENERAL,
                    timestamp = rem.date.atStartOfDay()
                )
            }
            (recurringNotifs + reminderNotifs).sortedBy { it.timestamp }
        }
    }

    fun getUpcomingReminders(
        currentDate: LocalDate = LocalDate.now()
    ): Flow<List<UpcomingReminder>> {
        return combine(
            repository.getRecurringTransactions(),
            reminderRepository.getReminders()
        ) { recs, reminders ->
            val recurringItems = recs.filter { it.reminderEnabled }.map { rec ->
                val next = nextDueDate(rec, currentDate)
                UpcomingReminder(
                    id = rec.transaction.id,
                    title = rec.transaction.name,
                    dueDate = next,
                    interval = rec.interval,
                    amount = rec.transaction.amount,
                    category = rec.transaction.category
                )
            }
            val reminderItems = reminders.map { rem ->
                UpcomingReminder(
                    id = rem.id,
                    title = rem.message,
                    dueDate = rem.date
                )
            }
            (recurringItems + reminderItems).sortedBy { it.dueDate }
        }
    }

    fun nextDueDate(rec: RecurringTransaction, fromDate: LocalDate = LocalDate.now()): LocalDate {
        var due = rec.startDate
        while (!due.isAfter(fromDate)) {
            due = when (rec.interval) {
                RecurringInterval.DAILY -> due.plusDays(1)
                RecurringInterval.WEEKLY -> due.plusWeeks(1)
                RecurringInterval.MONTHLY -> due.plusMonths(1)
                RecurringInterval.AFTER_CUTOFF -> due.plusMonths(1).withDayOfMonth(rec.cutoffDays)
                RecurringInterval.QUARTERLY -> due.plusMonths(3)
                RecurringInterval.HALF_YEARLY -> due.plusMonths(6)
                RecurringInterval.YEARLY -> due.plusYears(1)
            }
        }
        return due
    }

    fun occursOn(rec: RecurringTransaction, date: LocalDate): Boolean {
        var due = rec.startDate
        while (due.isBefore(date)) {
            due = when (rec.interval) {
                RecurringInterval.DAILY -> due.plusDays(1)
                RecurringInterval.WEEKLY -> due.plusWeeks(1)
                RecurringInterval.MONTHLY -> due.plusMonths(1)
                RecurringInterval.AFTER_CUTOFF -> due.plusMonths(1).withDayOfMonth(rec.cutoffDays)
                RecurringInterval.QUARTERLY -> due.plusMonths(3)
                RecurringInterval.HALF_YEARLY -> due.plusMonths(6)
                RecurringInterval.YEARLY -> due.plusYears(1)
            }
        }
        return due == date
    }

    fun occurrencesInRange(
        rec: RecurringTransaction,
        start: LocalDate,
        end: LocalDate
    ): List<LocalDate> {
        val results = mutableListOf<LocalDate>()
        var next = nextDueDate(rec, start.minusDays(1))
        while (!next.isAfter(end)) {
            if (!next.isBefore(start)) {
                results.add(next)
            }
            next = when (rec.interval) {
                RecurringInterval.DAILY -> next.plusDays(1)
                RecurringInterval.WEEKLY -> next.plusWeeks(1)
                RecurringInterval.MONTHLY -> next.plusMonths(1)
                RecurringInterval.AFTER_CUTOFF -> next.plusMonths(1).withDayOfMonth(rec.cutoffDays)
                RecurringInterval.QUARTERLY -> next.plusMonths(3)
                RecurringInterval.HALF_YEARLY -> next.plusMonths(6)
                RecurringInterval.YEARLY -> next.plusYears(1)
            }
        }
        return results
    }

    fun getRecurringTransactionsOn(date: LocalDate): Flow<List<RecurringTransaction>> =
        repository.getRecurringTransactions().map { list ->
            list.filter { occursOn(it, date) }
        }

    suspend fun addRecurringTransaction(transaction: RecurringTransaction) {
        repository.addRecurringTransaction(transaction)
    }

    suspend fun removeRecurringTransaction(transaction: RecurringTransaction) {
        repository.removeRecurringTransaction(transaction)
    }
}
