package dev.pandesal.sbp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import dev.pandesal.sbp.data.local.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    fun getAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountById(id: Int): AccountEntity?

    @Upsert
    suspend fun insert(value: AccountEntity)

    @Upsert
    suspend fun update(value: AccountEntity)

    @Delete
    suspend fun delete(value: AccountEntity)
}
