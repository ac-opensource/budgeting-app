package dev.pandesal.sbp.presentation.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.clickable
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.extensions.format
import dev.pandesal.sbp.extensions.label
import dev.pandesal.sbp.extensions.currencySymbol
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import dev.pandesal.sbp.presentation.components.SquigglyDivider
import dev.pandesal.sbp.presentation.transactions.TransactionsContent
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel = hiltViewModel(),
    txViewModel: AccountTransactionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val transactions by txViewModel.transactions.collectAsState()
    val navigationManager = LocalNavigationManager.current

    var selectedAccount by remember { mutableStateOf<dev.pandesal.sbp.domain.model.Account?>(null) }
    var showRename by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }

    val scaffoldState = rememberBottomSheetScaffoldState()
    var isIconExpanded by remember { mutableStateOf(scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(scaffoldState.bottomSheetState.targetValue) {
        isIconExpanded = scaffoldState.bottomSheetState.targetValue == SheetValue.Expanded
    }

    LaunchedEffect(selectedAccount) {
        selectedAccount?.let {
            txViewModel.load(it.id.toString())
            scope.launch { scaffoldState.bottomSheetState.expand() }
        }
    }

    if (showRename && selectedAccount != null) {
        RenameAccountSheet(
            currentName = selectedAccount!!.name,
            onSubmit = { name ->
                viewModel.updateAccountName(selectedAccount!!, name)
                showRename = false
            },
            onCancel = { showRename = false },
            onDismissRequest = { showRename = false }
        )
    }

    if (showDelete && selectedAccount != null) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteAccount(selectedAccount!!)
                    showDelete = false
                    selectedAccount = null
                }) { Text("Delete") }
            },
            dismissButton = {
                Button(onClick = { showDelete = false }) { Text("Cancel") }
            },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete this account?") }
        )
    }

    BottomSheetScaffold(
        containerColor = Color.Transparent,
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShadowElevation = 16.dp,
        sheetContent = {
            TransactionsContent(
                transactions = transactions,
                onNewTransactionClick = { navigationManager.navigate(NavigationDestination.NewTransaction()) },
                onTransactionClick = { navigationManager.navigate(NavigationDestination.TransactionDetails(it.id)) }
            )
        },
        sheetDragHandle = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .semantics { contentDescription = "drag handle" },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(Modifier.size(width = 32.dp, height = 4.dp))
                }

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Transactions", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { showRename = true }, enabled = selectedAccount != null) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                    IconButton(onClick = { showDelete = true }, enabled = selectedAccount != null) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                    IconButton(onClick = {
                        isIconExpanded = !isIconExpanded
                        scope.launch {
                            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                scaffoldState.bottomSheetState.partialExpand()
                            } else {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    }) {
                        androidx.compose.animation.Crossfade(targetState = isIconExpanded, label = "icon crossfade") { expanded ->
                            if (expanded) {
                                Icon(Icons.Default.CloseFullscreen, contentDescription = null)
                            } else {
                                Icon(Icons.Default.Fullscreen, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is AccountsUiState.Loading -> {
                Text("Loading...", modifier = Modifier.padding(16.dp))
            }
            is AccountsUiState.Success -> {

                Column {
                    Text(
                        text = "Accounts",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLargeEmphasized
                    )
                    Spacer(Modifier.size(16.dp))
                    AccountsContent(
                        accounts = state.accounts,
                        onAddWallet = { navigationManager.navigate(NavigationDestination.NewAccount) },
                        onAccountClick = { selectedAccount = it },
                        modifier = Modifier.padding(padding)
                    )
                }

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
    onAccountClick: (dev.pandesal.sbp.domain.model.Account) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalValue = accounts.fold(java.math.BigDecimal.ZERO) { acc, accnt -> acc + accnt.balance }
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
                Column {
                    Text("Consolidated Account", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = "$symbol${totalValue.format()}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                TextButton(onClick = onAddWallet) { Text("Add Account", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground) }
            }
        }
        item {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
        }
        items(accounts, key = { it.id }) { account ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAccountClick(account) }
            ) {
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
    AccountType.LOAN_FOR_ASSET, AccountType.LOAN_FOR_SPENDING -> Icons.Outlined.AttachMoney
}
