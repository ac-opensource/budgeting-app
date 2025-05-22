package dev.pandesal.sbp.presentation.insights.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import dev.pandesal.sbp.presentation.insights.DayTooltipUiState
import dev.pandesal.sbp.presentation.components.TransactionItem

@Composable
fun DateTooltip(
    date: LocalDate,
    modifier: Modifier = Modifier,
    state: DayTooltipUiState,
    onClose: () -> Unit,
    onAddReminder: () -> Unit,
) {
    AnimatedVisibility(
        visible = state is DayTooltipUiState.Ready,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            val ready = state as? DayTooltipUiState.Ready
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onAddReminder) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                ready?.transactions?.takeIf { it.isNotEmpty() }?.let { txs ->
                    Text("Transactions", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            txs.forEach { tx -> TransactionItem(tx) }
                        }
                    }
                }
                ready?.recurring?.takeIf { it.isNotEmpty() }?.let { recs ->
                    Text("Recurring", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            recs.forEach { rec -> TransactionItem(rec.transaction) }
                        }
                    }
                }
                ready?.reminders?.takeIf { it.isNotEmpty() }?.let { rems ->
                    Text("Reminders", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            rems.forEach { r -> Text("• ${r.message}") }
                        }
                    }
                }
            }
        }
    }
}
