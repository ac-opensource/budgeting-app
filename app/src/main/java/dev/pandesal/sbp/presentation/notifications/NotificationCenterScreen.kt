package dev.pandesal.sbp.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.notification.InAppNotificationCenter
import dev.pandesal.sbp.presentation.LocalNavigationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen() {
    val navController = LocalNavigationManager.current
    val notifications by InAppNotificationCenter.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(notifications, key = { it.id }) { notif ->
                val dismissState = rememberDismissState(confirmValueChange = {
                    if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                        InAppNotificationCenter.archive(notif.id)
                        true
                    } else {
                        false
                    }
                })
                SwipeToDismiss(
                    state = dismissState,
                    background = {},
                    directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart)
                ) {
                    NotificationItem(
                        notification = notif,
                        onMarkRead = { InAppNotificationCenter.markAsRead(notif.id) },
                        onCreateTransaction = {
                            InAppNotificationCenter.markAsRead(notif.id)
                            navController.navigate(dev.pandesal.sbp.presentation.NavigationDestination.NewTransaction)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: dev.pandesal.sbp.domain.model.Notification,
    onMarkRead: () -> Unit,
    onCreateTransaction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!notification.isRead) {
                    Box(
                        Modifier
                            .padding(end = 8.dp)
                            .background(color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small)
                            .padding(4.dp)
                    ) {}
                }
                Text(notification.message)
            }
            if (notification.canCreateTransaction) {
                TextButton(onClick = onCreateTransaction) {
                    Text("Add")
                }
            }
        }
    }
    onMarkRead()
}
