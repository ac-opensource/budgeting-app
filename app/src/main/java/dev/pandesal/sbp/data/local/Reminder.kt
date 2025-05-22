package dev.pandesal.sbp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.pandesal.sbp.domain.model.Reminder
import java.time.LocalDate

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val date: String,
    val message: String
)

fun ReminderEntity.toDomainModel() = Reminder(
    id = id,
    date = LocalDate.parse(date),
    message = message
)

fun Reminder.toEntity() = ReminderEntity(
    id = id,
    date = date.toString(),
    message = message
)
