package dev.pandesal.sbp.presentation.insights

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.presentation.home.components.NetWorthBarChart
import dev.pandesal.sbp.presentation.insights.components.BudgetOutflowEntry
import dev.pandesal.sbp.presentation.insights.components.BudgetVsOutflowChart
import dev.pandesal.sbp.presentation.insights.components.CashflowEntry
import dev.pandesal.sbp.presentation.insights.components.CashflowLineChart
import dev.pandesal.sbp.presentation.model.NetWorthUiModel

@Composable
fun InsightsScreen() {
    val dummyNetWorth = listOf(
        NetWorthUiModel("Jan", 40000.0, 10000.0),
        NetWorthUiModel("Feb", 42000.0, 9500.0),
        NetWorthUiModel("Mar", 45000.0, 8700.0),
        NetWorthUiModel("Apr", 47000.0, 8000.0)
    )
    val dummyCashflow = listOf(
        CashflowEntry("Jan", 2000.0, 1500.0),
        CashflowEntry("Feb", 2200.0, 1800.0),
        CashflowEntry("Mar", 2500.0, 1600.0),
        CashflowEntry("Apr", 2400.0, 1700.0)
    )
    val dummyBudget = listOf(
        BudgetOutflowEntry("Jan", 3000.0, 2800.0),
        BudgetOutflowEntry("Feb", 3100.0, 3300.0),
        BudgetOutflowEntry("Mar", 3200.0, 3000.0),
        BudgetOutflowEntry("Apr", 3300.0, 3400.0)
    )

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        CashflowLineChart(dummyCashflow)
        Spacer(modifier = Modifier.height(16.dp))
        BudgetVsOutflowChart(dummyBudget)
        Spacer(modifier = Modifier.height(16.dp))
        NetWorthBarChart(dummyNetWorth)
    }
}

