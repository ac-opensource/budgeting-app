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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.presentation.components.FilterTab
import dev.pandesal.sbp.presentation.components.TimePeriodDropdown
import dev.pandesal.sbp.presentation.insights.TimePeriod
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
    val period by viewModel.period.collectAsState()

    var cashflowPeriod by remember { mutableStateOf(period) }
    var budgetPeriod by remember { mutableStateOf(period) }
    var netWorthPeriod by remember { mutableStateOf(period) }

    if (state is InsightsUiState.Initial) {
        SkeletonLoader()
    } else if (state is InsightsUiState.Success) {
        val data = state as InsightsUiState.Success
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            FilterTab(
                selectedIndex = period.ordinal,
                tabs = TimePeriod.values().map { it.label }
            ) { index ->
                viewModel.setPeriod(TimePeriod.values()[index])
            }

            CalendarView(data.calendarEvents)

            Spacer(modifier = Modifier.height(16.dp))
            TimePeriodDropdown(period = cashflowPeriod, onPeriodChange = { cashflowPeriod = it })
            CashflowLineChart(data.cashflowByPeriod[cashflowPeriod] ?: emptyList())

            Spacer(modifier = Modifier.height(16.dp))
            TimePeriodDropdown(period = budgetPeriod, onPeriodChange = { budgetPeriod = it })
            BudgetVsOutflowChart(data.budgetVsOutflow, budgetPeriod.label)

            Spacer(modifier = Modifier.height(16.dp))
            TimePeriodDropdown(period = netWorthPeriod, onPeriodChange = { netWorthPeriod = it })
            NetWorthBarChart(data.netWorthData)
        }
    }
}

