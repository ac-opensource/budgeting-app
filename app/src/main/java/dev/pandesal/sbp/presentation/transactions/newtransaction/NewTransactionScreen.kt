package dev.pandesal.sbp.presentation.transactions.newtransaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import java.math.BigDecimal
import androidx.compose.ui.tooling.preview.Preview
import dev.pandesal.sbp.presentation.theme.StopBeingPoorTheme
import java.time.LocalDate
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import dev.pandesal.sbp.presentation.theme.AppFont

@Composable
fun NewTransactionScreen() {
    NewTransactionScreen(
        onSave = { transaction ->
            // Handle saving the transaction
        },
        onCancel = {
            // Handle canceling the transaction
        }
    )
}

@Composable
private fun NewTransactionScreen(
    onSave: (Transaction) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("1000") }
    var type by remember { mutableStateOf(TransactionType.OUTFLOW) }
    var category by remember { mutableStateOf("Select Category") }
    var merchant by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var location by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Transaction Type as TabRow with pill-style tabs
        val transactionTypes =
            listOf(TransactionType.INFLOW, TransactionType.OUTFLOW, TransactionType.TRANSFER)
        val selectedIndex = transactionTypes.indexOf(type)

        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedIndex])
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
                    onClick = { type = option },
                    selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .zIndex(1f)
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = option.name,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Amount + Currency
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Row {
                Text("₱", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.weight(1f))
            }

            BasicTextField(
                value = amount,
                onValueChange = { amount = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Text(
                        amount,
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

        Column {
            Text("Category", style = MaterialTheme.typography.bodyMedium)
            ElevatedCard(modifier = Modifier.padding(top = 4.dp).fillMaxWidth()) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { /* open dropdown */ }
                )
            }
        }

//        // From Account
//        Card(modifier = Modifier.fillMaxWidth()) {
//            Text("From Account", modifier = Modifier.padding(16.dp))
//        }

        // Transaction Name
        Column {
            Text("Transaction Name", style = MaterialTheme.typography.bodyMedium)
            ElevatedCard(modifier = Modifier.padding(top = 4.dp).fillMaxWidth()) {
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.padding(8.dp).fillMaxWidth().padding(8.dp),
                )
            }
        }


        Column {
            Text("To / Merchant", style = MaterialTheme.typography.bodyMedium)
            ElevatedCard(modifier = Modifier.padding(top = 4.dp).fillMaxWidth()) {
                BasicTextField(
                    value = merchant,
                    onValueChange = { merchant = it },
                    modifier = Modifier.padding(8.dp).fillMaxWidth().padding(8.dp),
                )
            }
        }


        Column {
            Text("Date", style = MaterialTheme.typography.bodyMedium)
            ElevatedCard(modifier = Modifier.padding(top = 4.dp).fillMaxWidth()) {
                Text(
                    text = date.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { /* open dropdown */ }
                )
            }
        }
        // Actions
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                val transaction = Transaction(
                    amount = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                    createdAt = date,
                    updatedAt = LocalDate.now(),
                    name = name,
                    accountId = "1",
                    transactionType = type
                )
                onSave(transaction)
            }) {
                Text("Save")
            }

            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }
        }

        Spacer(Modifier.size(150.dp))
    }
}


@Preview
@Composable
private fun NewTransactionScreenPreview() {
    StopBeingPoorTheme {
        val backgroundDiagonalGradient = Brush.linearGradient(
            colors = listOf(
                Color.White,
                Color(0xFFCEEDDB),  // Honeydew (surface)
                Color(0xFF85BAA1), // Cambridge Blue (background)
            ),
            start = Offset(0f, 0f),
            end = Offset.Infinite // Top-left to bottom-right
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundDiagonalGradient)
        ) {
            NewTransactionScreen(onSave = {}, onCancel = {})
        }
    }
}
