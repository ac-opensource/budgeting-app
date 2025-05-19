package dev.pandesal.sbp.presentation.categories.new

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCategoryGroupScreen(
    sheetState: SheetState = rememberModalBottomSheetState(),
    initialName: String = "",
    onSubmit: (String) -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Group Name")
            ElevatedCard(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
            ) {
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = {
                    onSubmit(name)
                    onDismissRequest()
                }, enabled = name.isNotBlank()) {
                    Text("Save")
                }
                OutlinedButton(onClick = {
                    onCancel()
                    onDismissRequest()
                }) {
                    Text("Cancel")
                }
            }
        }
    }
}