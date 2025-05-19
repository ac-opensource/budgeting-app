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
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    SettingsContent(
        settings = settings,
        onDarkModeChange = viewModel::setDarkMode,
        onNotificationsChange = viewModel::setNotificationsEnabled,
        onCurrencyChange = viewModel::setCurrency
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
    onCurrencyChange: (String) -> Unit
) {
    var darkMode by remember { mutableStateOf(settings.darkMode) }
    var notificationsEnabled by remember { mutableStateOf(settings.notificationsEnabled) }
    var showCurrencySheet by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        notificationsEnabled = granted
        onNotificationsChange(granted)
    }
    val items = listOf(
        SettingItem("Dark mode", SettingType.SWITCH),
        SettingItem("Enable notifications", SettingType.SWITCH),
        SettingItem("Currency", SettingType.TEXT)
    )

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
                    "Currency" -> SettingText(item.title, settings.currency) {
                        showCurrencySheet = true
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

