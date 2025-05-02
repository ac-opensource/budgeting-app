package dev.pandesal.sbp.domain.model

import java.time.YearMonth

data class CategoryGroup(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val weight: Int = 0,
    val isSystemSet: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false
)

data class Category(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val categoryGroupId: String,
    val categoryType: TransactionType,
    val weight: Int,
    val isSystemSet: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
)

data class MonthlyBudget(
    val categoryId: String,
    val month: YearMonth,
    val allocated: Double,
    val spent: Double
)

data class CategoryTotalSummary(
    val categoryId: String,
    val totalAmount: Double
)