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
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.notification.InAppNotificationCenter
import dev.pandesal.sbp.domain.model.NotificationType
import dev.pandesal.sbp.presentation.LocalNavigationManager
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.presentation.notifications.NotificationCenterViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(viewModel: NotificationCenterViewModel = hiltViewModel()) {
    val navController = LocalNavigationManager.current
    val coroutineScope = rememberCoroutineScope()
    val notifications by viewModel.notifications.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.setNotificationsEnabled(granted)
    }

    LaunchedEffect(notificationsEnabled) {
        if (notificationsEnabled &&
            android.os.Build.VERSION.SDK_INT >= 33
        ) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(permission)
            }
        }
    }
    val tabTitles = listOf(
        "All",
        "Upcoming/Bills",
        "Transactions"
    )
    val selectedIndex = remember { mutableIntStateOf(0) }
    val filteredNotifications = when (selectedIndex.intValue) {
        1 -> notifications.filter { it.type == NotificationType.BILL_REMINDER }
        2 -> notifications.filter { it.type == NotificationType.TRANSACTION_SUGGESTION }
        else -> notifications
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        PrimaryTabRow(
            selectedTabIndex = selectedIndex.intValue,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedIndex.intValue == index,
                    onClick = { selectedIndex.intValue = index },
                    text = { Text(title) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredNotifications, key = { it.id }) { notif ->
                val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
                        InAppNotificationCenter.archive(notif.id)
                        true
                    } else {
                        false
                    }
                })
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {},
                ) {
                    NotificationItem(
                        notification = notif,
                        onMarkRead = { InAppNotificationCenter.markAsRead(notif.id) },
                        onCreateTransaction = {
                            coroutineScope.launch {
                                val tx = viewModel.parseTransaction(notif.message)
                                InAppNotificationCenter.markAsRead(notif.id)
                                navController.navigate(dev.pandesal.sbp.presentation.NavigationDestination.NewTransaction(tx))
                            }
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
