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
    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transactionType = :transactionType")
    fun getTransactionsByType(transactionType: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transactionType = :transactionType AND accountId = :accountId")
    fun getTransactionsByTypeAndAccountId(transactionType: String, accountId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transactionType = :transactionType AND categoryId = :categoryId")
    fun getTransactionsByTypeAndCategoryId(transactionType: String, categoryId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transactionType = :transactionType AND accountId = :accountId AND categoryId = :categoryId")
    fun getTransactionsByTypeAndAccountIdAndCategoryId(transactionType: String, accountId: String, categoryId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transactionType = :transactionType AND date BETWEEN :startDate AND :endDate")
    fun getTransactionsByTypeAndDateRange(transactionType: String, startDate: String, endDate: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transactionType = :transactionType AND accountId = :accountId AND date BETWEEN :startDate AND :endDate")
    fun getTransactionsByTypeAndAccountIdAndDateRange(transactionType: String, accountId: String, startDate: String, endDate: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionById(id: String): Flow<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId")
    fun getTransactionsByAccountId(accountId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId")
    fun getTransactionsByCategoryId(categoryId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate")
    fun getTransactionsByDateRange(startDate: String, endDate: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate AND accountId = :accountId")
    fun getTransactionsByDateRangeAndAccountId(startDate: String, endDate: String, accountId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE note LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit OFFSET :offset")
    fun getPagedTransactions(limit: Int, offset: Int): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions 
        WHERE categoryId = :categoryId 
        ORDER BY date DESC 
        LIMIT :limit OFFSET :offset
    """)
    fun getPagedTransactionsByCategory(
        categoryId: String,
        limit: Int,
        offset: Int
    ): Flow<List<TransactionEntity>>

    @Query("""
        SELECT categoryId, SUM(amount) as total 
        FROM transactions 
        WHERE transactionType = :type 
        GROUP BY categoryId
    """)
    fun getTotalAmountByCategory(type: String): Flow<List<CategoryTotalSummary>>

    @Upsert
    suspend fun insert(value: TransactionEntity)

    @Delete
    suspend fun delete(value: TransactionEntity)
}