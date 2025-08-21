package dev.pandesal.sbp.presentation.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.presentation.accounts.AccountsContent
import dev.pandesal.sbp.presentation.accounts.AccountsUiState
import dev.pandesal.sbp.presentation.accounts.AccountsViewModel
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import dev.pandesal.sbp.presentation.settings.SettingsContent
import dev.pandesal.sbp.presentation.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MoreScreen(
    accountsViewModel: AccountsViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val accountsState by accountsViewModel.uiState.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()
    val nav = LocalNavigationManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = accountsState) {
            is AccountsUiState.Loading ->
                Text("Loading...", modifier = Modifier.padding(16.dp))
            is AccountsUiState.Error ->
                Text(state.message, color = MaterialTheme.colorScheme.error)
            is AccountsUiState.Success -> {
                Text(
                    text = "Accounts",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
                Spacer(Modifier.size(16.dp))
                AccountsContent(
                    accounts = state.accounts,
                    onAddWallet = { nav.navigate(NavigationDestination.NewAccount) },
                    onAccountClick = {},
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.size(24.dp))
            }
        }
        Text(
            text = "Settings",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLargeEmphasized
        )
        Spacer(Modifier.size(16.dp))
        SettingsContent(
            settings = settings,
            onDarkModeChange = settingsViewModel::setDarkMode,
            onNotificationsChange = settingsViewModel::setNotificationsEnabled,
            onDetectFinanceAppUsageChange = settingsViewModel::setDetectFinanceAppUsage,
            onDetectFinanceApps = settingsViewModel::detectFinanceApps,
            onCurrencyChange = settingsViewModel::setCurrency,
            onTravelModeChange = settingsViewModel::setTravelMode,
            onScanSms = settingsViewModel::scanSms
        )
    }
}
