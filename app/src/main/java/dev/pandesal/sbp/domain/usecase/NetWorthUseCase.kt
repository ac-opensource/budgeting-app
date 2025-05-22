package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.domain.model.NetWorthRecord
import dev.pandesal.sbp.domain.repository.AccountRepositoryInterface
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NetWorthUseCase @Inject constructor(
    private val accountRepository: AccountRepositoryInterface
) {
    fun getCurrentNetWorth(): Flow<List<NetWorthRecord>> =
        accountRepository.getAccounts().map { accounts ->
            val assets = accounts
                .filter { it.type != AccountType.CREDIT_CARD }
                .fold(BigDecimal.ZERO) { acc, account -> acc + account.balance }
            val liabilities = accounts
                .filter { it.type == AccountType.CREDIT_CARD }
                .fold(BigDecimal.ZERO) { acc, account -> acc + account.balance }
            listOf(NetWorthRecord("Now", assets, liabilities))
        }
}
