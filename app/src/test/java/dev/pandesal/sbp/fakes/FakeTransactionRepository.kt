package dev.pandesal.sbp.fakes

import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.repository.TransactionRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal
import java.time.LocalDate

class FakeTransactionRepository : TransactionRepositoryInterface {
    val transactionsFlow = MutableStateFlow<List<Transaction>>(emptyList())
    val pagedFlow = MutableStateFlow<List<Transaction>>(emptyList())
    val categoryFlow = MutableStateFlow<List<Transaction>>(emptyList())
    val merchantsFlow = MutableStateFlow<List<String>>(emptyList())
    val insertedTransactions = mutableListOf<Transaction>()

    override fun getAllTransactions(): Flow<List<Transaction>> = transactionsFlow
    override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> = flowOf(emptyList())
    override fun getTransactionsByTypeAndAccountId(type: TransactionType, accountId: String): Flow<List<Transaction>> = flowOf(emptyList())
    override fun getTransactionsByTypeAndCategoryId(type: TransactionType, categoryId: String): Flow<List<Transaction>> = flowOf(emptyList())
    override fun getTransactionsByTypeAndAccountIdAndCategoryId(type: TransactionType, accountId: String, categoryId: String): Flow<List<Transaction>> = flowOf(emptyList())
    override fun getTransactionsByTypeAndDateRange(type: TransactionType, startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>> = flowOf(emptyList())
    override fun getTransactionsByTypeAndAccountIdAndDateRange(type: TransactionType, accountId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>> = flowOf(emptyList())
    override fun getTransactionById(id: String): Flow<Transaction?> = flowOf(transactionsFlow.value.firstOrNull { it.id == id })
    override fun getTransactionsByAccountId(accountId: String): Flow<List<Transaction>> = flowOf(emptyList())
    override fun getTransactionsByCategoryId(categoryId: String): Flow<List<Transaction>> = categoryFlow
    override fun getTransactionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>> = flowOf(emptyList())
    override fun getTransactionsByDateRangeAndAccountId(startDate: LocalDate, endDate: LocalDate, accountId: String): Flow<List<Transaction>> = flowOf(emptyList())
    override fun searchTransactions(query: String): Flow<List<Transaction>> = flowOf(emptyList())
    override fun getPagedTransactions(limit: Int, offset: Int): Flow<List<Transaction>> = pagedFlow
    override fun getPagedTransactionsByCategory(categoryId: String, limit: Int, offset: Int): Flow<List<Transaction>> = flowOf(emptyList())
    override fun getTotalAmountByCategory(type: TransactionType): Flow<List<Pair<Int, BigDecimal>>> = flowOf(emptyList())
    override fun getMerchantsByCategoryId(categoryId: String): Flow<List<String>> = merchantsFlow
    override suspend fun getLastMerchantForCategory(categoryId: String): String? = merchantsFlow.value.lastOrNull()
    override suspend fun insert(transaction: Transaction) { insertedTransactions.add(transaction) }
    override suspend fun delete(transaction: Transaction) {}
}
