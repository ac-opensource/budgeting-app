package dev.pandesal.sbp.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.presentation.components.TransactionItem
import dev.pandesal.sbp.presentation.home.DayPopupUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DailySpendPopup(
    date: LocalDate,
    state: DayPopupUiState,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state is DayPopupUiState.Ready,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        val ready = state as DayPopupUiState.Ready
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.padding(top = 4.dp))
                Text("Inflow: ${ready.inflow}", style = MaterialTheme.typography.bodySmall)
                Text("Outflow: ${ready.outflow}", style = MaterialTheme.typography.bodySmall)
                Text("Budget changes: ${ready.budgetChange}", style = MaterialTheme.typography.bodySmall)
                ready.transactions.takeIf { it.isNotEmpty() }?.let { txs ->
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text("Transactions", style = MaterialTheme.typography.titleMedium)
                    txs.forEach { tx ->
                        TransactionItem(tx)
                    }
                }
            }
        }
    }
}
