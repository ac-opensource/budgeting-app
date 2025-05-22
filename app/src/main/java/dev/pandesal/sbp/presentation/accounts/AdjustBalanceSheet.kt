package dev.pandesal.sbp.presentation.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustBalanceSheet(
    currentBalance: BigDecimal,
    onSubmit: (BigDecimal) -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var balanceText by remember { mutableStateOf(currentBalance.toPlainString()) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Adjust Balance", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = balanceText,
                onValueChange = { balanceText = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("New Balance") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) { Text("Cancel") }
                Spacer(Modifier.size(8.dp))
                Button(
                    onClick = { balanceText.toBigDecimalOrNull()?.let(onSubmit) },
                    enabled = balanceText.toBigDecimalOrNull() != null
                ) { Text("Save") }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewAdjustBalanceSheet() {
    AdjustBalanceSheet(
        currentBalance = BigDecimal("1000"),
        onSubmit = {},
        onCancel = {},
        onDismissRequest = {}
    )
}
