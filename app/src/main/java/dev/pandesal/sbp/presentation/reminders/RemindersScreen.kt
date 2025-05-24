package dev.pandesal.sbp.presentation.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.domain.model.UpcomingReminder
import dev.pandesal.sbp.presentation.components.SkeletonLoader
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun RemindersScreen(viewModel: RemindersViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var selected by remember { mutableStateOf<UpcomingReminder?>(null) }

    when (state) {
        is RemindersUiState.Loading -> SkeletonLoader()
        is RemindersUiState.Success -> {
            val reminders = (state as RemindersUiState.Success).reminders
            RemindersContent(reminders) { selected = it }
        }
        is RemindersUiState.Error -> Text("Error")
    }

    selected?.let { item ->
        ReminderOptionsDialog(
            item = item,
            onDismiss = { selected = null },
            onMarkDone = {
                viewModel.markDone(it)
                selected = null
            },
            onEdit = { /*TODO*/ },
            onConvert = { /*TODO*/ }
        )
    }
}

@Composable
private fun RemindersContent(reminders: List<UpcomingReminder>, onItemClick: (UpcomingReminder) -> Unit) {
    if (reminders.isEmpty()) {
        EmptyState()
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(reminders, key = { it.id }) { item ->
                ReminderCard(item, onClick = { onItemClick(item) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderCard(item: UpcomingReminder, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (item.isRecurring) Icons.Outlined.Repeat else Icons.Outlined.Event,
                    contentDescription = null,
                    tint = if (item.isRecurring) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = item.dueDate.format(DateTimeFormatter.ofPattern("MMM d")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    item.interval?.let { interval ->
                        Text(
                            text = intervalLabel(interval),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .align(Alignment.Start),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            item.amount?.let { amt ->
                Text(
                    text = "₱" + amt.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun intervalLabel(interval: RecurringInterval): String = when (interval) {
    RecurringInterval.DAILY -> "Daily"
    RecurringInterval.WEEKLY -> "Weekly"
    RecurringInterval.MONTHLY -> "Monthly"
    RecurringInterval.AFTER_CUTOFF -> "Monthly"
    RecurringInterval.QUARTERLY -> "Quarterly"
    RecurringInterval.HALF_YEARLY -> "Every 6 Months"
    RecurringInterval.YEARLY -> "Yearly"
}

@Composable
private fun ReminderOptionsDialog(
    item: UpcomingReminder,
    onDismiss: () -> Unit,
    onMarkDone: (UpcomingReminder) -> Unit,
    onEdit: (UpcomingReminder) -> Unit,
    onConvert: (UpcomingReminder) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Column {
                Button(onClick = { onMarkDone(item) }) { Text("Mark Done") }
                Button(onClick = { onEdit(item) }) { Text("Edit") }
                Button(onClick = { onConvert(item) }) { Text("Convert") }
                if (item.isRecurring) {
                    TextButton(onClick = onDismiss) { Text("Skip This") }
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } },
        title = { Text(item.title) }
    )
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Icon(
            imageVector = Icons.Outlined.Event,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text("You're all caught up! No upcoming reminders.", style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview
@Composable
private fun ReminderCardPreview() {
    val item = UpcomingReminder(
        id = "1",
        title = "Electric Bill",
        dueDate = LocalDate.now().plusDays(2),
        interval = RecurringInterval.MONTHLY,
        amount = BigDecimal("1200")
    )
    ReminderCard(item = item, onClick = {})
}
