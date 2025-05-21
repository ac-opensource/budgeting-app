package dev.pandesal.sbp.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import dev.pandesal.sbp.presentation.LocalNavigationManager
import android.app.AppOpsManager
import android.os.Build

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val travelSpent by viewModel.travelSpent.collectAsState()
    val nav = LocalNavigationManager.current
    SettingsContent(
        settings = settings,
        onDarkModeChange = viewModel::setDarkMode,
        onNotificationsChange = viewModel::setNotificationsEnabled,
        onDetectFinanceAppUsageChange = viewModel::setDetectFinanceAppUsage,
        onDetectFinanceApps = viewModel::detectFinanceApps,
        onCurrencyChange = viewModel::setCurrency,
        onTravelModeChange = viewModel::setTravelMode,
        onTravelCurrencyChange = viewModel::setTravelCurrency,
        onRecurringTransactionsClick = {
            nav.navigate(dev.pandesal.sbp.presentation.NavigationDestination.RecurringTransactions)
        },
        onScanSms = viewModel::scanSms,
        travelSpent = travelSpent
    )
}

private data class SettingItem(
    val title: String,
    val type: SettingType
)

private enum class SettingType { SWITCH, TEXT }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsContent(
    settings: dev.pandesal.sbp.domain.model.Settings,
    onDarkModeChange: (Boolean) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onDetectFinanceAppUsageChange: (Boolean) -> Unit,
    onDetectFinanceApps: () -> Unit,
    onCurrencyChange: (String) -> Unit,
    onTravelModeChange: (Boolean) -> Unit,
    onTravelCurrencyChange: (String) -> Unit,
    onRecurringTransactionsClick: () -> Unit,
    onScanSms: () -> Unit,
    travelSpent: java.math.BigDecimal
) {
    var darkMode by remember { mutableStateOf(settings.darkMode) }
    var notificationsEnabled by remember { mutableStateOf(settings.notificationsEnabled) }
    var detectFinanceAppUsage by remember { mutableStateOf(settings.detectFinanceAppUsage) }
    var showCurrencySheet by remember { mutableStateOf(false) }
    var showTravelCurrencySheet by remember { mutableStateOf(false) }
    var showTravelTagSheet by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        notificationsEnabled = granted
        onNotificationsChange(granted)
    }
    val smsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            onScanSms()
        }
    }
    val items = buildList {
        add(SettingItem("Dark mode", SettingType.SWITCH))
        add(SettingItem("Enable notifications", SettingType.SWITCH))
        add(SettingItem("Detect Finance App Usage", SettingType.SWITCH))
        add(SettingItem("Currency", SettingType.TEXT))
        add(SettingItem("Travel mode", SettingType.SWITCH))
        if (settings.isTravelMode) {
            add(SettingItem("Travel currency", SettingType.TEXT))
            add(SettingItem("Travel tag", SettingType.TEXT))
        }
        add(SettingItem("Recurring Transactions", SettingType.TEXT))
        add(SettingItem("Import SMS Transactions", SettingType.TEXT))
    }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLargeEmphasized
            )
        }
        if (settings.isTravelMode) {
            item {
                Text(
                    text = "Travel spend: ${settings.travelCurrency} $travelSpent (${settings.currency})",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        items(items) { item ->
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                when (item.title) {
                    "Dark mode" -> SettingSwitch(item.title, darkMode) {
                        darkMode = it
                        onDarkModeChange(it)
                    }
                    "Enable notifications" -> SettingSwitch(item.title, notificationsEnabled) {
                        notificationsEnabled = it
                        if (it) {
                            val permission = Manifest.permission.POST_NOTIFICATIONS
                            val context = context
                            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                onNotificationsChange(true)
                            } else {
                                launcher.launch(permission)
                            }
                        } else {
                            onNotificationsChange(false)
                        }
                    }
                    "Detect Finance App Usage" -> SettingSwitch(item.title, detectFinanceAppUsage) {
                        detectFinanceAppUsage = it
                        if (it) {
                            if (hasUsageAccess(context)) {
                                onDetectFinanceAppUsageChange(true)
                                onDetectFinanceApps()
                            } else {
                                context.startActivity(
                                    android.content.Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
                                        .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                                onDetectFinanceAppUsageChange(true)
                            }
                        } else {
                            onDetectFinanceAppUsageChange(false)
                        }
                    }
                    "Currency" -> SettingText(item.title, settings.currency) {
                        showCurrencySheet = true
                    }
                    "Travel mode" -> SettingSwitch(item.title, settings.isTravelMode) {
                        onTravelModeChange(it)
                    }
                    "Travel currency" -> SettingText(item.title, settings.travelCurrency) {
                        showTravelCurrencySheet = true
                    }
                    "Travel tag" -> SettingText(item.title, settings.travelTag) {
                        showTravelTagSheet = true
                    }
                    "Recurring Transactions" -> SettingText(item.title, "") {
                        onRecurringTransactionsClick()
                    }
                    "Import SMS Transactions" -> SettingText(item.title, "") {
                        val permission = Manifest.permission.READ_SMS
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            onScanSms()
                        } else {
                            smsLauncher.launch(permission)
                        }
                    }
                }
            }
        }
    }

    if (showCurrencySheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val currencies = listOf("PHP", "USD", "EUR", "JPY")
        ModalBottomSheet(onDismissRequest = { showCurrencySheet = false }, sheetState = sheetState) {
            LazyColumn(modifier = Modifier.padding(16.dp).imePadding()) {
                items(currencies) { currency ->
                    ListItem(
                        headlineContent = { Text(currency) },
                        modifier = Modifier.clickable {
                            onCurrencyChange(currency)
                            showCurrencySheet = false
                        }
                    )
                }
            }
        }
    }

    if (showTravelCurrencySheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val currencies = listOf("PHP", "USD", "EUR", "JPY")
        ModalBottomSheet(onDismissRequest = { showTravelCurrencySheet = false }, sheetState = sheetState) {
            LazyColumn(modifier = Modifier.padding(16.dp).imePadding()) {
                items(currencies) { currency ->
                    ListItem(
                        headlineContent = { Text(currency) },
                        modifier = Modifier.clickable {
                            onTravelCurrencyChange(currency)
                            showTravelCurrencySheet = false
                        }
                    )
                }
            }
        }
    }

    if (showTravelTagSheet) {
        TravelTagScreen(onDismissRequest = { showTravelTagSheet = false })
    }
}

@Composable
private fun SettingSwitch(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}

@Composable
private fun SettingText(title: String, value: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(value) },
        modifier = Modifier.clickable { onClick() }
    )
}

private fun hasUsageAccess(context: android.content.Context): Boolean {
    val appOps = context.getSystemService(AppOpsManager::class.java) ?: return false
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
    } else {
        appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

