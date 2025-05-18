package dev.pandesal.sbp

import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import dev.pandesal.sbp.fakes.FakeAccountRepository
import dev.pandesal.sbp.fakes.FakeCategoryRepository
import dev.pandesal.sbp.fakes.FakeTransactionRepository
import dev.pandesal.sbp.presentation.transactions.newtransaction.NewTransactionUiState
import dev.pandesal.sbp.presentation.transactions.newtransaction.NewTransactionsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewTransactionsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val accountRepo = FakeAccountRepository()
    private val categoryRepo = FakeCategoryRepository()
    private val transactionRepo = FakeTransactionRepository()

    private val accountUseCase = AccountUseCase(accountRepo)
    private val categoryUseCase = CategoryUseCase(categoryRepo)
    private val transactionUseCase = TransactionUseCase(transactionRepo)

    @Test
    fun uiStateEmitsSuccess() = runTest {
        val group = CategoryGroup(id=1, name="G", description="", icon="")
        val category = Category(id=1, name="C", description="", icon="", categoryGroupId=1, categoryType=TransactionType.OUTFLOW, weight=0)
        accountRepo.accountsFlow.value = listOf(Account(name="A", type=AccountType.CASH_WALLET, currency = "PHP"))
        categoryRepo.groupsFlow.value = listOf(group)
        categoryRepo.categoriesFlow.value = listOf(category)
        transactionRepo.merchantsFlow.value = listOf("Shop")

        val vm = NewTransactionsViewModel(transactionUseCase, categoryUseCase, accountUseCase)
        advanceUntilIdle()
        assertTrue(vm.uiState.value is NewTransactionUiState.Success)
    }

    @Test
    fun saveTransactionCallsInsert() = runTest {
        val vm = NewTransactionsViewModel(transactionUseCase, categoryUseCase, accountUseCase)
        vm.saveTransaction()
        advanceUntilIdle()
        assertEquals(1, transactionRepo.insertedTransactions.size)
    }
}
