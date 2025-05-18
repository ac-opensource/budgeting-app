package dev.pandesal.sbp.fakes

import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.CategoryWithBudget
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.repository.CategoryRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import java.time.YearMonth

class FakeCategoryRepository : CategoryRepositoryInterface {
    val groupsFlow = MutableStateFlow<List<CategoryGroup>>(emptyList())
    val categoriesFlow = MutableStateFlow<List<Category>>(emptyList())
    val categoriesWithBudgetFlow = MutableStateFlow<List<CategoryWithBudget>>(emptyList())
    val monthlyBudgetsFlow = MutableStateFlow<List<MonthlyBudget>>(emptyList())

    val insertedGroups = mutableListOf<CategoryGroup>()
    val insertedCategories = mutableListOf<Category>()
    val insertedBudgets = mutableListOf<MonthlyBudget>()

    override fun getCategoryGroups(): Flow<List<CategoryGroup>> = groupsFlow
    override fun getFavoriteCategoryGroups(): Flow<List<CategoryGroup>> = flowOf(emptyList())
    override fun getCategoryGroupById(id: String): Flow<CategoryGroup> = flowOf(groupsFlow.value.first { it.id.toString() == id })
    override suspend fun insertCategoryGroup(value: CategoryGroup) { insertedGroups.add(value) }
    override suspend fun deleteCategoryGroup(value: CategoryGroup) {}

    override fun getCategories(): Flow<List<Category>> = categoriesFlow
    override fun getFavoriteCategories(): Flow<List<Category>> = flowOf(emptyList())
    override fun getCategoryById(id: String): Flow<Category> = flowOf(categoriesFlow.value.first { it.id.toString() == id })
    override fun getCategoriesByGroupId(groupId: String): Flow<List<Category>> = flowOf(categoriesFlow.value.filter { it.categoryGroupId.toString() == groupId })
    override fun getFavoriteCategoriesByGroupId(groupId: String): Flow<List<Category>> = flowOf(emptyList())
    override fun getCategoriesWithLatestBudget(): Flow<List<CategoryWithBudget>> = categoriesWithBudgetFlow
    override suspend fun insertCategory(value: Category) { insertedCategories.add(value) }
    override suspend fun deleteCategory(value: Category) {}

    override fun getMonthlyBudgetsByYearMonth(yearMonth: YearMonth): Flow<List<MonthlyBudget>> = monthlyBudgetsFlow
    override fun getMonthlyBudgetsByCategoryId(categoryId: String): Flow<List<MonthlyBudget>> = flowOf(emptyList())
    override fun getMonthlyBudgetByCategoryIdAndMonth(categoryId: String, yearMonth: YearMonth): Flow<MonthlyBudget> = flowOf(monthlyBudgetsFlow.value.first())
    override suspend fun insertMonthlyBudget(value: MonthlyBudget) { insertedBudgets.add(value) }
    override suspend fun deleteMonthlyBudget(value: MonthlyBudget) {}
}
