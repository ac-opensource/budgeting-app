package dev.pandesal.sbp

import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import dev.pandesal.sbp.fakes.FakeAccountRepository
import dev.pandesal.sbp.fakes.FakeCategoryRepository
import dev.pandesal.sbp.fakes.FakeRecurringTransactionRepository
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
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class NewTransactionsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val accountRepo = FakeAccountRepository()
    private val categoryRepo = FakeCategoryRepository()
    private val transactionRepo = FakeTransactionRepository()
    private val recurringTransactionRepo = FakeRecurringTransactionRepository()

    private val accountUseCase = AccountUseCase(accountRepo)
    private val categoryUseCase = CategoryUseCase(categoryRepo)
    private val transactionUseCase = TransactionUseCase(transactionRepo, accountRepo)
    private val recurringTransactionUseCase = RecurringTransactionUseCase(recurringTransactionRepo)

    @Test
    fun uiStateEmitsSuccess() = runTest {
        val group = CategoryGroup(id=1, name="G", description="", icon="")
        val category = Category(id=1, name="C", description="", icon="", categoryGroupId=1, categoryType=TransactionType.OUTFLOW, weight=0)
        accountRepo.accountsFlow.value = listOf(Account(name="A", type=AccountType.CASH_WALLET, currency = "PHP"))
        categoryRepo.groupsFlow.value = listOf(group)
        categoryRepo.categoriesFlow.value = listOf(category)
        transactionRepo.merchantsFlow.value = listOf("Shop")

        val vm = NewTransactionsViewModel(transactionUseCase, categoryUseCase, accountUseCase, recurringTransactionUseCase)
        advanceUntilIdle()
        assertTrue(vm.uiState.value is NewTransactionUiState.Success)
    }

    @Test
    fun saveTransactionCallsInsert() = runTest {
        val vm = NewTransactionsViewModel(transactionUseCase, categoryUseCase, accountUseCase, recurringTransactionUseCase)
        vm.updateTransaction(
            Transaction(name = "Test", category = Category(id = 1, name = "C", description = "", icon = "", categoryGroupId = 1, categoryType = TransactionType.OUTFLOW, weight = 0), from = 1, fromAccountName = "1", amount = "100.00".toBigDecimal(), createdAt = LocalDate.now(), updatedAt = LocalDate.now(), transactionType = TransactionType.OUTFLOW)
        )
        vm.saveTransaction(false, interval = RecurringInterval.MONTHLY, cutoffDays = 1)
        advanceUntilIdle()
        assertEquals(1, transactionRepo.insertedTransactions.size)
    }

    @Test
    fun saveTransactionUpdatesAccountBalance() = runTest {
        val account = Account(id = 1, name = "A", type = AccountType.CASH_WALLET, balance = BigDecimal("100"), currency = "PHP")
        accountRepo.accountsFlow.value = listOf(account)

        val vm = NewTransactionsViewModel(transactionUseCase, categoryUseCase, accountUseCase, recurringTransactionUseCase)
        vm.updateTransaction(
            Transaction(
                name = "Test",
                category = Category(id = 1, name = "C", description = "", icon = "", categoryGroupId = 1, categoryType = TransactionType.OUTFLOW, weight = 0),
                from = 1,
                fromAccountName = "A",
                amount = BigDecimal.TEN,
                createdAt = LocalDate.now(),
                updatedAt = LocalDate.now(),
                transactionType = TransactionType.OUTFLOW
            )
        )

        vm.saveTransaction(false, RecurringInterval.MONTHLY, 1)
        advanceUntilIdle()
        assertEquals(account.balance - BigDecimal.TEN, accountRepo.insertedAccounts.last().balance)
    }
}
