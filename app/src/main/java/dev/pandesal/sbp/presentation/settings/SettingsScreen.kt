package dev.pandesal.sbp.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
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

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
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
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLargeEmphasized
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Text("Appearance", style = MaterialTheme.typography.titleMedium)
            SettingSwitch("Dark Mode", settings.darkMode, onDarkModeChange)
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Text("Notifications", style = MaterialTheme.typography.titleMedium)
            SettingSwitch("Enable Notifications", settings.notificationsEnabled) { enabled ->
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

        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Text("Data & Permissions", style = MaterialTheme.typography.titleMedium)
            SettingSwitch("Detect Finance App Usage", settings.detectFinanceAppUsage) { enabled ->
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
            SettingButton("Import SMS Transactions") {
                val permission = Manifest.permission.READ_SMS
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    onScanSms()
                } else {
                    smsLauncher.launch(permission)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Text("Preferences", style = MaterialTheme.typography.titleMedium)
            SettingAction("Currency: ${settings.currency} >") { showCurrencySheet = true }
            SettingSwitch("Travel Mode", settings.isTravelMode, onTravelModeChange)
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

@Composable
private fun SettingButton(title: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        trailingContent = {
            Button(onClick = onClick) { Text("Import") }
        }
    )
}

@Composable
private fun SettingAction(title: String, onClick: () -> Unit) {
    ListItem(
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

