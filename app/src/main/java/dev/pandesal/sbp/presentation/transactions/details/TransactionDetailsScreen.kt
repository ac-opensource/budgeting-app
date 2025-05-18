package dev.pandesal.sbp.presentation.transactions.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.Transaction
import java.math.BigDecimal

@Composable
fun TransactionDetailsScreen(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    viewModel: TransactionDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(transaction) { viewModel.setTransaction(transaction) }
    val txState = viewModel.transaction.collectAsState()
    val tx = txState.value ?: return

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = tx.name,
            onValueChange = { viewModel.updateTransaction(tx.copy(name = it)) },
            label = { Text("Name") }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            value = if (tx.amount == BigDecimal.ZERO) "" else tx.amount.toPlainString(),
            onValueChange = { input ->
                val newAmount = input.toBigDecimalOrNull() ?: BigDecimal.ZERO
                viewModel.updateTransaction(tx.copy(amount = newAmount))
            },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
            Spacer(Modifier.padding(4.dp))
            Button(onClick = { viewModel.save { if (it) onSave() } }) {
                Text("Save")
            }
        }
    }
}
