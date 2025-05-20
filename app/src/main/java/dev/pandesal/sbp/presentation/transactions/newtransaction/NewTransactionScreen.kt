package dev.pandesal.sbp.presentation.transactions.newtransaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.twotone.ArrowDropDown
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.Wallet
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import dev.pandesal.sbp.presentation.components.SkeletonLoader
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.delay

@Composable
fun NewTransactionScreen(
    transactionId: String? = null,
    readOnly: Boolean = false,
    viewModel: NewTransactionsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val navManager = LocalNavigationManager.current

    LaunchedEffect(transactionId) {
        transactionId?.let { viewModel.loadTransaction(it) }
    }

    if (uiState.value is NewTransactionUiState.Initial) {
        SkeletonLoader()
    } else if (uiState.value is NewTransactionUiState.Success) {
        val state = uiState.value as NewTransactionUiState.Success
        var editable by remember { mutableStateOf(!readOnly) }
        var showDelete by remember { mutableStateOf(false) }
        NewTransactionScreen(
            groupedCategories = state.groupedCategories,
            accounts = state.accounts,
            transaction = state.transaction,
            merchants = state.merchants,
            editable = editable,
            onEdit = { editable = true },
            onDelete = { showDelete = true },
            onSave = { _, recur, interval, cutoff, reminder ->
                viewModel.saveTransaction(recur, interval, cutoff, reminder) {
                    navManager.navigateUp()
                }
            },
            onCancel = {
                navManager.navigateUp()
            },
            onUpdate = {
                viewModel.updateTransaction(it)
            }
        )

        if (showDelete) {
            AlertDialog(
                onDismissRequest = { showDelete = false },
                confirmButton = {
                    Button(onClick = {
                        viewModel.deleteTransaction(state.transaction) {
                            navManager.navigateUp()
                        }
                        showDelete = false
                    }) { Text("Delete") }
                },
                dismissButton = {
                    Button(onClick = { showDelete = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Delete Transaction") },
                text = { Text("Are you sure you want to delete this transaction?") }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NewTransactionScreen(
    groupedCategories: Map<CategoryGroup, List<Category>>,
    accounts: List<Account>,
    transaction: Transaction,
    merchants: List<String>,
    editable: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSave: (Transaction, Boolean, RecurringInterval, Int, Boolean) -> Unit,
    onCancel: () -> Unit,
    onUpdate: (Transaction) -> Unit
) {
    val navManager = LocalNavigationManager.current

    if (accounts.isEmpty()) {
        AlertDialog(
            onDismissRequest = {
                navManager.navigateUp()
                navManager.navigate(NavigationDestination.Accounts)
            },
            confirmButton = {
                TextButton(onClick = {
                    navManager.navigateUp()
                    navManager.navigate(NavigationDestination.Accounts)
                }) { Text("Add Account") }
            },
            title = { Text("No Accounts") },
            text = { Text("Please add an account first.") }
        )
        return
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = transaction.createdAt.atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
    )

    val flatList: List<Any> =
        groupedCategories
            .mapValues { entry ->
                entry.value.filter { it.categoryType == transaction.transactionType }
            }
            .filter { it.value.isNotEmpty() }
            .flatMap { (group, categories) ->
                listOf(group) + categories
            }

    val showDatePicker = remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var merchantExpanded by remember { mutableStateOf(false) }
    var fromAccountExpanded by remember { mutableStateOf(false) }
    var toAccountExpanded by remember { mutableStateOf(false) }
    var isRecurring by remember { mutableStateOf(false) }
    var recurringExpanded by remember { mutableStateOf(false) }
    var selectedInterval by remember { mutableStateOf(RecurringInterval.MONTHLY) }
    var cutoffDays by remember { mutableIntStateOf(21) }
    var reminderEnabled by remember { mutableStateOf(false) }

    // Transaction Type Tabs
    val transactionTypes =
        listOf(TransactionType.INFLOW, TransactionType.OUTFLOW, TransactionType.TRANSFER)
    var selectedIndex by remember(transaction.transactionType) {
        mutableIntStateOf(transactionTypes.indexOf(transaction.transactionType))
    }

    var showClose by remember { mutableStateOf(false) }
    var showAmount by remember { mutableStateOf(false) }
    var showInputs by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showClose = true
        delay(300)
        showAmount = true
        delay(300)
        showInputs = true
        delay(300)
        showButtons = true
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .imePadding(),
            verticalArrangement = Arrangement.Bottom
        ) {

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(
                modifier = Modifier.align(Alignment.End),
                visible = showClose,
                enter = fadeIn(animationSpec = tween(300)) +
                        slideInVertically(animationSpec = tween(300), initialOffsetY = { it / 2 })
            ) {
                ElevatedCard(
                    modifier = Modifier.wrapContentSize().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(50),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 16.dp
                    ),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            modifier = Modifier
                                .height(24.dp)
                                .padding(4.dp),
                            onClick = { onCancel() }) {
                            Icon(Icons.Filled.Close, "Localized description")
                        }
                        if (!editable) {
                            IconButton(
                                modifier = Modifier
                                    .height(24.dp)
                                    .padding(4.dp),
                                onClick = onEdit
                            ) {
                                Icon(Icons.Filled.Edit, null)
                            }
                            IconButton(
                                modifier = Modifier
                                    .height(24.dp)
                                    .padding(4.dp),
                                onClick = onDelete
                            ) {
                                Icon(Icons.Filled.Delete, null)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showAmount,
                enter = fadeIn(animationSpec = tween(300)) +
                        slideInVertically(animationSpec = tween(300), initialOffsetY = { it / 2 })
            ) {
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
                            enabled = editable,
                            value = amountText,
                            onValueChange = { input ->
                                val newAmount = input.toBigDecimalOrNull() ?: BigDecimal.ZERO
                                onUpdate(transaction.copy(amount = newAmount))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.headlineLarge.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = amountText.ifEmpty { "0" },
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showInputs,
                enter = fadeIn(animationSpec = tween(300)) +
                        slideInVertically(animationSpec = tween(300), initialOffsetY = { it / 2 })
            ) {
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
                                    .clickable(enabled = editable) { expanded = true }
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
                                                            .padding(
                                                                horizontal = 16.dp,
                                                                vertical = 8.dp
                                                            )
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
                                                                .clickable(enabled = editable) {
                                                                    onUpdate(
                                                                        transaction.copy(
                                                                            category = item
                                                                        )
                                                                    )
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
                                                                .clickable(enabled = editable) {
                                                                    onUpdate(
                                                                        transaction.copy(
                                                                            category = item
                                                                        )
                                                                    )
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
                                        .clickable(enabled = editable) {
                                            fromAccountExpanded = true
                                        }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = transaction.fromAccountName
                                                ?: "Select Source Account",
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
                                ModalBottomSheet(onDismissRequest = {
                                    fromAccountExpanded = false
                                }) {
                                    LazyColumn {
                                        items(accounts) { account ->
                                            Text(
                                                text = account.name,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        onUpdate(
                                                            transaction.copy(
                                                                from = account.id,
                                                                fromAccountName = account.name
                                                            )
                                                        )
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
                                            enabled = editable,
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
                                                .clickable(enabled = editable) {
                                                    merchantExpanded = true
                                                }
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
                                                    .clickable(enabled = editable) {
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
                                        .clickable(enabled = editable) { toAccountExpanded = true }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = transaction.toAccountName
                                                ?: "Select Destination Account",
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
                                                    .clickable(enabled = editable) {
                                                        onUpdate(
                                                            transaction.copy(
                                                                to = account.id,
                                                                toAccountName = account.name
                                                            )
                                                        )
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
                                    .clickable(enabled = editable) {
                                        showDatePicker.value = true
                                    }) {
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

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .clickable(enabled = editable) { isRecurring = !isRecurring },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Switch(
                                    enabled = editable,
                                    checked = isRecurring,
                                    onCheckedChange = { isRecurring = it }
                                )
                                Text(
                                    text = "Recurring",
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (isRecurring) {
                                ElevatedCard(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .fillMaxWidth()
                                        .clickable(enabled = editable) { recurringExpanded = true }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = selectedInterval.name.replace('_', ' ')
                                                .lowercase()
                                                .replaceFirstChar { it.uppercaseChar() },
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(16.dp)
                                        )

                                        Icon(
                                            Icons.TwoTone.ArrowDropDown,
                                            contentDescription = "Interval",
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = recurringExpanded,
                                    onDismissRequest = { recurringExpanded = false }
                                ) {
                                    RecurringInterval.values().forEach { interval ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    interval.name.replace('_', ' ').lowercase()
                                                        .replaceFirstChar { it.uppercaseChar() })
                                            },
                                            onClick = {
                                                selectedInterval = interval
                                                recurringExpanded = false
                                            }
                                        )
                                    }
                                }

                                if (selectedInterval == RecurringInterval.AFTER_CUTOFF) {
                                    OutlinedTextField(
                                        enabled = editable,
                                        value = cutoffDays.toString(),
                                        onValueChange = { input ->
                                            cutoffDays = input.toIntOrNull() ?: 21
                                        },
                                        label = { Text("Days After Cutoff") },
                                        modifier = Modifier
                                            .padding(top = 16.dp)
                                            .fillMaxWidth()
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Switch(
                                        enabled = editable,
                                        checked = reminderEnabled,
                                        onCheckedChange = { reminderEnabled = it }
                                    )
                                    Text(
                                        text = "Reminders",
                                        modifier = Modifier.padding(start = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                visible = showButtons,
                enter = fadeIn(animationSpec = tween(300)) +
                        slideInVertically(animationSpec = tween(300), initialOffsetY = { it / 2 })
            ) {
                HorizontalFloatingToolbar(
                    expanded = true,
                    floatingActionButton = {
                        FloatingToolbarDefaults.VibrantFloatingActionButton(
                            onClick = {
                                onSave(transaction, isRecurring, selectedInterval, cutoffDays, reminderEnabled)
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            val modifiers = listOf(
                                Modifier.wrapContentSize(),
                                Modifier.wrapContentSize(),
                                Modifier.wrapContentSize()
                            )

                            options.forEachIndexed { index, label ->
                                ToggleButton(
                                    enabled = editable,
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
                    }
                )
            }

            if (showDatePicker.value) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker.value = false },
                    confirmButton = {
                        Button(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val newDate =
                                    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
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
    }
}