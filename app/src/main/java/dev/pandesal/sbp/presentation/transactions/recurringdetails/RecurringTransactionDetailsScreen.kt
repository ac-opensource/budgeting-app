package dev.pandesal.sbp.presentation.transactions.recurringdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.presentation.LocalNavigationManager

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RecurringTransactionDetailsScreen(
    id: String,
    viewModel: RecurringTransactionDetailsViewModel = hiltViewModel()
) {
    val navManager = LocalNavigationManager.current

    LaunchedEffect(id) { viewModel.loadTransaction(id) }

    val txState = viewModel.transaction.collectAsState()
    val rec = txState.value ?: return

    var editable by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.weight(1f))

        ElevatedCard(
            modifier = Modifier.align(Alignment.End),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
        ) {
            IconButton(
                modifier = Modifier
                    .height(24.dp)
                    .padding(4.dp),
                onClick = { navManager.navigateUp() }
            ) {
                Icon(Icons.Filled.Close, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedCard(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(10),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = rec.transaction.name,
                    onValueChange = { viewModel.update(rec.copy(transaction = rec.transaction.copy(name = it))) },
                    enabled = editable,
                    label = { Text("Name") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    value = rec.transaction.amount.toPlainString(),
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Amount") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    value = rec.interval.name,
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Interval") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    value = rec.startDate.toString(),
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Start Date") }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = rec.reminderEnabled,
                        onCheckedChange = {
                            viewModel.update(rec.copy(reminderEnabled = it))
                        },
                        enabled = editable
                    )
                    Text(
                        text = "Reminders",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalFloatingToolbar(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            expanded = true,
            floatingActionButton = {
                FloatingToolbarDefaults.VibrantFloatingActionButton(
                    onClick = {
                        if (editable) viewModel.save { if (it) navManager.navigateUp() } else editable = true
                    }
                ) {
                    Icon(if (editable) Icons.Default.Check else Icons.Default.Edit, contentDescription = null)
                }
            },
            content = {
                if (!editable) {
                    IconButton(onClick = { viewModel.delete { if (it) navManager.navigateUp() } }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                }
            }
        )
    }
}
