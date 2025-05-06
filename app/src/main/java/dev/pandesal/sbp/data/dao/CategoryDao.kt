package dev.pandesal.sbp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import dev.pandesal.sbp.data.local.CategoryEntity
import dev.pandesal.sbp.data.local.CategoryGroupEntity
import dev.pandesal.sbp.data.local.CategoryWithBudgetRaw
import dev.pandesal.sbp.data.local.MonthlyBudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category_groups WHERE isArchived = 0 ORDER BY weight ASC")
    fun getAllCategoryGroups(): Flow<List<CategoryGroupEntity>>

    @Query("SELECT * FROM categories WHERE isArchived = 0 ORDER BY weight ASC")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: String): Flow<CategoryEntity>

    @Query("SELECT * FROM category_groups WHERE id = :id")
    fun getCategoryGroupById(id: String): Flow<CategoryGroupEntity>

    @Query("SELECT * FROM categories WHERE categoryGroupId = :categoryGroupId")
    fun getCategoriesByCategoryGroupId(categoryGroupId: String): Flow<List<CategoryEntity>>

    @Query("""
        SELECT 
            c.*, 
            mb.id AS mb_id, 
            mb.yearMonth, 
            mb.allocated, 
            mb.spent
        FROM categories c
        LEFT JOIN monthly_budgets mb 
            ON mb.categoryId = c.id 
            AND mb.yearMonth = (
                SELECT MAX(yearMonth) FROM monthly_budgets WHERE categoryId = c.id
            )
    """)
    fun getCategoriesWithLatestBudget(): Flow<List<CategoryWithBudgetRaw>>

    @Query("SELECT * FROM category_groups WHERE isArchived = 0 AND isFavorite = 1 ORDER BY weight ASC")
    fun getFavoriteCategoryGroups(): Flow<List<CategoryGroupEntity>>

    @Query("SELECT * FROM categories WHERE isArchived = 0 AND isFavorite = 1 ORDER BY weight ASC")
    fun getFavoriteCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE categoryGroupId = :categoryGroupId AND isArchived = 0 AND isFavorite = 1 ORDER BY weight ASC")
    fun getFavoriteCategoriesByCategoryGroupId(categoryGroupId: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM monthly_budgets WHERE yearMonth = :yearMonth")
    fun getMonthlyBudgetsByYearMonth(yearMonth: String): Flow<List<MonthlyBudgetEntity>>

    @Query("SELECT * FROM monthly_budgets WHERE categoryId = :categoryId")
    fun getMonthlyBudgetsByCategoryId(categoryId: String): Flow<List<MonthlyBudgetEntity>>

    @Query("SELECT * FROM monthly_budgets WHERE categoryId = :categoryId AND yearMonth = :yearMonth")
    fun getMonthlyBudgetByCategoryIdAndYearMonth(categoryId: String, yearMonth: String): Flow<MonthlyBudgetEntity>

    @Upsert
    suspend fun insert(value: CategoryGroupEntity)

    @Upsert
    suspend fun insert(value: CategoryEntity)

    @Upsert
    suspend fun insert(value: MonthlyBudgetEntity)

    @Delete
    suspend fun delete(value: CategoryEntity)

    @Delete
    suspend fun delete(value: CategoryGroupEntity)

    @Delete
    suspend fun delete(value: MonthlyBudgetEntity)


}