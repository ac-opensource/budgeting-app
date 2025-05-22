package dev.pandesal.sbp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.domain.usecase.NetWorthUseCase
import dev.pandesal.sbp.domain.usecase.ZeroBasedBudgetUseCase
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel
import dev.pandesal.sbp.presentation.model.BudgetSummaryUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase,
    private val accountUseCase: AccountUseCase,
    private val netWorthUseCase: NetWorthUseCase,
    private val zeroBasedBudgetUseCase: ZeroBasedBudgetUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = HomeUiState.Loading

        val dummyBudgets = listOf(
            BudgetCategoryUiModel("Groceries", java.math.BigDecimal("5000"), java.math.BigDecimal("3200"), "PHP"),
            BudgetCategoryUiModel("Utilities", java.math.BigDecimal("3000"), java.math.BigDecimal("1200"), "PHP"),
            BudgetCategoryUiModel("Transport", java.math.BigDecimal("2000"), java.math.BigDecimal("1800"), "PHP"),
            BudgetCategoryUiModel("Dining Out", java.math.BigDecimal("1500"), java.math.BigDecimal("800"), "PHP"),
        )

        viewModelScope.launch {
            combine(
                categoryUseCase.getCategoriesWithLatestBudget(),
                accountUseCase.getAccounts(),
                netWorthUseCase.getCurrentNetWorth(),
                zeroBasedBudgetUseCase.getBudgetSummary()
            ) { categories, accounts, netWorth, summary ->
                val budgets = categories.map { it.toBudgetUiModel() }
                val accountsUi = accounts.map { it.toUiModel() }
                val netWorthUi = netWorth.map { it.toUiModel() }
                val summaryUi = summary.toUiModel()
                HomeUiState.Success(
                    favoriteBudgets = budgets,
                    accounts = accountsUi,
                    netWorthData = netWorthUi,
                    budgetSummary = summaryUi
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
