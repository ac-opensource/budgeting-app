package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.repository.TransactionRepositoryInterface
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class TransactionUseCase @Inject constructor(
    private val repository: TransactionRepositoryInterface
) {
    fun getAllTransactions(): Flow<List<Transaction>> =
        repository.getAllTransactions()

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        repository.getTransactionsByType(type)

    fun getTransactionsByTypeAndAccountId(type: TransactionType, accountId: String): Flow<List<Transaction>> =
        repository.getTransactionsByTypeAndAccountId(type, accountId)

    fun getTransactionsByTypeAndCategoryId(type: TransactionType, categoryId: String): Flow<List<Transaction>> =
        repository.getTransactionsByTypeAndCategoryId(type, categoryId)

    fun getTransactionsByTypeAndAccountIdAndCategoryId(type: TransactionType, accountId: String, categoryId: String): Flow<List<Transaction>> =
        repository.getTransactionsByTypeAndAccountIdAndCategoryId(type, accountId, categoryId)

    fun getTransactionsByTypeAndDateRange(type: TransactionType, startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>> =
        repository.getTransactionsByTypeAndDateRange(type, startDate, endDate)

    fun getTransactionsByTypeAndAccountIdAndDateRange(type: TransactionType, accountId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>> =
        repository.getTransactionsByTypeAndAccountIdAndDateRange(type, accountId, startDate, endDate)

    fun getTransactionById(id: String): Flow<Transaction> =
        repository.getTransactionById(id)

    fun getTransactionsByAccountId(accountId: String): Flow<List<Transaction>> =
        repository.getTransactionsByAccountId(accountId)

    fun getTransactionsByCategoryId(categoryId: String): Flow<List<Transaction>> =
        repository.getTransactionsByCategoryId(categoryId)

    fun getTransactionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>> =
        repository.getTransactionsByDateRange(startDate, endDate)

    fun getTransactionsByDateRangeAndAccountId(startDate: LocalDate, endDate: LocalDate, accountId: String): Flow<List<Transaction>> =
        repository.getTransactionsByDateRangeAndAccountId(startDate, endDate, accountId)

    fun searchTransactions(query: String): Flow<List<Transaction>> =
        repository.searchTransactions(query)

    fun getPagedTransactions(limit: Int, offset: Int): Flow<List<Transaction>> =
        repository.getPagedTransactions(limit, offset)

    fun getPagedTransactionsByCategory(categoryId: String, limit: Int, offset: Int): Flow<List<Transaction>> =
        repository.getPagedTransactionsByCategory(categoryId, limit, offset)

    fun getTotalAmountByCategory(type: TransactionType): Flow<List<Pair<Int, BigDecimal>>> =
        repository.getTotalAmountByCategory(type)

    suspend fun insert(transaction: Transaction) =
        repository.insert(transaction)

    suspend fun delete(transaction: Transaction) =
        repository.delete(transaction)
}