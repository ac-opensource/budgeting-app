package dev.pandesal.sbp.fakes

import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.repository.AccountRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAccountRepository : AccountRepositoryInterface {
    val accountsFlow = MutableStateFlow<List<Account>>(emptyList())
    val insertedAccounts = mutableListOf<Account>()

    override fun getAccounts(): Flow<List<Account>> = accountsFlow

    override suspend fun insertAccount(account: Account) {
        insertedAccounts.add(account)
    }

    override suspend fun deleteAccount(account: Account) { /* no-op */ }
}
