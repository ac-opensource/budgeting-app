package dev.pandesal.sbp.domain.repository

import dev.pandesal.sbp.domain.model.Reminder
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ReminderRepositoryInterface {
    fun getReminders(): Flow<List<Reminder>>
    fun getRemindersByDate(date: LocalDate): Flow<List<Reminder>>
    suspend fun upsertReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
}
