package dev.pandesal.sbp.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.pandesal.sbp.presentation.insights.TimePeriod

@Composable
fun TimePeriodDropdown(
    period: TimePeriod,
    onPeriodChange: (TimePeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        TextButton(onClick = { expanded = true }) {
            Text(period.label)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TimePeriod.values().forEach { p ->
                DropdownMenuItem(
                    text = { Text(p.label) },
                    onClick = {
                        expanded = false
                        onPeriodChange(p)
                    }
                )
            }
        }
    }
}
