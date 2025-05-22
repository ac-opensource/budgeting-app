package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.Reminder
import dev.pandesal.sbp.domain.repository.ReminderRepositoryInterface
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class ReminderUseCase @Inject constructor(
    private val repository: ReminderRepositoryInterface
) {
    fun getReminders(): Flow<List<Reminder>> = repository.getReminders()

    fun getRemindersByDate(date: LocalDate): Flow<List<Reminder>> =
        repository.getRemindersByDate(date)

    suspend fun upsertReminder(reminder: Reminder) = repository.upsertReminder(reminder)

    suspend fun deleteReminder(reminder: Reminder) = repository.deleteReminder(reminder)
}
