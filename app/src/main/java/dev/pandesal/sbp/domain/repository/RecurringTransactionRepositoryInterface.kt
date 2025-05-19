package dev.pandesal.sbp.domain.repository

import dev.pandesal.sbp.domain.model.RecurringTransaction
import kotlinx.coroutines.flow.Flow

interface RecurringTransactionRepositoryInterface {
    fun getRecurringTransactions(): Flow<List<RecurringTransaction>>
    fun getRecurringTransactionById(id: String): Flow<RecurringTransaction>
    suspend fun addRecurringTransaction(transaction: RecurringTransaction)
    suspend fun removeRecurringTransaction(transaction: RecurringTransaction)
}
