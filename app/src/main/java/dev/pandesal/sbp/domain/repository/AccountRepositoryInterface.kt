package dev.pandesal.sbp.domain.repository

import dev.pandesal.sbp.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepositoryInterface {
    fun getAccounts(): Flow<List<Account>>
    suspend fun insertAccount(account: Account)
    suspend fun deleteAccount(account: Account)
}
