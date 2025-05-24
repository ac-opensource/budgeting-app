package dev.pandesal.sbp.presentation.home

import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthBarGroup
import dev.pandesal.sbp.presentation.model.BudgetSummaryUiModel
import dev.pandesal.sbp.presentation.model.DailySpendUiModel

sealed interface HomeUiState {
    data object Initial : HomeUiState
    data object Loading : HomeUiState
    data class Success(
        val favoriteBudgets: List<BudgetCategoryUiModel>,
        val accounts: List<AccountSummaryUiModel>,
        val netWorthData: List<NetWorthBarGroup>,
        val dailySpent: DailySpendUiModel,
        val budgetSummary: BudgetSummaryUiModel,
        val currency: String
    ) : HomeUiState
    data class Error(val errorMessage: String) : HomeUiState
}