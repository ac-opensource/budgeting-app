package dev.pandesal.sbp.data.repository

import dev.pandesal.sbp.data.dao.TransactionDao
import dev.pandesal.sbp.data.dao.CategoryDao
import dev.pandesal.sbp.data.local.toDomainModel
import dev.pandesal.sbp.data.local.toEntity
import dev.pandesal.sbp.data.local.MonthlyBudgetEntity
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.model.TagSummary
import dev.pandesal.sbp.domain.repository.TransactionRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val dao: TransactionDao,
    private val categoryDao: CategoryDao
) : TransactionRepositoryInterface {

    override fun getAllTransactions(): Flow<List<Transaction>> =
        dao.getAllTransactions().map { it.map { entity -> entity.toDomainModel() } }

    override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        dao.getTransactionsByType(type.name).map { it.map { entity -> entity.toDomainModel() } }

    override fun getTransactionsByTypeAndAccountId(
        type: TransactionType,
        accountId: String
    ): Flow<List<Transaction>> =
        dao.getTransactionsByTypeAndAccountId(type.name, accountId)
            .map { it.map { it.toDomainModel() } }

    override fun getTransactionsByTypeAndCategoryId(
        type: TransactionType,
        categoryId: String
    ): Flow<List<Transaction>> =
        dao.getTransactionsByTypeAndCategoryId(type.name, categoryId)
            .map { it.map { it.toDomainModel() } }

    override fun getTransactionsByTypeAndAccountIdAndCategoryId(
        type: TransactionType,
        accountId: String,
        categoryId: String
    ): Flow<List<Transaction>> =
        dao.getTransactionsByTypeAndAccountIdAndCategoryId(type.name, accountId, categoryId)
            .map { it.map { it.toDomainModel() } }

    override fun getTransactionsByTypeAndDateRange(
        type: TransactionType,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<Transaction>> =
        dao.getTransactionsByTypeAndDateRange(type.name, startDate.toString(), endDate.toString())
            .map { it.map { it.toDomainModel() } }

    override fun getTransactionsByTypeAndAccountIdAndDateRange(
        type: TransactionType,
        accountId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<Transaction>> =
        dao.getTransactionsByTypeAndAccountIdAndDateRange(
            type.name,
            accountId,
            startDate.toString(),
            endDate.toString()
        ).map { it.map { it.toDomainModel() } }

    override fun getTransactionById(id: String): Flow<Transaction?> =
        dao.getTransactionById(id).map { it?.toDomainModel() }

    override fun getTransactionsByAccountId(accountId: String): Flow<List<Transaction>> =
        dao.getTransactionsByAccountId(accountId).map { it.map { it.toDomainModel() } }

    override fun getTransactionsByCategoryId(categoryId: String): Flow<List<Transaction>> =
        dao.getTransactionsByCategoryId(categoryId).map { it.map { it.toDomainModel() } }

    override fun getTransactionsByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<Transaction>> =
        dao.getTransactionsByDateRange(startDate.toString(), endDate.toString())
            .map { it.map { it.toDomainModel() } }

    override fun getTransactionsByDateRangeAndAccountId(
        startDate: LocalDate,
        endDate: LocalDate,
        accountId: String
    ): Flow<List<Transaction>> =
        dao.getTransactionsByDateRangeAndAccountId(
            startDate.toString(),
            endDate.toString(),
            accountId
        )
            .map { it.map { it.toDomainModel() } }

    override fun searchTransactions(query: String): Flow<List<Transaction>> =
        dao.searchTransactions(query).map { it.map { it.toDomainModel() } }

    override fun getPagedTransactions(limit: Int, offset: Int): Flow<List<Transaction>> =
        dao.getPagedTransactions(limit, offset).map { it.map { it.toDomainModel() } }

    override fun getPagedTransactionsByCategory(
        categoryId: String,
        limit: Int,
        offset: Int
    ): Flow<List<Transaction>> =
        dao.getPagedTransactionsByCategory(categoryId, limit, offset)
            .map { it.map { it.toDomainModel() } }

    override fun getTotalAmountByCategory(type: TransactionType): Flow<List<Pair<Int, BigDecimal>>> =
        dao.getTotalAmountByCategory(type.name)
            .map { list -> list.map { it.categoryId to it.total } }

    override fun getMerchantsByCategoryId(categoryId: String): Flow<List<String>> =
        dao.getMerchantsByCategoryId(categoryId)

    override fun getTags(): Flow<List<String>> =
        dao.getAllTransactions().map { list ->
            list.flatMap { it.tags }.distinct()
        }

    override fun getTotalAmountByTag(type: TransactionType): Flow<List<TagSummary>> =
        dao.getAllTransactions().map { list ->
            list.filter { it.transactionType == type }
                .flatMap { tx -> tx.tags.map { it to tx.amount } }
                .groupBy({ it.first }, { it.second })
                .map { TagSummary(it.key, it.value.fold(BigDecimal.ZERO, BigDecimal::add)) }
        }

    override suspend fun getLastMerchantForCategory(categoryId: String): String? =
        dao.getLastMerchantForCategory(categoryId)

    override suspend fun insert(transaction: Transaction) {
        dao.insert(transaction.toEntity())

        val category = transaction.category ?: return
        val month = YearMonth.from(transaction.createdAt)

        val amount = when (transaction.transactionType) {
            TransactionType.INFLOW -> transaction.amount.negate()
            TransactionType.OUTFLOW -> transaction.amount
            else -> BigDecimal.ZERO
        }
        if (amount == BigDecimal.ZERO) return

        val current = categoryDao
            .getMonthlyBudgetByCategoryIdAndYearMonth(category.id, month.toString())

        if (current != null) {
            categoryDao.insert(current.copy(spent = current.spent + amount))
        } else {
            val previous = categoryDao.getMonthlyBudgetsByCategoryId(category.id.toString())
                .firstOrNull()
                ?.maxByOrNull { YearMonth.parse(it.yearMonth) }

            val allocated = previous?.allocated ?: BigDecimal.ZERO
            val newBudget = MonthlyBudgetEntity(
                categoryId = category.id,
                yearMonth = month.toString(),
                allocated = allocated,
                spent = amount,
                currency = transaction.currency
            )
            categoryDao.insert(newBudget)
        }
    }

    override suspend fun delete(transaction: Transaction) =
        dao.delete(transaction.toEntity())
}