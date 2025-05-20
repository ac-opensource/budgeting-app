package dev.pandesal.sbp

import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import dev.pandesal.sbp.fakes.FakeRecurringTransactionRepository
import dev.pandesal.sbp.presentation.transactions.recurringdetails.RecurringTransactionDetailsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class RecurringTransactionDetailsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = FakeRecurringTransactionRepository()
    private val useCase = RecurringTransactionUseCase(repository)

    @Test
    fun loadTransactionEmitsValue() = runTest {
        val tx = RecurringTransaction(
            transaction = Transaction(
                name = "Bill",
                amount = BigDecimal.ONE,
                createdAt = LocalDate.now(),
                updatedAt = LocalDate.now(),
                transactionType = TransactionType.OUTFLOW
            ),
            interval = RecurringInterval.MONTHLY,
            startDate = LocalDate.now()
        )
        repository.transactionsFlow.value = listOf(tx)

        val vm = RecurringTransactionDetailsViewModel(useCase)
        vm.loadTransaction(tx.transaction.id)
        advanceUntilIdle()
        assertEquals(tx, vm.transaction.value)
    }

    @Test
    fun deleteCallsRepository() = runTest {
        val tx = RecurringTransaction(
            transaction = Transaction(
                name = "Bill",
                amount = BigDecimal.ONE,
                createdAt = LocalDate.now(),
                updatedAt = LocalDate.now(),
                transactionType = TransactionType.OUTFLOW
            ),
            interval = RecurringInterval.MONTHLY,
            startDate = LocalDate.now()
        )
        repository.transactionsFlow.value = listOf(tx)
        val vm = RecurringTransactionDetailsViewModel(useCase)
        vm.loadTransaction(tx.transaction.id)
        advanceUntilIdle()
        vm.delete()
        advanceUntilIdle()
        assertEquals(listOf(tx), repository.deleted)
    }
}
