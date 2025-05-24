package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.repository.TransactionRepositoryInterface
import dev.pandesal.sbp.domain.repository.AccountRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class TransactionUseCase @Inject constructor(
    private val repository: TransactionRepositoryInterface,
    private val accountRepository: AccountRepositoryInterface
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

    fun getTransactionById(id: String): Flow<Transaction?> =
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

    fun getMerchantsByCategoryId(categoryId: String): Flow<List<String>> =
        repository.getMerchantsByCategoryId(categoryId)

    suspend fun getLastMerchantForCategory(categoryId: String): String? =
        repository.getLastMerchantForCategory(categoryId)

    suspend fun insert(transaction: Transaction) {
        repository.insert(transaction)

        val accounts = accountRepository.getAccounts().first()
        when (transaction.transactionType) {
            TransactionType.OUTFLOW -> transaction.from?.let { id ->
                accounts.firstOrNull { it.id == id }?.let { account ->
                    accountRepository.insertAccount(
                        account.copy(balance = account.balance - transaction.amount)
                    )
                }
            }
            TransactionType.INFLOW -> transaction.to?.let { id ->
                accounts.firstOrNull { it.id == id }?.let { account ->
                    accountRepository.insertAccount(
                        account.copy(balance = account.balance + transaction.amount)
                    )
                }
            }
            TransactionType.TRANSFER -> {
                transaction.from?.let { fromId ->
                    accounts.firstOrNull { it.id == fromId }?.let { account ->
                        accountRepository.insertAccount(
                            account.copy(balance = account.balance - transaction.amount)
                        )
                    }
                }
                transaction.to?.let { toId ->
                    accounts.firstOrNull { it.id == toId }?.let { account ->
                        accountRepository.insertAccount(
                            account.copy(balance = account.balance + transaction.amount)
                        )
                    }
                }
            }
            TransactionType.ADJUSTMENT -> {
                transaction.from?.let { fromId ->
                    accounts.firstOrNull { it.id == fromId }?.let { account ->
                        accountRepository.insertAccount(
                            account.copy(balance = account.balance - transaction.amount)
                        )
                    }
                }
                transaction.to?.let { toId ->
                    accounts.firstOrNull { it.id == toId }?.let { account ->
                        accountRepository.insertAccount(
                            account.copy(balance = account.balance + transaction.amount)
                        )
                    }
                }
            }
            else -> {}
        }
    }

    suspend fun delete(transaction: Transaction) =
        repository.delete(transaction)
}