package dev.pandesal.sbp.data.repository

import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.domain.repository.RecurringTransactionRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurringTransactionRepository @Inject constructor() : RecurringTransactionRepositoryInterface {
    private val recurringTransactions = MutableStateFlow<List<RecurringTransaction>>(emptyList())

    override fun getRecurringTransactions(): StateFlow<List<RecurringTransaction>> = recurringTransactions.asStateFlow()

    override suspend fun addRecurringTransaction(transaction: RecurringTransaction) {
        recurringTransactions.value = recurringTransactions.value + transaction
    }

    override suspend fun removeRecurringTransaction(transaction: RecurringTransaction) {
        recurringTransactions.value = recurringTransactions.value - transaction
    }
}
