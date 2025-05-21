package dev.pandesal.sbp

import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import dev.pandesal.sbp.fakes.FakeTransactionRepository
import dev.pandesal.sbp.fakes.FakeAccountRepository
import dev.pandesal.sbp.presentation.categories.CategoryTransactionsViewModel
import dev.pandesal.sbp.presentation.transactions.TransactionsUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryTransactionsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = FakeTransactionRepository()
    private val accountRepository = FakeAccountRepository()
    private val useCase = TransactionUseCase(repository, accountRepository)

    @Test
    fun uiStateEmitsTransactions() = runTest {
        val tx = Transaction(
            name = "t",
            amount = BigDecimal.ONE,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now(),
            from = 1,
            fromAccountName = "1",
            transactionType = TransactionType.OUTFLOW
        )
        repository.categoryFlow.value = listOf(tx)
        val vm = CategoryTransactionsViewModel(useCase)
        vm.load("1")
        advanceUntilIdle()
        val state = vm.uiState.value as TransactionsUiState.Success
        assertEquals(listOf(tx), state.transactions)
    }
}
