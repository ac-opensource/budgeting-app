package dev.pandesal.sbp.fakes

import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.domain.repository.RecurringTransactionRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeRecurringTransactionRepository : RecurringTransactionRepositoryInterface {
    val transactionsFlow = MutableStateFlow<List<RecurringTransaction>>(emptyList())
    val inserted = mutableListOf<RecurringTransaction>()
    val deleted = mutableListOf<RecurringTransaction>()

    override fun getRecurringTransactions(): Flow<List<RecurringTransaction>> = transactionsFlow

    override fun getRecurringTransactionById(id: String): Flow<RecurringTransaction> =
        flowOf(transactionsFlow.value.first { it.transaction.id == id })

    override suspend fun addRecurringTransaction(transaction: RecurringTransaction) {
        inserted.add(transaction)
    }

    override suspend fun removeRecurringTransaction(transaction: RecurringTransaction) {
        deleted.add(transaction)
    }
}
