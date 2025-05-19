package dev.pandesal.sbp.presentation.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.extensions.format
import dev.pandesal.sbp.extensions.label
import dev.pandesal.sbp.extensions.currencySymbol
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import dev.pandesal.sbp.presentation.components.SquigglyDivider

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNew by remember { mutableStateOf(false) }
    val navigationManager = LocalNavigationManager.current

    Column {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Accounts",
            style = MaterialTheme.typography.titleLargeEmphasized
        )

        when (val state = uiState) {
            is AccountsUiState.Loading -> {
                Text("Loading...", modifier = Modifier.padding(16.dp))
            }
            is AccountsUiState.Success -> {
                AccountsContent(
                    accounts = state.accounts,
                    onAddWallet = {
                        navigationManager.navigate(NavigationDestination.NewAccount)
                    },
                )
            }
            is AccountsUiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }

}

@Composable
private fun AccountsContent(
    accounts: List<dev.pandesal.sbp.domain.model.Account>,
    onAddWallet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalValue = accounts.sumOf { it.balance.toDouble() }
    val symbol = accounts.firstOrNull()?.currency?.currencySymbol() ?: "₱"

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: $symbol${totalValue.format()}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Button(onClick = onAddWallet) { Text("Add Wallet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary) }
            }
        }
        item {
            SquigglyDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
        }
        items(accounts, key = { it.id }) { account ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text(account.name) },
                    supportingContent = { Text(account.type.label(), style = MaterialTheme.typography.bodySmall) },
                    leadingContent = { Icon(getAccountIcon(account.type), contentDescription = null) },
                    trailingContent = {
                        Text(
                            "${account.currency.currencySymbol()}${account.balance.format()}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
        }
    }
}

private fun getAccountIcon(type: AccountType): ImageVector = when (type) {
    AccountType.CASH_WALLET -> Icons.Outlined.Wallet
    AccountType.MOBILE_DIGITAL_WALLET -> Icons.Outlined.AccountBalanceWallet
    AccountType.BANK_ACCOUNT -> Icons.Outlined.AccountBalance
    AccountType.CREDIT_CARD -> Icons.Outlined.CreditCard
    AccountType.LOAN -> Icons.Outlined.AttachMoney
}
