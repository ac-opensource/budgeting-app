package dev.pandesal.sbp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.model.TransactionType
import java.time.YearMonth
import java.util.UUID

@Entity(tableName = "category_groups")
data class CategoryGroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String = "",
    val icon: String,
    val categoryGroupId: String = "",
    val weight: Int = 0,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isSystemSet: Boolean = false
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val icon: String,
    val description: String = "",
    val categoryGroupId: String = "",
    val categoryType: String,
    val weight: Int = 0,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isSystemSet: Boolean = false,
)

@Entity(tableName = "monthly_budgets")
data class MonthlyBudgetEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val yearMonth: String,
    val allocated: Double,
    val spent: Double
)

fun CategoryGroup.toEntity(): CategoryGroupEntity {
    return CategoryGroupEntity(
        id = id,
        name = name,
        icon = icon,
        weight = weight,
        isFavorite = isFavorite,
        description = description,

        )
}

fun CategoryGroupEntity.toDomainModel(): CategoryGroup {
    return CategoryGroup(
        id = id,
        name = name,
        icon = icon,
        weight = weight,
        isFavorite = isFavorite,
        description = description
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        icon = icon,
        categoryGroupId = categoryGroupId,
        weight = weight,
        isFavorite = isFavorite,
        description = description,
        categoryType = categoryType.name
    )
}

fun CategoryEntity.toDomainModel(): Category {
    return Category(
        id = id,
        name = name,
        icon = icon,
        categoryGroupId = categoryGroupId,
        weight = weight,
        isFavorite = isFavorite,
        description = description,
        categoryType = TransactionType.valueOf(categoryType)
    )
}

fun MonthlyBudget.toEntity(): MonthlyBudgetEntity {
    return MonthlyBudgetEntity(
        id = UUID.randomUUID().toString(),
        categoryId = categoryId,
        yearMonth = month.toString(),
        allocated = allocated,
        spent = spent,
    )
}

fun MonthlyBudgetEntity.toDomainModel(): MonthlyBudget {
    return MonthlyBudget(
        categoryId = categoryId,
        month = YearMonth.parse(yearMonth),
        allocated = allocated,
        spent = spent,
    )
}

