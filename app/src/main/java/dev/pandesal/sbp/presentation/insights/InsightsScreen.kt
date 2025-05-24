package dev.pandesal.sbp.presentation.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.YearMonth
import java.time.LocalDate
import dev.pandesal.sbp.presentation.components.SkeletonLoader
import dev.pandesal.sbp.presentation.components.TimePeriodDropdown
import dev.pandesal.sbp.presentation.home.components.NetWorthBarChart
import dev.pandesal.sbp.presentation.insights.components.BudgetVsOutflowChart
import dev.pandesal.sbp.presentation.insights.components.CalendarView
import dev.pandesal.sbp.presentation.insights.components.CashflowLineChart
import dev.pandesal.sbp.presentation.insights.components.DateTooltip
import dev.pandesal.sbp.presentation.reminders.ReminderFormDialog
import dev.pandesal.sbp.presentation.trends.TrendsViewModel
import dev.pandesal.sbp.presentation.trends.TrendsUiState
import dev.pandesal.sbp.presentation.trends.components.SpendingTrendLineChart

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel(),
    trendsViewModel: TrendsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val period by viewModel.period.collectAsState()
    val trendState by trendsViewModel.uiState.collectAsState()
    val trendPeriod by trendsViewModel.period.collectAsState()

    var cashflowPeriod by remember { mutableStateOf(period) }
    var budgetPeriod by remember { mutableStateOf(period) }
    var netWorthPeriod by remember { mutableStateOf(period) }
    var trendChartPeriod by remember { mutableStateOf(trendPeriod) }
    var calendarMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var tooltipOffset by remember { mutableStateOf(IntOffset.Zero) }
    var showReminderForm by remember { mutableStateOf(false) }
    val tooltip by viewModel.tooltipState.collectAsState()

    if (state is InsightsUiState.Initial) {
        SkeletonLoader()
    } else if (state is InsightsUiState.Success) {
        val data = state as InsightsUiState.Success
        Box {
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

                val monthEvents =
                    data.calendarEvents.filter { YearMonth.from(it.date) == calendarMonth }
                CalendarView(
                    events = monthEvents,
                    month = calendarMonth,
                    selectedDate = selectedDate,
                    onMonthChange = {
                        calendarMonth = it
                        viewModel.setCalendarMonth(it)
                    },
                    onDateClick = { date, offset ->
                        selectedDate = date
                        tooltipOffset = offset
                        viewModel.loadDayDetails(date)
                    }
                )

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
                val budgetEntries = data.budgetVsOutflow[budgetPeriod] ?: emptyList()
                BudgetVsOutflowChart(budgetEntries, budgetPeriod.label)
                TimePeriodDropdown(
                    modifier = Modifier.align(Alignment.TopEnd),
                    period = budgetPeriod, onPeriodChange = { budgetPeriod = it }
                )
            }

                Spacer(modifier = Modifier.height(16.dp))

            Box {
                val netWorthEntries = data.netWorthByPeriod[netWorthPeriod] ?: emptyList()
                NetWorthBarChart(netWorthEntries)
                TimePeriodDropdown(
                    modifier = Modifier.align(Alignment.TopEnd),
                    period = netWorthPeriod, onPeriodChange = { netWorthPeriod = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val tagSummary by viewModel.tagSummary.collectAsState()
            Text("Top 5 Hobbies you spend on", style = MaterialTheme.typography.titleMedium)
            tagSummary.take(5).forEach { summary ->
                Text("${summary.tag}: ${summary.total}", style = MaterialTheme.typography.bodyMedium)
            }

                if (trendState is TrendsUiState.Ready) {
                    Spacer(modifier = Modifier.height(16.dp))
                    val trendData =
                        (trendState as TrendsUiState.Ready).trends[trendChartPeriod] ?: emptyList()
                    Box {
                        SpendingTrendLineChart(trendData)
                        TimePeriodDropdown(
                            modifier = Modifier.align(Alignment.TopEnd),
                            period = trendChartPeriod,
                            onPeriodChange = {
                                trendChartPeriod = it
                                trendsViewModel.setPeriod(it)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Next month forecast: ${"%.2f".format((trendState as TrendsUiState.Ready).forecast)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(140.dp))
            }

            selectedDate?.let { date ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable {
                            selectedDate = null
                        }
                )
                Popup(alignment = Alignment.TopStart, offset = tooltipOffset) {
                    DateTooltip(
                        date = date,
                        state = tooltip,
                        onClose = { selectedDate = null },
                        onAddReminder = { showReminderForm = true }
                    )
                }
            }
        }

        if (showReminderForm && selectedDate != null) {
            ReminderFormDialog(date = selectedDate!!, onDismiss = { showReminderForm = false })
        }
    }
}
