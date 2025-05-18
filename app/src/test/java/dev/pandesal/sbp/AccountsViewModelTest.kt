package dev.pandesal.sbp

import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.fakes.FakeAccountRepository
import dev.pandesal.sbp.presentation.accounts.AccountsUiState
import dev.pandesal.sbp.presentation.accounts.AccountsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = FakeAccountRepository()
    private val useCase = AccountUseCase(repository)

    @Test
    fun uiStateEmitsAccounts() = runTest {
        val account = Account(name = "A", type = AccountType.CASH_WALLET)
        repository.accountsFlow.value = listOf(account)

        val vm = AccountsViewModel(useCase)
        advanceUntilIdle()

        val state = vm.uiState.value as AccountsUiState.Success
        assertEquals(listOf(account), state.accounts)
    }

    @Test
    fun addAccountInsertsAccount() = runTest {
        val vm = AccountsViewModel(useCase)
        vm.addAccount("B", AccountType.BANK_ACCOUNT)
        advanceUntilIdle()
        assertEquals(1, repository.insertedAccounts.size)
        assertEquals("B", repository.insertedAccounts[0].name)
    }
}
