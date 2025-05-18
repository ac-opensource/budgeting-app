package dev.pandesal.sbp.presentation.transactions.newtransaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.twotone.ArrowDropDown
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.components.SkeletonLoader
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId

@Composable
fun NewTransactionScreen(
    viewModel: NewTransactionsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val navManager = LocalNavigationManager.current

    if (uiState.value is NewTransactionUiState.Initial) {
        SkeletonLoader()
    } else if (uiState.value is NewTransactionUiState.Success) {
        val state = uiState.value as NewTransactionUiState.Success
        NewTransactionScreen(
            state.groupedCategories,
            state.accounts,
            state.transaction,
            state.merchants,
            onSave = {
                viewModel.saveTransaction {
                    navManager.navigateUp()
                }
            },
            onCancel = {
                navManager.navigateUp()
            },
            onUpdate = {
                viewModel.updateTransaction(it)
            },
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NewTransactionScreen(
    groupedCategories: Map<CategoryGroup, List<Category>>,
    accounts: List<Account>,
    transaction: Transaction,
    merchants: List<String>,
    onSave: (Transaction) -> Unit,
    onCancel: () -> Unit,
    onUpdate: (Transaction) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = transaction.createdAt.atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
    )

    val flatList: List<Any> =
        groupedCategories.filter { it.value.isNotEmpty() }.flatMap { (group, categories) ->
            listOf(group) + categories
        }

    val showDatePicker = remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var merchantExpanded by remember { mutableStateOf(false) }
    var fromAccountExpanded by remember { mutableStateOf(false) }
    var toAccountExpanded by remember { mutableStateOf(false) }

    // Transaction Type Tabs
    val transactionTypes = listOf(TransactionType.INFLOW, TransactionType.OUTFLOW, TransactionType.TRANSFER)
    var selectedIndex by remember(transaction.transactionType) {
        mutableIntStateOf(transactionTypes.indexOf(transaction.transactionType))
    }

    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {

        Spacer(modifier = Modifier.weight(1f))

        ElevatedCard(
            modifier = Modifier.align(Alignment.End),
            shape = RoundedCornerShape(50),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 16.dp
            ),
        ) {
            IconButton(
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp),
                onClick = { onCancel() }) {
                Icon(Icons.Filled.Close, "Localized description")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedCard(
            shape = RoundedCornerShape(50),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 16.dp
            ),
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val amountText =
                    if (transaction.amount == BigDecimal.ZERO) "" else transaction.amount.toPlainString()

                BasicTextField(
                    value = amountText,
                    onValueChange = { input ->
                        val newAmount = input.toBigDecimalOrNull() ?: BigDecimal.ZERO
                        onUpdate(transaction.copy(amount = newAmount))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (amountText.isEmpty()) "0" else amountText,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    textAlign = TextAlign.Center,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1,
                                modifier = Modifier.fillMaxWidth()
                            )
                            innerTextField()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedCard(
            shape = RoundedCornerShape(10),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 16.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Category
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text("Category", style = MaterialTheme.typography.bodyMedium)
                    ElevatedCard(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                            .clickable { expanded = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = transaction.category?.name ?: "Select Category",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )

                            Icon(
                                Icons.TwoTone.ArrowDropDown,
                                contentDescription = "Reorder",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }

                    if (expanded) {
                        ModalBottomSheet(onDismissRequest = { expanded = false }) {
                            LazyColumn {
                                items(flatList) { item ->
                                    when (item) {
                                        is CategoryGroup -> {
                                            Text(
                                                text = item.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                            )
                                        }

                                        is Category -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "-",
                                                    style = MaterialTheme.typography.headlineLarge,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier
                                                        .clickable {
                                                            onUpdate(transaction.copy(category = item))
                                                            expanded = false
                                                        }
                                                        .padding(start = 16.dp)
                                                )

                                                Text(
                                                    text = item.name,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clickable {
                                                            onUpdate(transaction.copy(category = item))
                                                            expanded = false
                                                        }
                                                        .padding(
                                                            horizontal = 8.dp,
                                                            vertical = 12.dp
                                                        )
                                                )
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = transactionTypes[selectedIndex] == TransactionType.OUTFLOW || transactionTypes[selectedIndex] == TransactionType.TRANSFER
                ) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text("From Account", style = MaterialTheme.typography.bodyMedium)
                        ElevatedCard(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .fillMaxWidth()
                                .clickable { fromAccountExpanded = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = transaction.from ?: "Select Source Account",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp)
                                )

                                Icon(
                                    Icons.TwoTone.Wallet,
                                    contentDescription = "Reorder",
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(24.dp)
                                )
                            }
                        }
                    }

                    if (fromAccountExpanded) {
                        ModalBottomSheet(onDismissRequest = { fromAccountExpanded = false }) {
                            LazyColumn {
                                items(accounts) { account ->
                                    Text(
                                        text = account.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onUpdate(transaction.copy(from = account.name))
                                                fromAccountExpanded = false
                                            }
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }


                AnimatedVisibility(
                    visible = transactionTypes[selectedIndex] == TransactionType.OUTFLOW
                ) {
                    // Merchant
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text("To / Merchant", style = MaterialTheme.typography.bodyMedium)
                        ElevatedCard(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                BasicTextField(
                                    value = transaction.merchantName.orEmpty(),
                                    onValueChange = { onUpdate(transaction.copy(merchantName = it)) },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .weight(1f)
                                        .padding(8.dp),
                                )

                                Icon(
                                    Icons.TwoTone.Favorite,
                                    contentDescription = "Reorder",
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(24.dp)
                                        .clickable { merchantExpanded = true }
                                )
                            }
                        }
                    }

                    if (merchantExpanded) {
                        ModalBottomSheet(onDismissRequest = { merchantExpanded = false }) {
                            LazyColumn {
                                items(merchants) { item ->
                                    Text(
                                        text = item,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onUpdate(transaction.copy(merchantName = item))
                                                merchantExpanded = false
                                            }
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = transactionTypes[selectedIndex] == TransactionType.INFLOW || transactionTypes[selectedIndex] == TransactionType.TRANSFER
                ) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text("To Account", style = MaterialTheme.typography.bodyMedium)
                        ElevatedCard(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .fillMaxWidth()
                                .clickable { toAccountExpanded = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = transaction.to ?: "Select Destination Account",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp)
                                )

                                Icon(
                                    Icons.TwoTone.Wallet,
                                    contentDescription = "Reorder",
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(24.dp)
                                )
                            }
                        }
                    }

                    if (toAccountExpanded) {
                        ModalBottomSheet(onDismissRequest = { toAccountExpanded = false }) {
                            LazyColumn {
                                items(accounts) { account ->
                                    Text(
                                        text = account.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onUpdate(transaction.copy(to = account.name))
                                                toAccountExpanded = false
                                            }
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }


                // Date
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text("Transaction Date", style = MaterialTheme.typography.bodyMedium)
                    ElevatedCard(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                            .clickable { showDatePicker.value = true }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = transaction.createdAt.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )

                            Icon(
                                Icons.TwoTone.DateRange,
                                contentDescription = "Reorder",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        HorizontalFloatingToolbar(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            expanded = true,
            floatingActionButton = {
                FloatingToolbarDefaults.VibrantFloatingActionButton(
                    onClick = {
                        onSave(transaction)
                    },
                ) {
                    Icon(Icons.Default.Check, "Localized description")
                }

            },
            content = {
                val options = listOf("Inflow", "Outflow", "Transfer")

                Row(
                    Modifier.padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                ) {
                    val modifiers = listOf(
                        Modifier.wrapContentSize(),
                        Modifier.wrapContentSize(),
                        Modifier.wrapContentSize()
                    )

                    options.forEachIndexed { index, label ->
                        ToggleButton(
                            checked = selectedIndex == index,
                            onCheckedChange = {
                                selectedIndex = index
                                onUpdate(transaction.copy(transactionType = transactionTypes[index]))

                            },
                            modifier = modifiers[index],
                            shapes =
                                when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                }
                        ) {
                            Text(
                                label,
                                color = if (selectedIndex == index) Color.White else Color.Black,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            },
        )

    }

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onUpdate(transaction.copy(createdAt = newDate))
                    }
                    showDatePicker.value = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDatePicker.value = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
