package dev.pandesal.sbp.presentation.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.twotone.ArrowDropDown
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ListItem
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.vector.ImageVector
import java.math.BigDecimal
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.extensions.label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAccountScreen(
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val navigationManager = LocalNavigationManager.current

    NewAccountScreen(
        onSubmit = { name, type, balance, currency, contractValue, monthlyPayment ->
            viewModel.addAccount(name, type, balance, currency, contractValue, monthlyPayment)
        },
        onCancel = { },
        onDismissRequest = {
            navigationManager.navigateUp()
        }
    )

}

private fun getAccountIcon(type: AccountType): ImageVector = when (type) {
    AccountType.CASH_WALLET -> Icons.Outlined.Wallet
    AccountType.MOBILE_DIGITAL_WALLET -> Icons.Outlined.AccountBalanceWallet
    AccountType.BANK_ACCOUNT -> Icons.Outlined.AccountBalance
    AccountType.CREDIT_CARD -> Icons.Outlined.CreditCard
    AccountType.LOAN -> Icons.Outlined.AttachMoney
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NewAccountScreen(
    sheetState: SheetState = rememberModalBottomSheetState(),
    onSubmit: (name: String, type: AccountType, balance: BigDecimal, currency: String, contractValue: String?, monthlyPayment: String?) -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.CASH_WALLET) }
    var currency by remember { mutableStateOf("PHP") }
    var contractValue by remember { mutableStateOf("") }
    var monthlyPayment by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf(BigDecimal.ZERO) }
    var showCurrencySheet by remember { mutableStateOf(false) }
    var showTypeSheet by remember { mutableStateOf(false) }
    var paidAmount by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.weight(1f))

        ElevatedCard(
            modifier = Modifier.align(Alignment.End),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
        ) {
            IconButton(
                modifier = Modifier
                    .height(24.dp)
                    .padding(4.dp),
                onClick = {
                    onCancel()
                    onDismissRequest()
                }
            ) {
                Icon(Icons.Filled.Close, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedCard(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(10),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Account Name", style = MaterialTheme.typography.bodyMedium)
                ElevatedCard(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth()
                ) {
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                val balanceLabel = when {
                    selectedType == AccountType.LOAN && paidAmount -> "Paid Amount"
                    selectedType == AccountType.LOAN && !paidAmount -> "Remaining Balance"
                    else -> "Initial Balance"
                }
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(balanceLabel, style = MaterialTheme.typography.bodyMedium)
                    ElevatedCard(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                    ) {
                        BasicTextField(
                            value = if (balance == BigDecimal.ZERO) "" else balance.toPlainString(),
                            onValueChange = {
                                balance = it.toBigDecimalOrNull() ?: BigDecimal.ZERO
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text("Currency", style = MaterialTheme.typography.bodyMedium)
                    ElevatedCard(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                            .clickable { showCurrencySheet = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                currency,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(
                                Icons.TwoTone.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }

                if (selectedType == AccountType.LOAN) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text("Total Contract Value", style = MaterialTheme.typography.bodyMedium)
                        ElevatedCard(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .fillMaxWidth()
                        ) {
                            BasicTextField(
                                value = contractValue,
                                onValueChange = { contractValue = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text("Monthly Payment", style = MaterialTheme.typography.bodyMedium)
                        ElevatedCard(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .fillMaxWidth()
                        ) {
                            BasicTextField(
                                value = monthlyPayment,
                                onValueChange = { monthlyPayment = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                }

                if (selectedType == AccountType.LOAN) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Paid Amount")
                        Switch(
                            checked = paidAmount,
                            onCheckedChange = { paidAmount = it },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text("Remaining Balance")
                    }
                }
            }
        }

        if (showCurrencySheet) {
            val currencySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val currencies = listOf("PHP", "USD", "EUR", "JPY")
            ModalBottomSheet(
                onDismissRequest = { showCurrencySheet = false },
                sheetState = currencySheetState
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(currencies) { currencyOption ->
                        ListItem(
                            headlineContent = { Text(currencyOption) },
                            modifier = Modifier.clickable {
                                currency = currencyOption
                                showCurrencySheet = false
                            }
                        )
                    }
                }
            }
        }

        if (showTypeSheet) {
            val typeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showTypeSheet = false },
                sheetState = typeSheetState
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(AccountType.values()) { type ->
                        ListItem(
                            leadingContent = {
                                Icon(
                                    getAccountIcon(type),
                                    contentDescription = null
                                )
                            },
                            headlineContent = { Text(type.label()) },
                            modifier = Modifier.clickable {
                                selectedType = type
                                showTypeSheet = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalFloatingToolbar(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            expanded = true,
            floatingActionButton = {
                FloatingToolbarDefaults.VibrantFloatingActionButton(
                    onClick = {
                        onSubmit(
                            name,
                            selectedType,
                            balance,
                            currency,
                            contractValue.ifBlank { null },
                            monthlyPayment.ifBlank { null }
                        )
                        onDismissRequest()
                    }
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            },
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { showTypeSheet = true }
                ) {
                    Icon(getAccountIcon(selectedType), contentDescription = null)
                    Text(
                        selectedType.label(),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Icon(
                        Icons.TwoTone.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        )
    }
}
