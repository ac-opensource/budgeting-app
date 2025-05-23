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
import androidx.compose.foundation.lazy.stickyHeader
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    val permissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(notificationsEnabled) {
        if (notificationsEnabled &&
            android.os.Build.VERSION.SDK_INT >= 33
        ) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(permission)
            }
            permissionGranted.value =
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
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

    val pullRefreshState = rememberPullToRefreshState()
    val groupedNotifications = filteredNotifications
        .sortedByDescending { it.timestamp }
        .groupBy {
            val date = it.timestamp.toLocalDate()
            when (date) {
                LocalDate.now() -> "Today"
                LocalDate.now().minusDays(1) -> "Yesterday"
                in LocalDate.now().minusDays(6)..LocalDate.now().minusDays(2) -> "This Week"
                else -> date.format(DateTimeFormatter.ofPattern("MMM d"))
            }
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

        if (notificationsEnabled && !permissionGranted.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Notification permission denied",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = false,
            state = pullRefreshState,
            onRefresh = { viewModel.refresh() }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                if (filteredNotifications.isEmpty()) {
                    item { EmptyState() }
                }
                groupedNotifications.forEach { (label, list) ->
                    stickyHeader {
                        Surface(color = MaterialTheme.colorScheme.surface) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    items(list, key = { it.id }) { notif ->
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
    }
}

@Composable
private fun NotificationItem(
    notification: dev.pandesal.sbp.domain.model.Notification,
    onMarkRead: () -> Unit,
    onCreateTransaction: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (notification.type) {
                        NotificationType.BILL_REMINDER -> Icons.Outlined.Event
                        NotificationType.TRANSACTION_SUGGESTION -> Icons.Outlined.Payments
                        else -> Icons.Outlined.Notifications
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = when (notification.type) {
                            NotificationType.BILL_REMINDER -> "Bill Reminder"
                            NotificationType.TRANSACTION_SUGGESTION -> "Transaction Alert"
                            else -> "Notification"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = notification.timestamp.format(DateTimeFormatter.ofPattern("MMM d, h:mm a")),
                    style = MaterialTheme.typography.labelSmall
                )
                if (notification.canCreateTransaction) {
                    TextButton(onClick = onCreateTransaction) { Text("Add") }
                }
            }
        }
    }
    onMarkRead()
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(text = "You're all caught up!", style = MaterialTheme.typography.bodyMedium)
    }
}
