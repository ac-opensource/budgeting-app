package dev.pandesal.sbp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import dev.pandesal.sbp.data.local.RecurringTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringTransactionDao {
    @Query("SELECT * FROM recurring_transactions")
    fun getRecurringTransactions(): Flow<List<RecurringTransactionEntity>>

    @Query("SELECT * FROM recurring_transactions WHERE id = :id")
    fun getRecurringTransactionById(id: String): Flow<RecurringTransactionEntity>

    @Upsert
    suspend fun insert(value: RecurringTransactionEntity)

    @Delete
    suspend fun delete(value: RecurringTransactionEntity)
}
