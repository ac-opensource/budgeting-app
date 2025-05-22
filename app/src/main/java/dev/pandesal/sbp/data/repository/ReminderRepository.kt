package dev.pandesal.sbp.data.repository

import dev.pandesal.sbp.data.dao.ReminderDao
import dev.pandesal.sbp.data.local.toDomainModel
import dev.pandesal.sbp.data.local.toEntity
import dev.pandesal.sbp.domain.model.Reminder
import dev.pandesal.sbp.domain.repository.ReminderRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val dao: ReminderDao
) : ReminderRepositoryInterface {
    override fun getReminders(): Flow<List<Reminder>> =
        dao.getReminders().map { list -> list.map { it.toDomainModel() } }

    override fun getRemindersByDate(date: LocalDate): Flow<List<Reminder>> =
        dao.getRemindersByDate(date.toString()).map { it.map { it.toDomainModel() } }

    override suspend fun upsertReminder(reminder: Reminder) {
        dao.insert(reminder.toEntity())
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        dao.delete(reminder.toEntity())
    }
}
