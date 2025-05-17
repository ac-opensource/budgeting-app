package dev.pandesal.sbp.presentation.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.domain.model.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAccountScreen(
    sheetState: SheetState = rememberModalBottomSheetState(),
    onSubmit: (name: String, type: AccountType) -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.CASH_WALLET) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Account Name") },
                modifier = Modifier.fillMaxWidth()
            )
            // Simple type selection via buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AccountType.values().forEach { type ->
                    Button(onClick = { selectedType = type }) {
                        val label = type.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercaseChar() }
                        Text(label)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        onSubmit(name, selectedType)
                        onDismissRequest()
                    },
                    enabled = name.isNotBlank()
                ) { Text("Save") }
                OutlinedButton(onClick = {
                    onCancel()
                    onDismissRequest()
                }) { Text("Cancel") }
            }
        }
    }
}
