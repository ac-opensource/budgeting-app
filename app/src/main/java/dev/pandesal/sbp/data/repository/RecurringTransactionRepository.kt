package dev.pandesal.sbp.data.repository

import dev.pandesal.sbp.data.dao.RecurringTransactionDao
import dev.pandesal.sbp.data.local.toDomainModel
import dev.pandesal.sbp.data.local.toEntity
import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.domain.repository.RecurringTransactionRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurringTransactionRepository @Inject constructor(
    private val dao: RecurringTransactionDao
) : RecurringTransactionRepositoryInterface {
    override fun getRecurringTransactions(): Flow<List<RecurringTransaction>> =
        dao.getRecurringTransactions().map { list -> list.map { it.toDomainModel() } }

    override fun getRecurringTransactionById(id: String): Flow<RecurringTransaction> =
        dao.getRecurringTransactionById(id).map { it.toDomainModel() }

    override suspend fun addRecurringTransaction(transaction: RecurringTransaction) {
        dao.insert(transaction.toEntity())
    }

    override suspend fun removeRecurringTransaction(transaction: RecurringTransaction) {
        dao.delete(transaction.toEntity())
    }
}
