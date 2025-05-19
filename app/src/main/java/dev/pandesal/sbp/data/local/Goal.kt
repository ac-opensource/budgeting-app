package dev.pandesal.sbp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.pandesal.sbp.domain.model.Goal
import java.math.BigDecimal
import java.time.LocalDate

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val target: BigDecimal,
    val current: BigDecimal,
    val dueDate: String?,
    val categoryId: Int?
)

fun GoalEntity.toDomainModel(): Goal {
    return Goal(
        id = id,
        name = name,
        target = target,
        current = current,
        dueDate = dueDate?.let { LocalDate.parse(it) },
        categoryId = categoryId
    )
}

fun Goal.toEntity(): GoalEntity {
    return GoalEntity(
        id = id,
        name = name,
        target = target,
        current = current,
        dueDate = dueDate?.toString(),
        categoryId = categoryId
    )
}
