package dev.pandesal.sbp

import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import java.math.BigDecimal
import dev.pandesal.sbp.fakes.FakeAccountRepository
import dev.pandesal.sbp.fakes.FakeCategoryRepository
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
    private val categoryRepository = FakeCategoryRepository()
    private val useCase = AccountUseCase(repository)
    private val categoryUseCase = CategoryUseCase(categoryRepository)

    @Test
    fun uiStateEmitsAccounts() = runTest {
        val account = Account(name = "A", type = AccountType.CASH_WALLET, currency = "PHP")
        repository.accountsFlow.value = listOf(account)

        val vm = AccountsViewModel(useCase, categoryUseCase)
        advanceUntilIdle()

        val state = vm.uiState.value as AccountsUiState.Success
        assertEquals(listOf(account), state.accounts)
    }

    @Test
    fun addAccountInsertsAccount() = runTest {
        val vm = AccountsViewModel(useCase, categoryUseCase)
        vm.addAccount("B", AccountType.BANK_ACCOUNT, initialBalance = BigDecimal.TEN, "PHP", null, null)
        advanceUntilIdle()
        assertEquals(1, repository.insertedAccounts.size)
        assertEquals("B", repository.insertedAccounts[0].name)
        assertEquals(BigDecimal.TEN, repository.insertedAccounts[0].balance)
    }

    @Test
    fun addLoanAccountCreatesLiabilityCategory() = runTest {
        val vm = AccountsViewModel(useCase, categoryUseCase)
        vm.addAccount("Car Loan", AccountType.LOAN, "PHP", "1000", "100")
        advanceUntilIdle()
        assertEquals("Liabilities", categoryRepository.insertedGroups[0].name)
        assertEquals("Car Loan", categoryRepository.insertedCategories[0].name)
    }
}
