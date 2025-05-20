package dev.pandesal.sbp.domain.repository

import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

interface TransactionRepositoryInterface {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>
    fun getTransactionsByTypeAndAccountId(type: TransactionType, accountId: String): Flow<List<Transaction>>
    fun getTransactionsByTypeAndCategoryId(type: TransactionType, categoryId: String): Flow<List<Transaction>>
    fun getTransactionsByTypeAndAccountIdAndCategoryId(type: TransactionType, accountId: String, categoryId: String): Flow<List<Transaction>>
    fun getTransactionsByTypeAndDateRange(type: TransactionType, startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>>
    fun getTransactionsByTypeAndAccountIdAndDateRange(type: TransactionType, accountId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>>
    fun getTransactionById(id: String): Flow<Transaction?>
    fun getTransactionsByAccountId(accountId: String): Flow<List<Transaction>>
    fun getTransactionsByCategoryId(categoryId: String): Flow<List<Transaction>>
    fun getTransactionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>>
    fun getTransactionsByDateRangeAndAccountId(startDate: LocalDate, endDate: LocalDate, accountId: String): Flow<List<Transaction>>
    fun searchTransactions(query: String): Flow<List<Transaction>>
    fun getPagedTransactions(limit: Int, offset: Int): Flow<List<Transaction>>
    fun getPagedTransactionsByCategory(categoryId: String, limit: Int, offset: Int): Flow<List<Transaction>>
    fun getTotalAmountByCategory(type: TransactionType): Flow<List<Pair<Int, BigDecimal>>>
    fun getMerchantsByCategoryId(categoryId: String): Flow<List<String>>
    suspend fun insert(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
}