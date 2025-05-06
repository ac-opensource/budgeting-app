package dev.pandesal.sbp.data.repository

import dev.pandesal.sbp.data.dao.CategoryDao
import dev.pandesal.sbp.data.local.toDomainModel
import dev.pandesal.sbp.data.local.toEntity
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.CategoryWithBudget
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.repository.CategoryRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject
import kotlin.collections.map

class CategoryRepository @Inject constructor(private val dao: CategoryDao): CategoryRepositoryInterface {

    // --- Category Groups ---
    override fun getCategoryGroups(): Flow<List<CategoryGroup>> =
        dao.getAllCategoryGroups().map { it.map { entity -> entity.toDomainModel() } }

    override fun getFavoriteCategoryGroups(): Flow<List<CategoryGroup>> =
        dao.getFavoriteCategoryGroups().map { it.map { entity -> entity.toDomainModel() } }

    override fun getCategoryGroupById(id: String): Flow<CategoryGroup> =
        dao.getCategoryGroupById(id).map { it.toDomainModel() }

    override suspend fun insertCategoryGroup(value: CategoryGroup) =
        dao.insert(value.toEntity())

    override suspend fun deleteCategoryGroup(value: CategoryGroup) =
        dao.delete(value.toEntity())

    // --- Categories ---
    override fun getCategories(): Flow<List<Category>> =
        dao.getCategories().map { it.map { entity -> entity.toDomainModel() } }

    override fun getFavoriteCategories(): Flow<List<Category>> =
        dao.getFavoriteCategories().map { it.map { entity -> entity.toDomainModel() } }

    override fun getCategoryById(id: String): Flow<Category> =
        dao.getCategoryById(id).map { it.toDomainModel() }

    override fun getCategoriesByGroupId(categoryGroupId: String): Flow<List<Category>> =
        dao.getCategoriesByCategoryGroupId(categoryGroupId).map { it.map { entity -> entity.toDomainModel() } }

    override fun getFavoriteCategoriesByGroupId(categoryGroupId: String): Flow<List<Category>> =
        dao.getFavoriteCategoriesByCategoryGroupId(categoryGroupId).map { it.map { entity -> entity.toDomainModel() } }

    override fun getCategoriesWithLatestBudget(): Flow<List<CategoryWithBudget>> =
        dao.getCategoriesWithLatestBudget().map {
            it.map { entity -> entity.toDomain() }
    }

    override suspend fun insertCategory(value: Category) =
        dao.insert(value.toEntity())

    override suspend fun deleteCategory(value: Category) =
        dao.delete(value.toEntity())

    // --- Monthly Budgets ---
    override fun getMonthlyBudgetsByYearMonth(yearMonth: YearMonth): Flow<List<MonthlyBudget>> =
        dao.getMonthlyBudgetsByYearMonth(yearMonth.toString())
            .map { it.map { entity -> entity.toDomainModel() } }

    override fun getMonthlyBudgetsByCategoryId(categoryId: String): Flow<List<MonthlyBudget>> =
        dao.getMonthlyBudgetsByCategoryId(categoryId)
            .map { it.map { entity -> entity.toDomainModel() } }

    override fun getMonthlyBudgetByCategoryIdAndMonth(
        categoryId: String,
        yearMonth: YearMonth
    ): Flow<MonthlyBudget> =
        dao.getMonthlyBudgetByCategoryIdAndYearMonth(categoryId, yearMonth.toString())
            .map { it.toDomainModel() }

    override suspend fun insertMonthlyBudget(value: MonthlyBudget) =
        dao.insert(value.toEntity())

    override suspend fun deleteMonthlyBudget(value: MonthlyBudget) =
        dao.delete(value.toEntity())
}