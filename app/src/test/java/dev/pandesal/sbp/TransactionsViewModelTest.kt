package dev.pandesal.sbp

import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import dev.pandesal.sbp.fakes.FakeTransactionRepository
import dev.pandesal.sbp.presentation.transactions.TransactionsUiState
import dev.pandesal.sbp.presentation.transactions.TransactionsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = FakeTransactionRepository()
    private val useCase = TransactionUseCase(repository)

    @Test
    fun uiStateEmitsTransactions() = runTest {
        val tx = Transaction(name="t", amount=BigDecimal.ONE, createdAt=LocalDate.now(),
            updatedAt=LocalDate.now(), accountId="", transactionType=TransactionType.OUTFLOW)
        repository.pagedFlow.value = listOf(tx)
        val vm = TransactionsViewModel(useCase)
        advanceUntilIdle()
        val state = vm.uiState.value as TransactionsUiState.Success
        assertEquals(listOf(tx), state.transactions)
    }

    @Test
    fun searchUsesRepository() = runTest {
        val tx = Transaction(name="note", amount=BigDecimal.ONE, createdAt=LocalDate.now(),
            updatedAt=LocalDate.now(), accountId="", transactionType = TransactionType.OUTFLOW)
        repository.searchFlow.value = listOf(tx)
        val vm = TransactionsViewModel(useCase)
        vm.search("note")
        advanceUntilIdle()
        val state = vm.uiState.value as TransactionsUiState.Success
        assertEquals(listOf(tx), state.transactions)
        assertEquals(listOf("note"), repository.searchQueries)
    }
}
