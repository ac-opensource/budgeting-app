package dev.pandesal.sbp

import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import dev.pandesal.sbp.fakes.FakeAccountRepository
import dev.pandesal.sbp.fakes.FakeCategoryRepository
import dev.pandesal.sbp.fakes.FakeTransactionRepository
import dev.pandesal.sbp.presentation.insights.InsightsUiState
import dev.pandesal.sbp.presentation.insights.InsightsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModelTest {
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
        accountRepo.accountsFlow.value = listOf(Account(name = "A", type = AccountType.CASH_WALLET, currency = "PHP"))
        categoryRepo.monthlyBudgetsFlow.value = listOf(
            MonthlyBudget(1,1,YearMonth.now(),BigDecimal.ONE,BigDecimal.ZERO)
        )
        transactionRepo.transactionsFlow.value = listOf(
            Transaction(name="t", amount=BigDecimal.ONE, createdAt= LocalDate.now(),
                updatedAt= LocalDate.now(), accountId="", transactionType=TransactionType.OUTFLOW)
        )
        val vm = InsightsViewModel(transactionUseCase, categoryUseCase, accountUseCase)
        advanceUntilIdle()
        val state = vm.uiState.value as InsightsUiState.Success
        assertTrue(state.calendarEvents.isNotEmpty())
    }
}
