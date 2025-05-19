package dev.pandesal.sbp

import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import dev.pandesal.sbp.fakes.FakeRecurringTransactionRepository
import dev.pandesal.sbp.presentation.recurring.RecurringTransactionDetailsViewModel
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
    fun setTransactionUpdatesState() = runTest {
        val vm = RecurringTransactionDetailsViewModel(useCase)
        val tx = RecurringTransaction(
            transaction = Transaction(
                name = "T",
                amount = BigDecimal.ONE,
                createdAt = LocalDate.now(),
                updatedAt = LocalDate.now(),
                accountId = "",
                transactionType = TransactionType.OUTFLOW
            ),
            interval = RecurringInterval.MONTHLY
        )
        repository.transactionsFlow.value = listOf(tx)
        vm.setTransaction(tx.transaction.id)
        advanceUntilIdle()
        assertEquals(tx, vm.transaction.value)
    }

    @Test
    fun saveInsertsTransaction() = runTest {
        val vm = RecurringTransactionDetailsViewModel(useCase)
        val tx = RecurringTransaction(
            transaction = Transaction(
                name = "T",
                amount = BigDecimal.ONE,
                createdAt = LocalDate.now(),
                updatedAt = LocalDate.now(),
                accountId = "",
                transactionType = TransactionType.OUTFLOW
            ),
            interval = RecurringInterval.MONTHLY
        )
        repository.transactionsFlow.value = listOf(tx)
        vm.setTransaction(tx.transaction.id)
        vm.save()
        advanceUntilIdle()
        assertEquals(listOf(tx), repository.inserted)
    }
}
