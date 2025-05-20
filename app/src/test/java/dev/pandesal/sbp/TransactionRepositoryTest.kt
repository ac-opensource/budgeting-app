package dev.pandesal.sbp

import dev.pandesal.sbp.data.dao.CategoryDao
import dev.pandesal.sbp.data.dao.TransactionDao
import dev.pandesal.sbp.data.local.AccountEntity
import dev.pandesal.sbp.data.local.toDomainModel
import dev.pandesal.sbp.data.repository.TransactionRepository
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.fakes.dao.FakeAccountDao
import io.mockk.coEvery
import io.mockk.mockk
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionRepositoryTest {
    private val transactionDao = mockk<TransactionDao>(relaxed = true)
    private val categoryDao = mockk<CategoryDao>(relaxed = true)
    private lateinit var accountDao: FakeAccountDao
    private lateinit var repository: TransactionRepository

    @Before
    fun setup() {
        accountDao = FakeAccountDao()
        repository = TransactionRepository(transactionDao, categoryDao, accountDao)
    }

    @Test
    fun insertOutflow_debitsFromAccount() = runTest {
        val account = AccountEntity(id = 1, name = "A", type = "CASH_WALLET", balance = BigDecimal(100))
        accountDao.insert(account)

        val tx = Transaction(
            name = "Coffee",
            amount = BigDecimal.TEN,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now(),
            from = 1,
            transactionType = TransactionType.OUTFLOW
        )

        repository.insert(tx)

        val updated = accountDao.getAccountById(1)!!.toDomainModel()
        assertEquals(BigDecimal(90), updated.balance)
    }

    @Test
    fun insertInflow_creditsToAccount() = runTest {
        val account = AccountEntity(id = 1, name = "A", type = "CASH_WALLET", balance = BigDecimal(50))
        accountDao.insert(account)

        val tx = Transaction(
            name = "Salary",
            amount = BigDecimal.TEN,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now(),
            to = 1,
            transactionType = TransactionType.INFLOW
        )

        repository.insert(tx)

        val updated = accountDao.getAccountById(1)!!.toDomainModel()
        assertEquals(BigDecimal(60), updated.balance)
    }

    @Test
    fun insertTransfer_updatesBothAccounts() = runTest {
        val from = AccountEntity(id = 1, name = "A", type = "CASH_WALLET", balance = BigDecimal(100))
        val to = AccountEntity(id = 2, name = "B", type = "CASH_WALLET", balance = BigDecimal(20))
        accountDao.insert(from)
        accountDao.insert(to)

        val tx = Transaction(
            name = "Move",
            amount = BigDecimal.TEN,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now(),
            from = 1,
            to = 2,
            transactionType = TransactionType.TRANSFER
        )

        repository.insert(tx)

        val updatedFrom = accountDao.getAccountById(1)!!.toDomainModel()
        val updatedTo = accountDao.getAccountById(2)!!.toDomainModel()
        assertEquals(BigDecimal(90), updatedFrom.balance)
        assertEquals(BigDecimal(30), updatedTo.balance)
    }
}
