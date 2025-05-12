package dev.pandesal.sbp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import dev.pandesal.sbp.data.local.TransactionEntity
import dev.pandesal.sbp.domain.model.CategoryTotalSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
    """)
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.transactionType = :transactionType
    """)
    fun getTransactionsByType(transactionType: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.transactionType = :transactionType AND t.accountId = :accountId
    """)
    fun getTransactionsByTypeAndAccountId(transactionType: String, accountId: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.transactionType = :transactionType AND t.category_id = :categoryId
    """)
    fun getTransactionsByTypeAndCategoryId(transactionType: String, categoryId: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.transactionType = :transactionType AND t.accountId = :accountId AND t.category_id = :categoryId
    """)
    fun getTransactionsByTypeAndAccountIdAndCategoryId(transactionType: String, accountId: String, categoryId: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.transactionType = :transactionType AND t.createdAt BETWEEN :startDate AND :endDate
    """)
    fun getTransactionsByTypeAndDateRange(transactionType: String, startDate: String, endDate: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.transactionType = :transactionType AND t.accountId = :accountId AND t.createdAt BETWEEN :startDate AND :endDate
    """)
    fun getTransactionsByTypeAndAccountIdAndDateRange(transactionType: String, accountId: String, startDate: String, endDate: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.id = :id
    """)
    fun getTransactionById(id: String): Flow<TransactionEntity>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.accountId = :accountId
    """)
    fun getTransactionsByAccountId(accountId: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.category_id = :categoryId
    """)
    fun getTransactionsByCategoryId(categoryId: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.createdAt BETWEEN :startDate AND :endDate
    """)
    fun getTransactionsByDateRange(startDate: String, endDate: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.createdAt BETWEEN :startDate AND :endDate AND t.accountId = :accountId
    """)
    fun getTransactionsByDateRangeAndAccountId(startDate: String, endDate: String, accountId: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.note LIKE '%' || :query || '%'
        ORDER BY t.createdAt DESC
    """)
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        ORDER BY t.createdAt DESC
        LIMIT :limit OFFSET :offset
    """)
    fun getPagedTransactions(limit: Int, offset: Int): Flow<List<TransactionEntity>>

    @Query("""
        SELECT t.*, 
               c.id AS category_id, 
               c.name AS category_name, 
               c.categoryGroupId AS category_categoryGroupId, 
               c.isArchived AS category_isArchived, 
               c.weight AS category_weight
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.category_id = :categoryId
        ORDER BY t.createdAt DESC
        LIMIT :limit OFFSET :offset
    """)
    fun getPagedTransactionsByCategory(
        categoryId: String,
        limit: Int,
        offset: Int
    ): Flow<List<TransactionEntity>>

    @Query("""
        SELECT category_id, SUM(amount) as total 
        FROM transactions 
        WHERE transactionType = :type 
        GROUP BY category_id
    """)
    fun getTotalAmountByCategory(type: String): Flow<List<CategoryTotalSummary>>

    @Upsert
    suspend fun insert(value: TransactionEntity)

    @Delete
    suspend fun delete(value: TransactionEntity)
}