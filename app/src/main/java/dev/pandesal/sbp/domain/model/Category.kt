package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.YearMonth

@Parcelize
data class CategoryGroup(
    val id: String,
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
): Parcelable

@Parcelize
data class MonthlyBudget(
    val categoryId: String,
    val month: YearMonth,
    val allocated: Double,
    val spent: Double
): Parcelable

@Parcelize
data class CategoryTotalSummary(
    val categoryId: String,
    val totalAmount: Double
): Parcelable