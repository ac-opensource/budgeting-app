package dev.pandesal.sbp.domain.repository

import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.CategoryWithBudget
import dev.pandesal.sbp.domain.model.MonthlyBudget
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

interface CategoryRepositoryInterface {
    // Category Groups
    fun getCategoryGroups(): Flow<List<CategoryGroup>>
    fun getFavoriteCategoryGroups(): Flow<List<CategoryGroup>>
    fun getCategoryGroupById(id: String): Flow<CategoryGroup>
    suspend fun insertCategoryGroup(value: CategoryGroup)
    suspend fun deleteCategoryGroup(value: CategoryGroup)

    // Categories
    fun getCategories(): Flow<List<Category>>
    fun getFavoriteCategories(): Flow<List<Category>>
    fun getCategoryById(id: String): Flow<Category>
    fun getCategoriesByGroupId(categoryGroupId: String): Flow<List<Category>>
    fun getFavoriteCategoriesByGroupId(categoryGroupId: String): Flow<List<Category>>
    fun getCategoriesWithLatestBudget(): Flow<List<CategoryWithBudget>>
    suspend fun insertCategory(value: Category)
    suspend fun deleteCategory(value: Category)

    // Monthly Budgets
    fun getMonthlyBudgetsByYearMonth(yearMonth: YearMonth): Flow<List<MonthlyBudget>>
    fun getMonthlyBudgetsByCategoryId(categoryId: String): Flow<List<MonthlyBudget>>
    fun getMonthlyBudgetByCategoryIdAndMonth(categoryId: Int, yearMonth: YearMonth): Flow<MonthlyBudget?>
    suspend fun insertMonthlyBudget(value: MonthlyBudget)
    suspend fun deleteMonthlyBudget(value: MonthlyBudget)
}