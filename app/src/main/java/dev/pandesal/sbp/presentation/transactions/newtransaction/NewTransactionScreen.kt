package dev.pandesal.sbp.presentation.transactions.newtransaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowDropDown
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.R
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.presentation.LocalNavigationManager
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId

@Composable
fun NewTransactionScreen(
    viewModel: NewTransactionsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val navManager = LocalNavigationManager.current

    if (uiState.value is NewTransactionUiState.Success) {
        val state = uiState.value as NewTransactionUiState.Success
        NewTransactionScreen(
            state.groupedCategories,
            state.transaction,
            state.merchants,
            onSave = {
                viewModel.saveTransaction {

                }
            },
            onCancel = {

            },
            onUpdate = {
                viewModel.updateTransaction(it)
            },
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewTransactionScreen(
    groupedCategories: Map<CategoryGroup, List<Category>>,
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

    // Transaction Type Tabs
    val transactionTypes = listOf(TransactionType.INFLOW, TransactionType.OUTFLOW, TransactionType.TRANSFER)
    val selectedIndex = transactionTypes.indexOf(transaction.transactionType)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {

        PrimaryTabRow(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50)),
            selectedTabIndex = selectedIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = {
                Box(
                    Modifier
                        .tabIndicatorOffset(selectedIndex)
                        .padding(5.dp)
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(50)
                        )
                        .border(BorderStroke(2.dp, Color.White), RoundedCornerShape(50))
                )
            },
            divider = {}
        ) {
            transactionTypes.forEachIndexed { index, option ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { onUpdate(transaction.copy(transactionType = option)) },
                    selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .zIndex(1f)
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = option.name,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selectedIndex == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Amount Input
        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
            BasicTextField(
                value = transaction.amount.toPlainString(),
                onValueChange = {
                    val newAmount = it.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    onUpdate(transaction.copy(amount = newAmount))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = {
                    Text(
                        text = transaction.amount.toPlainString(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }

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
                                                .padding(horizontal = 8.dp, vertical = 12.dp)
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        // Transaction Name
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text("From Account", style = MaterialTheme.typography.bodyMedium)
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
                    Text(
                        text = transaction.category?.name ?: "Select Source Account",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )

                    Icon(
                        painterResource(R.drawable.ic_wallet),
                        contentDescription = "Reorder",
                        modifier = Modifier.padding(end = 8.dp).size(24.dp)                    )
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
        }

        AnimatedVisibility(
            visible = transactionTypes[selectedIndex] == TransactionType.TRANSFER
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text("To Account", style = MaterialTheme.typography.bodyMedium)
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
                        Text(
                            text = transaction.category?.name ?: "Select Destination Account",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )

                        Icon(
                            painterResource(R.drawable.ic_wallet),
                            contentDescription = "Reorder",
                            modifier = Modifier.padding(end = 8.dp).size(24.dp)                    )
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

        // Save & Cancel
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.weight(1f))

            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }

            Button(onClick = { onSave(transaction) }) {
                Text("Save")
            }
        }

        Spacer(Modifier.size(150.dp))
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
