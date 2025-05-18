package dev.pandesal.sbp.presentation.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.math.BigDecimal
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.presentation.LocalNavigationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAccountScreen(
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val navigationManager = LocalNavigationManager.current

    NewAccountScreen(
        onSubmit = { name, type, balance, currency ->
            viewModel.addAccount(name, type, balance, currency)
        },
        onCancel = { },
        onDismissRequest = {
            navigationManager.navigateUp()
        }
    )

}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NewAccountScreen(
    sheetState: SheetState = rememberModalBottomSheetState(),
    onSubmit: (name: String, type: AccountType, balance: BigDecimal, currency: String) -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.CASH_WALLET) }
    var currency by remember { mutableStateOf("PHP") }
    var balance by remember { mutableStateOf(BigDecimal.ZERO) }
    var showCurrencySheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Account Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = balance.toString(),
                    onValueChange = { balance = it.toBigDecimalOrNull() ?: BigDecimal.ZERO },
                    label = { Text("Initial Balance") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = currency,
                    onValueChange = { },
                    label = { Text("Currency") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { showCurrencySheet = true },
                    readOnly = true
                )

                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                ) {
                    val allTypes = AccountType.values()
                    allTypes.forEachIndexed { index, type ->
                        ToggleButton(
                            checked = selectedType == type,
                            onCheckedChange = { selectedType = type },
                            modifier = Modifier.weight(1f),
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                allTypes.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            }
                        ) {
                            val label = type.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercaseChar() }
                            Text(
                                label,
                                color = if (selectedType == type) Color.White else Color.Black
                            )
                        }
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

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalFloatingToolbar(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            expanded = true,
            floatingActionButton = {
                FloatingToolbarDefaults.VibrantFloatingActionButton(
                    onClick = {
                        onSubmit(name, selectedType, balance, currency)
                        onDismissRequest()
                    }
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            },
            content = {}
        )
    }
}
