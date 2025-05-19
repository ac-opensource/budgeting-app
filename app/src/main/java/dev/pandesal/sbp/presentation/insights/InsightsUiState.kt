package dev.pandesal.sbp.presentation.insights

import dev.pandesal.sbp.presentation.model.BudgetOutflowUiModel
import dev.pandesal.sbp.presentation.model.CashflowUiModel
import dev.pandesal.sbp.presentation.model.CalendarEvent
import dev.pandesal.sbp.presentation.model.NetWorthUiModel

sealed interface InsightsUiState {
    data object Initial : InsightsUiState
    data object Loading : InsightsUiState
    data class Success(
        val cashflowByPeriod: Map<TimePeriod, List<CashflowUiModel>>,
        val budgetVsOutflow: List<BudgetOutflowUiModel>,
        val netWorthData: List<NetWorthUiModel>,
        val calendarEvents: List<CalendarEvent>
    ) : InsightsUiState
    data class Error(val errorMessage: String) : InsightsUiState
}
