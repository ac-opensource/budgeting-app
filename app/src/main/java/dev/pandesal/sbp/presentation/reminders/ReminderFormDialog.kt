package dev.pandesal.sbp.presentation.reminders

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate

@Composable
fun ReminderFormDialog(
    date: LocalDate,
    onDismiss: () -> Unit,
    reminderId: String? = null,
    viewModel: ReminderFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var text by remember { mutableStateOf("") }

    when (state) {
        is ReminderFormUiState.Initial -> viewModel.load(null)
        is ReminderFormUiState.Ready -> text = (state as ReminderFormUiState.Ready).text
        else -> {}
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { viewModel.save(date, text, reminderId = reminderId) { onDismiss() } }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = {
            TextField(value = text, onValueChange = { text = it }, label = { Text("Reminder") })
        }
    )
}
