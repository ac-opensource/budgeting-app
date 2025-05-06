package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.repository.CategoryRepositoryInterface
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import javax.inject.Inject

class CategoryUseCase @Inject constructor(
    private val repository: CategoryRepositoryInterface
) {

    // Category Groups
    fun getCategoryGroups(): Flow<List<CategoryGroup>> =
        repository.getCategoryGroups()

    fun getFavoriteCategoryGroups(): Flow<List<CategoryGroup>> =
        repository.getFavoriteCategoryGroups()

    fun getCategoryGroupById(id: String): Flow<CategoryGroup> =
        repository.getCategoryGroupById(id)

    suspend fun insertCategoryGroup(value: CategoryGroup) =
        repository.insertCategoryGroup(value)

    suspend fun deleteCategoryGroup(value: CategoryGroup) =
        repository.deleteCategoryGroup(value)


    // Categories
    fun getCategories(): Flow<List<Category>> =
        repository.getCategories()

    fun getFavoriteCategories(): Flow<List<Category>> =
        repository.getFavoriteCategories()

    fun getCategoryById(id: String): Flow<Category> =
        repository.getCategoryById(id)

    fun getCategoriesByGroupId(groupId: String): Flow<List<Category>> =
        repository.getCategoriesByGroupId(groupId)

    fun getFavoriteCategoriesByGroupId(groupId: String): Flow<List<Category>> =
        repository.getFavoriteCategoriesByGroupId(groupId)

    suspend fun insertCategory(value: Category) =
        repository.insertCategory(value)

    suspend fun deleteCategory(value: Category) =
        repository.deleteCategory(value)


    // Monthly Budgets
    fun getMonthlyBudgetsByYearMonth(yearMonth: YearMonth): Flow<List<MonthlyBudget>> =
        repository.getMonthlyBudgetsByYearMonth(yearMonth)

    fun getMonthlyBudgetsByCategoryId(categoryId: String): Flow<List<MonthlyBudget>> =
        repository.getMonthlyBudgetsByCategoryId(categoryId)

    fun getMonthlyBudgetByCategoryIdAndMonth(categoryId: String, yearMonth: YearMonth): Flow<MonthlyBudget> =
        repository.getMonthlyBudgetByCategoryIdAndMonth(categoryId, yearMonth)

    suspend fun insertMonthlyBudget(value: MonthlyBudget) =
        repository.insertMonthlyBudget(value)

    suspend fun deleteMonthlyBudget(value: MonthlyBudget) =
        repository.deleteMonthlyBudget(value)
}