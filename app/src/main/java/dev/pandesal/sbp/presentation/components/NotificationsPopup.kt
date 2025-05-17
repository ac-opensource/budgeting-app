package dev.pandesal.sbp.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotificationsPopup(
    notifications: List<String>,
    expanded: Boolean,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        if (notifications.isEmpty()) {
            DropdownMenuItem(text = { Text("No notifications") }, onClick = { })
        } else {
            notifications.forEachIndexed { index, notif ->
                DropdownMenuItem(text = { Text(notif) }, onClick = {})
                if (index != notifications.lastIndex) {
                    Divider(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                }
            }
        }
    }
}

