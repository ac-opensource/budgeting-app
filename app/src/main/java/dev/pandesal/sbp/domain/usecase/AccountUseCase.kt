package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.repository.AccountRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountUseCase @Inject constructor(
    private val repository: AccountRepositoryInterface
) {
    fun getAccounts(): Flow<List<Account>> = repository.getAccounts()

    suspend fun insertAccount(account: Account) = repository.insertAccount(account)

    suspend fun deleteAccount(account: Account) = repository.deleteAccount(account)
}
