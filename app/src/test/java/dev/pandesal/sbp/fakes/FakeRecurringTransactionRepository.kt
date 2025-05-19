package dev.pandesal.sbp.fakes

import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.domain.repository.RecurringTransactionRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeRecurringTransactionRepository : RecurringTransactionRepositoryInterface {
    val recurringFlow = MutableStateFlow<List<RecurringTransaction>>(emptyList())
    val inserted = mutableListOf<RecurringTransaction>()

    override fun getRecurringTransactions(): Flow<List<RecurringTransaction>> = recurringFlow

    override suspend fun addRecurringTransaction(transaction: RecurringTransaction) {
        inserted.add(transaction)
    }

    override suspend fun removeRecurringTransaction(transaction: RecurringTransaction) {}
}
