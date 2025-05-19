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

    init {
        observeData()
    }

    private fun observeData() {
        _uiState.value = InsightsUiState.Loading
        viewModelScope.launch {
            combine(
                transactionUseCase.getAllTransactions(),
                categoryUseCase.getMonthlyBudgetsByYearMonth(YearMonth.now()),
                accountUseCase.getAccounts()
            ) { transactions, budgets, accounts ->
                val cashflow = transactions
                    .groupBy { YearMonth.from(it.createdAt) }
                    .toSortedMap()
                    .map { (month, txs) ->
                        val inflow = txs.filter { it.transactionType == TransactionType.INFLOW }
                            .sumOf { it.amount.toDouble() }
                        val outflow = txs.filter { it.transactionType == TransactionType.OUTFLOW }
                            .sumOf { it.amount.toDouble() }
                        CashflowUiModel(month.month.name.take(3), inflow, outflow)
                    }

                val totalBudget = budgets.sumOf { it.allocated.toDouble() }
                val totalSpent = budgets.sumOf { it.spent.toDouble() }
                val budgetVsOutflow = listOf(
                    BudgetOutflowUiModel("This Month", totalBudget, totalSpent)
                )

                val assets = accounts
                    .filter { it.type != AccountType.CREDIT_CARD }
                    .sumOf { it.balance.toDouble() }
                val liabilities = accounts
                    .filter { it.type == AccountType.CREDIT_CARD }
                    .sumOf { it.balance.toDouble() }
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
                    cashflow,
                    budgetVsOutflow,
                    netWorth,
                    calendarEvents
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
