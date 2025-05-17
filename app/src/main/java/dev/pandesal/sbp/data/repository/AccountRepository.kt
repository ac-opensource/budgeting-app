package dev.pandesal.sbp.data.repository

import dev.pandesal.sbp.data.dao.AccountDao
import dev.pandesal.sbp.data.local.toDomainModel
import dev.pandesal.sbp.data.local.toEntity
import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.repository.AccountRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val dao: AccountDao
) : AccountRepositoryInterface {

    override fun getAccounts(): Flow<List<Account>> =
        dao.getAccounts().map { entities -> entities.map { it.toDomainModel() } }

    override suspend fun insertAccount(account: Account) =
        dao.insert(account.toEntity())

    override suspend fun deleteAccount(account: Account) =
        dao.delete(account.toEntity())
}
