package dev.pandesal.sbp.presentation.transactions.newtransaction

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.twotone.ArrowDropDown
import androidx.compose.material.icons.twotone.Camera
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import dev.pandesal.sbp.presentation.components.SkeletonLoader
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt

@Composable
fun NewTransactionScreen(
    transactionId: String? = null,
    readOnly: Boolean = false,
    initialTransaction: Transaction? = null,
    viewModel: NewTransactionsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val canSave = viewModel.canSave.collectAsState()
    val navManager = LocalNavigationManager.current
    val context = LocalContext.current
    var jiggleTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.feedback.collect { event ->
            if (event is NewTransactionsViewModel.FeedbackEvent.InvalidForm) {
                jiggleTrigger++
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    manager.defaultVibrator
                } else {
                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }
                if (vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(100)
                    }
                }
            }
        }
    }

    LaunchedEffect(transactionId, initialTransaction) {
        transactionId?.let { viewModel.loadTransaction(it) }
        initialTransaction?.let { viewModel.updateTransaction(it) }
    }

    if (uiState.value is NewTransactionUiState.Initial) {
        SkeletonLoader()
    } else if (uiState.value is NewTransactionUiState.Success) {
        val state = uiState.value as NewTransactionUiState.Success
        var editable by remember { mutableStateOf(!readOnly) }
        var showDelete by remember { mutableStateOf(false) }
        val attachmentLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                viewModel.attachReceipt(it, context)
            }
        }
        NewTransactionScreen(
            groupedCategories = state.groupedCategories,
            accounts = state.accounts,
            transaction = state.transaction,
            merchants = state.merchants,
            tags = state.tags,
            errors = state.errors,
            validationTrigger = jiggleTrigger,
            editable = editable,
            onEdit = { editable = true },
            onDelete = { showDelete = true },
            onSave = { _, recur, interval, cutoff, reminder ->
                viewModel.saveTransaction(recur, interval, cutoff, reminder) { success ->
                    if (success) navManager.navigateUp()
                }
            },
            onCancel = {
                navManager.navigateUp()
            },
            onUpdate = {
                viewModel.updateTransaction(it)
            },
            onAttach = { attachmentLauncher.launch("image/*") }
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

@Composable
private fun JiggleErrorText(
    text: String,
    visible: Boolean,
    trigger: Int,
    modifier: Modifier = Modifier
) {
    val offsetX = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(trigger) {
        if (visible) {
            offsetX.snapTo(0f)
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = androidx.compose.animation.core.keyframes {
                    durationMillis = 400
                    -10f at 50
                    10f at 100
                    -8f at 150
                    8f at 200
                    0f at 250
                }
            )
        }
    }
    if (visible) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall,
            modifier = modifier.offset { IntOffset(offsetX.value.roundToInt(), 0) }
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
    tags: List<String>,
    errors: NewTransactionUiState.ValidationErrors,
    validationTrigger: Int,
    editable: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSave: (Transaction, Boolean, RecurringInterval, Int, Boolean) -> Unit,
    onCancel: () -> Unit,
    onUpdate: (Transaction) -> Unit,
    onAttach: () -> Unit
) {
    val navManager = LocalNavigationManager.current

    if (accounts.isEmpty()) {
        AlertDialog(
            onDismissRequest = {
                navManager.navigateUp()
                navManager.navigate(NavigationDestination.More)
            },
            confirmButton = {
                TextButton(onClick = {
                    navManager.navigateUp()
                    navManager.navigate(NavigationDestination.More)
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
    var newTag by remember { mutableStateOf("") }
    var tagExpanded by remember { mutableStateOf(false) }
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
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(vertical = 8.dp),
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
                Column {
                    ElevatedCard(
                        shape = RoundedCornerShape(50),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 16.dp
                        ),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
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
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = amountText,
                                            style = MaterialTheme.typography.headlineLarge.copy(
                                                textAlign = TextAlign.Center,
                                                fontSize = 48.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            maxLines = 1,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        if (amountText.isEmpty()) {
                                            Box(
                                                modifier = Modifier.alpha(0f)
                                            ) {
                                                innerTextField()
                                            }

                                        } else {
                                            innerTextField()
                                        }


                                    }
                                }
                            )
                        }
                    }
                    JiggleErrorText(
                        text = "Amount is required",
                        visible = errors.amount,
                        trigger = validationTrigger,
                        modifier = Modifier
                            .align(Alignment.End)
                            .background(
                                MaterialTheme.colorScheme.errorContainer,
                                RoundedCornerShape(50)
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
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
                            JiggleErrorText(
                                text = "Category is required",
                                visible = errors.category,
                                trigger = validationTrigger,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )

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
                                Text(
                                    "From Account (optional)",
                                    style = MaterialTheme.typography.bodyMedium
                                )
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
                                JiggleErrorText(
                                    text = "Source account is required",
                                    visible = errors.from,
                                    trigger = validationTrigger,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
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
                                Text(
                                    "To / Merchant (optional)",
                                    style = MaterialTheme.typography.bodyMedium
                                )
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

                                JiggleErrorText(
                                    text = "Payee is required",
                                    visible = errors.to,
                                    trigger = validationTrigger,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
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
                                Text(
                                    "To Account (optional)",
                                    style = MaterialTheme.typography.bodyMedium
                                )
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

                                JiggleErrorText(
                                    text = "Destination account is required",
                                    visible = errors.to,
                                    trigger = validationTrigger,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
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

                        // Tags section
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            Text(
                                "Track to Hobby/Activity (optional)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            ElevatedCard(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .fillMaxWidth()
                            ) {
                                androidx.compose.foundation.layout.FlowRow(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    transaction.tags.forEach { tag ->
                                        AssistChip(
                                            onClick = {},
                                            label = { Text(tag, style = MaterialTheme.typography.labelMedium) },
                                            trailingIcon = {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .clickable(enabled = editable) {
                                                            onUpdate(transaction.copy(tags = transaction.tags - tag))
                                                        }
                                                )
                                            }
                                        )
                                    }

                                    if (transaction.tags.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    OutlinedTextField(
                                        value = newTag,
                                        onValueChange = { newTag = it },
                                        textStyle = MaterialTheme.typography.labelMedium.copy(lineHeight = TextUnit.Unspecified),
                                        modifier = Modifier
                                            .widthIn(min = 40.dp)
                                            .heightIn(min = 24.dp)
                                            .clickable { tagExpanded = true }
                                    )

                                    IconButton(enabled = editable, onClick = {
                                        if (newTag.isNotBlank()) {
                                            onUpdate(transaction.copy(tags = (transaction.tags + newTag).distinct()))
                                            newTag = ""
                                        }
                                    }) {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                    }

                                }
                            }
                        }

                        if (tagExpanded) {
                            ModalBottomSheet(onDismissRequest = { tagExpanded = false }) {
                                LazyColumn {
                                    items(tags) { item ->
                                        Text(
                                            text = item,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { newTag = item; tagExpanded = false }
                                                .padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(top = 16.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                Column(modifier = Modifier.padding(top = 16.dp).weight(1f)) {
                                    Text(
                                        "Receipt Photo (optional)",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    ElevatedCard(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .fillMaxWidth()
                                            .clickable(enabled = editable) { onAttach() },
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            if (transaction.attachment != null) {
                                                AsyncImage(
                                                    model = transaction.attachment,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(64.dp)
                                                        .padding(8.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = "Add Receipt",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.padding(16.dp)
                                                )
                                            }
                                            Icon(
                                                Icons.TwoTone.Camera,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(end = 8.dp)
                                                    .size(24.dp)
                                            )
                                        }
                                    }
                                }

                                Column(modifier = Modifier.padding(top = 16.dp).weight(1f)) {
                                    Text(
                                        "Transaction Date",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
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
                                }
                            }
                        }



                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .clickable(enabled = editable) {
                                    isRecurring = !isRecurring
                                },
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
                                    .clickable(enabled = editable) {
                                        recurringExpanded = true
                                    }
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

                                DropdownMenu(
                                    expanded = recurringExpanded,
                                    onDismissRequest = { recurringExpanded = false }
                                ) {
                                    RecurringInterval.values().forEach { interval ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    interval.name.replace('_', ' ')
                                                        .lowercase()
                                                        .replaceFirstChar { it.uppercaseChar() })
                                            },
                                            onClick = {
                                                selectedInterval = interval
                                                recurringExpanded = false
                                            }
                                        )
                                    }
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
                                val context = LocalContext.current
                                val permissionLauncher = rememberLauncherForActivityResult(
                                    ActivityResultContracts.RequestPermission()
                                ) { granted ->
                                    reminderEnabled = granted
                                }
                                Switch(
                                    enabled = editable,
                                    checked = reminderEnabled,
                                    onCheckedChange = { checked ->
                                        if (checked && android.os.Build.VERSION.SDK_INT >= 33) {
                                            val permission =
                                                Manifest.permission.POST_NOTIFICATIONS
                                            if (ContextCompat.checkSelfPermission(
                                                    context,
                                                    permission
                                                ) == PackageManager.PERMISSION_GRANTED
                                            ) {
                                                reminderEnabled = true
                                            } else {
                                                permissionLauncher.launch(permission)
                                            }
                                        } else {
                                            reminderEnabled = checked
                                        }
                                    }
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

            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                visible = showButtons,
                enter = fadeIn(animationSpec = tween(300)) +
                        slideInVertically(
                            animationSpec = tween(300),
                            initialOffsetY = { it / 2 })
            ) {
                HorizontalFloatingToolbar(
                    expanded = true,
                    floatingActionButton = {
                        FloatingToolbarDefaults.VibrantFloatingActionButton(
                            onClick = {
                                onSave(
                                    transaction,
                                    isRecurring,
                                    selectedInterval,
                                    cutoffDays,
                                    reminderEnabled
                                )
                            }
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
        }
    }
}