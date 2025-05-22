package dev.pandesal.sbp.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import dev.pandesal.sbp.presentation.model.BudgetOutflowUiModel
import dev.pandesal.sbp.presentation.model.CashflowUiModel
import dev.pandesal.sbp.presentation.model.CalendarEvent
import dev.pandesal.sbp.presentation.model.CalendarEventType
import dev.pandesal.sbp.presentation.model.NetWorthUiModel
import dev.pandesal.sbp.presentation.insights.TimePeriod
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase,
    private val categoryUseCase: CategoryUseCase,
    private val accountUseCase: AccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<InsightsUiState>(InsightsUiState.Initial)
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    private val _period = MutableStateFlow(TimePeriod.MONTHLY)
    val period: StateFlow<TimePeriod> = _period.asStateFlow()

    init {
        observeData()
    }

    fun setPeriod(period: TimePeriod) {
        _period.value = period
    }

    private fun observeData() {
        _uiState.value = InsightsUiState.Loading
        viewModelScope.launch {
            combine(
                transactionUseCase.getAllTransactions(),
                categoryUseCase.getMonthlyBudgetsByYearMonth(YearMonth.now()),
                accountUseCase.getAccounts()
            ) { transactions, budgets, accounts ->
                val cashflowByPeriod = TimePeriod.values().associateWith { p ->
                    groupTransactionsByPeriod(transactions, p)
                }

                val totalBudget = budgets.fold(java.math.BigDecimal.ZERO) { acc, b -> acc + b.allocated }
                val totalSpent = budgets.fold(java.math.BigDecimal.ZERO) { acc, b -> acc + b.spent }
                val budgetVsOutflow = listOf(
                    BudgetOutflowUiModel("This Month", totalBudget, totalSpent)
                )

                val assets = accounts
                    .filter { it.type != AccountType.CREDIT_CARD }
                    .fold(java.math.BigDecimal.ZERO) { acc, a -> acc + a.balance }
                val liabilities = accounts
                    .filter { it.type == AccountType.CREDIT_CARD }
                    .fold(java.math.BigDecimal.ZERO) { acc, a -> acc + a.balance }
                val netWorth = listOf(NetWorthUiModel("Now", assets, liabilities))

                val calendarEvents = transactions.mapNotNull { tx ->
                    val type = when (tx.transactionType) {
                        TransactionType.INFLOW -> CalendarEventType.INFLOW
                        TransactionType.OUTFLOW -> CalendarEventType.OUTFLOW
                        else -> null
                    }
                    type?.let { CalendarEvent(tx.createdAt, it) }
                }

                InsightsUiState.Success(
                    cashflowByPeriod,
                    budgetVsOutflow,
                    netWorth,
                    calendarEvents
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun groupTransactionsByPeriod(
        transactions: List<dev.pandesal.sbp.domain.model.Transaction>,
        period: TimePeriod
    ): List<CashflowUiModel> {
        return when (period) {
            TimePeriod.DAILY -> transactions
                .groupBy { it.createdAt }
                .toSortedMap()
                .map { (date, txs) ->
                    val inflow = txs.filter { it.transactionType == TransactionType.INFLOW }
                        .fold(java.math.BigDecimal.ZERO) { acc, t -> acc + t.amount }
                    val outflow = txs.filter { it.transactionType == TransactionType.OUTFLOW }
                        .fold(java.math.BigDecimal.ZERO) { acc, t -> acc + t.amount }
                    CashflowUiModel(date.dayOfMonth.toString(), inflow, outflow)
                }
            TimePeriod.WEEKLY -> transactions
                .groupBy { Pair(it.createdAt.year, it.createdAt.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear())) }
                .toSortedMap(compareBy({ it.first }, { it.second }))
                .map { (pair, txs) ->
                    val inflow = txs.filter { it.transactionType == TransactionType.INFLOW }
                        .fold(java.math.BigDecimal.ZERO) { acc, t -> acc + t.amount }
                    val outflow = txs.filter { it.transactionType == TransactionType.OUTFLOW }
                        .fold(java.math.BigDecimal.ZERO) { acc, t -> acc + t.amount }
                    CashflowUiModel("W${pair.second}", inflow, outflow)
                }
            TimePeriod.MONTHLY -> transactions
                .groupBy { YearMonth.from(it.createdAt) }
                .toSortedMap()
                .map { (month, txs) ->
                    val inflow = txs.filter { it.transactionType == TransactionType.INFLOW }
                        .fold(java.math.BigDecimal.ZERO) { acc, t -> acc + t.amount }
                    val outflow = txs.filter { it.transactionType == TransactionType.OUTFLOW }
                        .fold(java.math.BigDecimal.ZERO) { acc, t -> acc + t.amount }
                    CashflowUiModel(month.month.name.take(3), inflow, outflow)
                }
            TimePeriod.THREE_MONTH -> transactions
                .groupBy { Pair(it.createdAt.year, (it.createdAt.monthValue - 1) / 3) }
                .toSortedMap(compareBy({ it.first }, { it.second }))
                .map { (pair, txs) ->
                    val inflow = txs.filter { it.transactionType == TransactionType.INFLOW }
                        .fold(java.math.BigDecimal.ZERO) { acc, t -> acc + t.amount }
                    val outflow = txs.filter { it.transactionType == TransactionType.OUTFLOW }
                        .fold(java.math.BigDecimal.ZERO) { acc, t -> acc + t.amount }
                    CashflowUiModel("Q${pair.second + 1}", inflow, outflow)
                }
            TimePeriod.SIX_MONTH -> transactions
                .groupBy { Pair(it.createdAt.year, (it.createdAt.monthValue - 1) / 6) }
                .toSortedMap(compareBy({ it.first }, { it.second }))
                .map { (pair, txs) ->
                    val inflow = txs.filter { it.transactionType == TransactionType.INFLOW }
                        .fold(java.math.BigDecimal.ZERO) { acc, t -> acc + t.amount }
                    val outflow = txs.filter { it.transactionType == TransactionType.OUTFLOW }
                        .fold(java.math.BigDecimal.ZERO) { acc, t -> acc + t.amount }
                    CashflowUiModel("H${pair.second + 1}", inflow, outflow)
                }
            TimePeriod.YEARLY -> transactions
                .groupBy { it.createdAt.year }
                .toSortedMap()
                .map { (year, txs) ->
                    val inflow = txs.filter { it.transactionType == TransactionType.INFLOW }
                        .fold(java.math.BigDecimal.ZERO) { acc, t -> acc + t.amount }
                    val outflow = txs.filter { it.transactionType == TransactionType.OUTFLOW }
                        .fold(java.math.BigDecimal.ZERO) { acc, t -> acc + t.amount }
                    CashflowUiModel(year.toString(), inflow, outflow)
                }
        }
    }
}
