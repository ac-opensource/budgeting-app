package dev.pandesal.sbp

import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import dev.pandesal.sbp.fakes.FakeTransactionRepository
import dev.pandesal.sbp.fakes.FakeAccountRepository
import dev.pandesal.sbp.presentation.transactions.details.TransactionDetailsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionDetailsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = FakeTransactionRepository()
    private val accountRepository = FakeAccountRepository()
    private val useCase = TransactionUseCase(repository, accountRepository)

    @Test
    fun setTransactionUpdatesState() = runTest {
        val vm = TransactionDetailsViewModel(useCase)
        val tx = Transaction(
            name = "T",
            amount = BigDecimal.ONE,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now(),
            from = 1,
            fromAccountName = "1",
            transactionType = TransactionType.OUTFLOW
        )
        vm.updateTransaction(tx)
        advanceUntilIdle()
        assertEquals(tx, vm.transaction.value)
    }

    @Test
    fun saveInsertsTransaction() = runTest {
        val vm = TransactionDetailsViewModel(useCase)
        val tx = Transaction(
            name = "T",
            amount = BigDecimal.ONE,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now(),
            from = 1,
            fromAccountName = "1",
            transactionType = TransactionType.OUTFLOW
        )
        vm.updateTransaction(tx)
        advanceUntilIdle()
        vm.save()
        advanceUntilIdle()
        assertEquals(listOf(tx), repository.insertedTransactions)
    }
}
