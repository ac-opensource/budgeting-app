package dev.pandesal.sbp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import dev.pandesal.sbp.data.local.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders")
    fun getReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE date = :date")
    fun getRemindersByDate(date: String): Flow<List<ReminderEntity>>

    @Upsert
    suspend fun insert(value: ReminderEntity)

    @Delete
    suspend fun delete(value: ReminderEntity)
}
