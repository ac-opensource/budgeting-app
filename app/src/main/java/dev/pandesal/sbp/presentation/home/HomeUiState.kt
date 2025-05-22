package dev.pandesal.sbp.presentation.home

import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel
import dev.pandesal.sbp.presentation.model.BudgetSummaryUiModel
import dev.pandesal.sbp.presentation.model.DailySpendUiModel

sealed interface HomeUiState {
    data object Initial : HomeUiState
    data object Loading : HomeUiState
    data class Success(
        val favoriteBudgets: List<BudgetCategoryUiModel>,
        val accounts: List<AccountSummaryUiModel>,
        val netWorthData: List<NetWorthUiModel>,
        val dailySpent: DailySpendUiModel,
        val budgetSummary: BudgetSummaryUiModel
    ) : HomeUiState
    data class Error(val errorMessage: String) : HomeUiState
}