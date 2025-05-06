package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.time.YearMonth

@Parcelize
data class CategoryGroup(
    val id: Int = 0,
    val name: String,
    val description: String,
    val icon: String,
    val weight: Int = 0,
    val isSystemSet: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false
): Parcelable

@Parcelize
data class Category(
    val id: Int = 0,
    val name: String,
    val description: String,
    val icon: String,
    val categoryGroupId: Int,
    val categoryType: TransactionType,
    val weight: Int,
    val isSystemSet: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
): Parcelable

@Parcelize
data class MonthlyBudget(
    val id: Int = 0,
    val categoryId: Int,
    val month: YearMonth,
    val allocated: BigDecimal,
    val spent: BigDecimal
): Parcelable

@Parcelize
data class CategoryTotalSummary(
    val categoryId: Int,
    val total: BigDecimal
): Parcelable