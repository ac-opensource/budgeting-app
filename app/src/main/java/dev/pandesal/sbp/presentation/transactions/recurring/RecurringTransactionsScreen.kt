package dev.pandesal.sbp.presentation.transactions.recurring

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import java.time.LocalDate

@Composable
fun RecurringTransactionsScreen(
    viewModel: RecurringTransactionsViewModel = hiltViewModel()
) {
    val navManager = LocalNavigationManager.current
    val state = viewModel.uiState.collectAsState()

    if (state.value is RecurringTransactionsUiState.Success) {
        val transactions = (state.value as RecurringTransactionsUiState.Success).transactions
        RecurringTransactionsContent(transactions, nextDueDate = {
            viewModel.nextDueDate(it, LocalDate.now())
        }) { rec ->
            navManager.navigate(
                NavigationDestination.TransactionDetails(rec.transaction.id)
            )
        }
    }
}

@Composable
private fun RecurringTransactionsContent(
    transactions: List<RecurringTransaction>,
    nextDueDate: (RecurringTransaction) -> LocalDate,
    onItemClick: (RecurringTransaction) -> Unit
) {
    LazyColumn {
        items(transactions, key = { it.transaction.id }) { rec ->
            Column(
                modifier = Modifier
                    .clickable { onItemClick(rec) }
                    .padding(16.dp)
            ) {
                Text(rec.transaction.name, style = MaterialTheme.typography.titleMedium)
                val interval = when (rec.interval) {
                    RecurringInterval.DAILY -> "Daily"
                    RecurringInterval.WEEKLY -> "Weekly"
                    RecurringInterval.MONTHLY -> "Monthly"
                    RecurringInterval.AFTER_CUTOFF -> "Monthly after cutoff"
                }
                Text(interval, style = MaterialTheme.typography.bodySmall)
                val nextDate = nextDueDate(rec)
                Text("Next on $nextDate", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
