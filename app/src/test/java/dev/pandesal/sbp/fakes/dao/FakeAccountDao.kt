package dev.pandesal.sbp.fakes.dao

import dev.pandesal.sbp.data.dao.AccountDao
import dev.pandesal.sbp.data.local.AccountEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAccountDao : AccountDao {
    private val accounts = mutableListOf<AccountEntity>()
    private val flow = MutableStateFlow<List<AccountEntity>>(accounts)

    override fun getAccounts(): Flow<List<AccountEntity>> = flow.asStateFlow()

    override suspend fun getAccountById(id: Int): AccountEntity? =
        accounts.find { it.id == id }

    override suspend fun insert(value: AccountEntity) {
        accounts.removeAll { it.id == value.id }
        accounts.add(value)
        flow.value = accounts.toList()
    }

    override suspend fun update(value: AccountEntity) = insert(value)

    override suspend fun delete(value: AccountEntity) {
        accounts.removeIf { it.id == value.id }
        flow.value = accounts.toList()
    }
}
