package dev.pandesal.sbp.presentation.trends

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.presentation.components.TimePeriodDropdown
import dev.pandesal.sbp.presentation.insights.TimePeriod
import dev.pandesal.sbp.presentation.trends.components.SpendingTrendLineChart

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TrendsScreen(viewModel: TrendsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val period by viewModel.period.collectAsState()

    if (state is TrendsUiState.Ready) {
        val data = state as TrendsUiState.Ready
        val trends = data.trends[period] ?: emptyList()
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Trends", style = MaterialTheme.typography.titleLargeEmphasized)
            Spacer(modifier = Modifier.height(16.dp))
            Box {
                SpendingTrendLineChart(trends)
                TimePeriodDropdown(
                    modifier = Modifier.align(Alignment.TopEnd),
                    period = period,
                    onPeriodChange = { viewModel.setPeriod(it) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Next month forecast: ${"%.2f".format(data.forecast)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(140.dp))
        }
    }
}

