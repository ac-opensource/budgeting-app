package dev.pandesal.sbp.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
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
import android.app.AppOpsManager
import android.os.Build
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    Column {
        CenterAlignedTopAppBar(title = { Text("Settings") })
        SettingsContent(
            settings = settings,
            onDarkModeChange = viewModel::setDarkMode,
            onNotificationsChange = viewModel::setNotificationsEnabled,
            onDetectFinanceAppUsageChange = viewModel::setDetectFinanceAppUsage,
            onDetectFinanceApps = viewModel::detectFinanceApps,
            onCurrencyChange = viewModel::setCurrency,
            onTravelModeChange = viewModel::setTravelMode,
            onScanSms = viewModel::scanSms
        )
    }
}


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
    onScanSms: () -> Unit
) {
    var showCurrencySheet by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        onNotificationsChange(granted)
    }
    val smsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            onScanSms()
        }
    }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SettingSection("Appearance") {
                SettingSwitch(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    checked = settings.darkMode,
                    onCheckedChange = onDarkModeChange
                )
            }
        }

        item {
            SettingSection("Notifications") {
                SettingSwitch(
                    icon = Icons.Default.Notifications,
                    title = "Enable Notifications",
                    checked = settings.notificationsEnabled
                ) { enabled ->
                    if (enabled) {
                        val permission = Manifest.permission.POST_NOTIFICATIONS
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            onNotificationsChange(true)
                        } else {
                            launcher.launch(permission)
                        }
                    } else {
                        onNotificationsChange(false)
                    }
                }
            }
        }

        item {
            SettingSection("Permissions") {
                SettingSwitch(
                    icon = Icons.Default.BarChart,
                    title = "Detect Finance App Usage",
                    checked = settings.detectFinanceAppUsage
                ) { enabled ->
                    if (enabled) {
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

                SettingButton(
                    icon = Icons.Default.Sms,
                    title = "Import SMS Transactions"
                ) {
                    val permission = Manifest.permission.READ_SMS
                    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                        onScanSms()
                    } else {
                        smsLauncher.launch(permission)
                    }
                }
            }
        }

        item {
            SettingSection("Preferences") {
                SettingAction(
                    icon = Icons.Default.AttachMoney,
                    title = "Currency: ${'$'}{settings.currency} >",
                    onClick = { showCurrencySheet = true }
                )

                SettingSwitch(
                    icon = Icons.Default.Flight,
                    title = "Travel Mode",
                    checked = settings.isTravelMode,
                    onCheckedChange = onTravelModeChange
                )
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

}

@Composable
private fun SettingSwitch(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        leadingContent = { Icon(icon, contentDescription = null) },
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

@Composable
private fun SettingSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        content()
    }
}

@Composable
private fun SettingButton(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    ListItem(
        leadingContent = { Icon(icon, contentDescription = null) },
        headlineContent = { Text(title) },
        trailingContent = {
            Button(onClick = onClick) { Text("Import") }
        }
    )
}

@Composable
private fun SettingAction(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    ListItem(
        leadingContent = { Icon(icon, contentDescription = null) },
        headlineContent = { Text(title) },
        modifier = Modifier.clickable { onClick() }
    )
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    dev.pandesal.sbp.presentation.theme.StopBeingPoorTheme {
        SettingsContent(
            settings = dev.pandesal.sbp.domain.model.Settings(),
            onDarkModeChange = {},
            onNotificationsChange = {},
            onDetectFinanceAppUsageChange = {},
            onDetectFinanceApps = {},
            onCurrencyChange = {},
            onTravelModeChange = {},
            onScanSms = {}
        )
    }
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

