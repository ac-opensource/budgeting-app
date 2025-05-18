package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.CategoryWithBudget
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.repository.CategoryRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import dev.pandesal.sbp.domain.model.TransactionType
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

    fun getCategoriesWithLatestBudget(): Flow<List<CategoryWithBudget>> =
        repository.getCategoriesWithLatestBudget().map { list ->
            list.filter { it.category.categoryType != TransactionType.INFLOW }
        }

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
        combine(
            repository.getMonthlyBudgetsByYearMonth(yearMonth),
            repository.getCategories()
        ) { budgets, categories ->
            budgets.filter { budget ->
                categories.firstOrNull { it.id == budget.categoryId }?.categoryType != TransactionType.INFLOW
            }
        }

    fun getMonthlyBudgetsByCategoryId(categoryId: String): Flow<List<MonthlyBudget>> =
        repository.getMonthlyBudgetsByCategoryId(categoryId)

    fun getMonthlyBudgetByCategoryIdAndMonth(categoryId: String, yearMonth: YearMonth): Flow<MonthlyBudget> =
        repository.getMonthlyBudgetByCategoryIdAndMonth(categoryId, yearMonth)

    suspend fun insertMonthlyBudget(value: MonthlyBudget) =
        repository.insertMonthlyBudget(value)

    suspend fun deleteMonthlyBudget(value: MonthlyBudget) =
        repository.deleteMonthlyBudget(value)

    suspend fun seedDefaultOutflow() {
        val groups = listOf(
            CategoryGroup(
                id = 10,
                name = "Immediate Obligations",
                description = "",
                icon = "",
                weight = 0,
                isSystemSet = true
            ),
            CategoryGroup(
                id = 11,
                name = "True Expenses",
                description = "",
                icon = "",
                weight = 1,
                isSystemSet = true
            )
        )

        val categories = listOf(
            Category(7, "Rent", "", "", 10, TransactionType.OUTFLOW, 0, isSystemSet = true),
            Category(8, "Utilities", "", "", 10, TransactionType.OUTFLOW, 1, isSystemSet = true),
            Category(9, "Groceries", "", "", 10, TransactionType.OUTFLOW, 2, isSystemSet = true),
            Category(10, "Car Maintenance", "", "", 11, TransactionType.OUTFLOW, 0, isSystemSet = true),
            Category(11, "Medical", "", "", 11, TransactionType.OUTFLOW, 1, isSystemSet = true),
            Category(12, "Clothing", "", "", 11, TransactionType.OUTFLOW, 2, isSystemSet = true)
        )

        groups.forEach { repository.insertCategoryGroup(it) }
        categories.forEach { repository.insertCategory(it) }
    }
}