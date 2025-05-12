package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
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

@Serializable
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
    @ColumnInfo(name = "category_id")
    val categoryId: Int,
    val total: BigDecimal
): Parcelable

@Parcelize
data class CategoryWithBudget(
    val category: Category,
    val budget: MonthlyBudget?
): Parcelable