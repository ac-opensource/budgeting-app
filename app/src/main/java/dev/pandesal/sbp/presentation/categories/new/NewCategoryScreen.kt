package dev.pandesal.sbp.presentation.categories.new

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun NewCategoryScreen(
    sheetState: SheetState = rememberModalBottomSheetState(),
    groupId: Int,
    groupName: String,
    initialName: String = "",
    onSubmit: (
        name: String,
        groupId: Int,
        isGoal: Boolean,
        target: BigDecimal?,
        dueDate: LocalDate?
    ) -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedTab by remember { mutableStateOf(0) }
    var targetAmount by remember { mutableStateOf(BigDecimal.ZERO) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

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
            Text(text = "Add Category to Group: $groupName")

            Row(
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
            ) {
                val options = listOf("Budget", "Goal")
                options.forEachIndexed { index, label ->
                    ToggleButton(
                        checked = selectedTab == index,
                        onCheckedChange = { selectedTab = index },
                        modifier = Modifier.weight(1f),
                        shapes = when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        }
                    ) {
                        Text(label, color = if (selectedTab == index) Color.White else Color.Black)
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth()
            )

            if (selectedTab == 1) {
                OutlinedTextField(
                    value = targetAmount.toString(),
                    onValueChange = { targetAmount = it.toBigDecimalOrNull() ?: BigDecimal.ZERO },
                    label = { Text("Target Amount") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedDate?.toString() ?: "Select Due Date")
                    IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.Check, null) }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        onSubmit(
                            name,
                            groupId,
                            selectedTab == 1,
                            if (selectedTab == 1) targetAmount else null,
                            selectedDate.takeIf { selectedTab == 1 }
                        )
                        onDismissRequest()
                    },
                    enabled = name.isNotBlank()
                ) {
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
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    IconButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        showDatePicker = false
                    }) { Icon(Icons.Default.Check, contentDescription = null) }
                },
                dismissButton = {
                    IconButton(onClick = { showDatePicker = false }) { Icon(Icons.Default.Close, contentDescription = null) }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}