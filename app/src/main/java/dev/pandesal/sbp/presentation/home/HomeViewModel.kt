package dev.pandesal.sbp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.domain.usecase.NetWorthUseCase
import dev.pandesal.sbp.domain.usecase.ZeroBasedBudgetUseCase
import dev.pandesal.sbp.domain.usecase.SettingsUseCase
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel
import dev.pandesal.sbp.presentation.model.BudgetSummaryUiModel
import dev.pandesal.sbp.presentation.model.DailySpend
import dev.pandesal.sbp.presentation.model.DailySpendUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase,
    private val accountUseCase: AccountUseCase,
    private val netWorthUseCase: NetWorthUseCase,
    private val zeroBasedBudgetUseCase: ZeroBasedBudgetUseCase,
    private val transactionUseCase: TransactionUseCase,
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var loadJob: kotlinx.coroutines.Job? = null

    init {
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val today = LocalDate.now()
            val start = today.minusDays(6)
            val prevStart = start.minusDays(7)
            val prevEnd = start.minusDays(1)

            combine(
                categoryUseCase.getCategoriesWithLatestBudget(),
                accountUseCase.getAccounts(),
                netWorthUseCase.getCurrentNetWorth(),
                zeroBasedBudgetUseCase.getBudgetSummary(),
                transactionUseCase.getTransactionsByTypeAndDateRange(
                    dev.pandesal.sbp.domain.model.TransactionType.OUTFLOW,
                    start,
                    today
                ),
                transactionUseCase.getTransactionsByTypeAndDateRange(
                    dev.pandesal.sbp.domain.model.TransactionType.OUTFLOW,
                    prevStart,
                    prevEnd
                ),
                settingsUseCase.getSettings()
            ) { categories, accounts, netWorth, summary, currentTx, prevTx, settings ->
                val budgets = categories.map { it.toBudgetUiModel() }
                val accountsUi = accounts.map { it.toUiModel() }
                val netWorthUi = netWorth.map { it.toUiModel() }
                val summaryUi = summary.toUiModel()
                val grouped = currentTx.groupBy { it.createdAt }
                val entries = (0..6).map { i ->
                    val date = start.plusDays(i.toLong())
                    DailySpend(
                        label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                        amount = grouped[date]?.fold(BigDecimal.ZERO) { acc, tx -> acc + tx.amount } ?: BigDecimal.ZERO
                    )
                }
                val currentTotal = entries.fold(BigDecimal.ZERO) { acc, d -> acc + d.amount }
                val prevTotal = prevTx.fold(BigDecimal.ZERO) { acc, tx -> acc + tx.amount }
                val change = if (prevTotal == BigDecimal.ZERO) 0.0 else ((currentTotal - prevTotal)
                    .divide(prevTotal, java.math.MathContext.DECIMAL64)
                    .toDouble() * 100)

                val dailySpent = DailySpendUiModel(entries, change)

                HomeUiState.Success(
                    favoriteBudgets = budgets,
                    accounts = accountsUi,
                    netWorthData = netWorthUi,
                    dailySpent = dailySpent,
                    budgetSummary = summaryUi,
                    currency = settings.currency
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }


}

private fun dev.pandesal.sbp.domain.model.Account.toUiModel(): AccountSummaryUiModel {
    val isSpendingWallet = when (type) {
        dev.pandesal.sbp.domain.model.AccountType.CASH_WALLET,
        dev.pandesal.sbp.domain.model.AccountType.MOBILE_DIGITAL_WALLET -> true
        else -> false
    }
    val isFundingWallet = type == dev.pandesal.sbp.domain.model.AccountType.BANK_ACCOUNT
    return AccountSummaryUiModel(
        name = name,
        balance = balance,
        type = type,
        isSpendingWallet = isSpendingWallet,
        isFundingWallet = isFundingWallet,
        currency = currency
    )
}

private fun dev.pandesal.sbp.domain.model.NetWorthRecord.toUiModel(): NetWorthUiModel =
    NetWorthUiModel(label, assets, liabilities)

private fun dev.pandesal.sbp.domain.model.CategoryWithBudget.toBudgetUiModel(): BudgetCategoryUiModel {
    val allocated = budget?.allocated ?: java.math.BigDecimal.ZERO
    val spent = budget?.spent ?: java.math.BigDecimal.ZERO
    return BudgetCategoryUiModel(category.name, allocated, spent, budget?.currency ?: "PHP")
}

private fun dev.pandesal.sbp.domain.model.BudgetSummary.toUiModel(): BudgetSummaryUiModel =
    BudgetSummaryUiModel(assigned, unassigned)
