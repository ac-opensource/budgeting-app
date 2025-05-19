package dev.pandesal.sbp.presentation.insights

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.presentation.components.SkeletonLoader
import dev.pandesal.sbp.presentation.components.TimePeriodDropdown
import dev.pandesal.sbp.presentation.home.components.NetWorthBarChart
import dev.pandesal.sbp.presentation.insights.components.BudgetVsOutflowChart
import dev.pandesal.sbp.presentation.insights.components.CalendarView
import dev.pandesal.sbp.presentation.insights.components.CashflowLineChart

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
            Text(
                text = "Insights",
                style = MaterialTheme.typography.titleLargeEmphasized
            )

            Spacer(modifier = Modifier.height(16.dp))

            CalendarView(data.calendarEvents)

            Spacer(modifier = Modifier.height(16.dp))

            Box {
                CashflowLineChart(data.cashflowByPeriod[cashflowPeriod] ?: emptyList())
                TimePeriodDropdown(
                    modifier = Modifier.align(Alignment.TopEnd),
                    period = cashflowPeriod, onPeriodChange = { cashflowPeriod = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box {
                BudgetVsOutflowChart(data.budgetVsOutflow, budgetPeriod.label)
                TimePeriodDropdown(
                    modifier = Modifier.align(Alignment.TopEnd),
                    period = budgetPeriod, onPeriodChange = { budgetPeriod = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box {
                NetWorthBarChart(data.netWorthData)
                TimePeriodDropdown(
                    modifier = Modifier.align(Alignment.TopEnd),
                    period = netWorthPeriod, onPeriodChange = { netWorthPeriod = it }
                )
            }
            Spacer(modifier = Modifier.height(140.dp))
        }
    }
}

