package dev.pandesal.sbp.presentation.insights

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.presentation.home.components.NetWorthBarChart
import dev.pandesal.sbp.presentation.insights.components.BudgetVsOutflowChart
import dev.pandesal.sbp.presentation.insights.components.CashflowLineChart
import dev.pandesal.sbp.presentation.insights.components.CalendarView
import dev.pandesal.sbp.presentation.model.BudgetOutflowUiModel
import dev.pandesal.sbp.presentation.model.CashflowUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel
import dev.pandesal.sbp.presentation.components.SkeletonLoader

@Composable
fun InsightsScreen(viewModel: InsightsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    if (state is InsightsUiState.Initial) {
        SkeletonLoader()
    } else if (state is InsightsUiState.Success) {
        val data = state as InsightsUiState.Success
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            CalendarView(data.calendarEvents)
            Spacer(modifier = Modifier.height(16.dp))
            CashflowLineChart(data.cashflow)
            Spacer(modifier = Modifier.height(16.dp))
            BudgetVsOutflowChart(data.budgetVsOutflow)
            Spacer(modifier = Modifier.height(16.dp))
            NetWorthBarChart(data.netWorthData)
        }
    }
}

