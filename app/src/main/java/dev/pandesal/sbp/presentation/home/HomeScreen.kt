package dev.pandesal.sbp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import dev.pandesal.sbp.presentation.components.TransactionItem
import java.math.BigDecimal
import java.time.LocalDate


@Composable
fun HomeScreen() {

    val navController = LocalNavigationManager.current

    HomeScreen(
        totalAmount = 12345.67,
        categoryPercentages = listOf(
            "Invest" to 30.0,
            "Healthcare" to 20.0,
            "Self Reward" to 15.0,
            "Food" to 10.0,
            "Transport" to 5.0
        ),
        transactions = List(100) { index ->
            Transaction(
                categoryId = "${index + 1}",
                amount = BigDecimal(100 + index * 10),
                createdAt = LocalDate.now().minusDays(index.toLong()),
                updatedAt = LocalDate.now().minusDays(index.toLong()),
                name = "Transaction #${index + 1}",
                accountId = "1",
                transactionType = if (index % 2 == 0) TransactionType.OUTFLOW else TransactionType.INFLOW
            )
        },
        onViewAllTransactions = {
            navController.navigate(NavigationDestination.Transactions)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    totalAmount: Double,
    categoryPercentages: List<Pair<String, Double>>,
    transactions: List<Transaction>,
    onViewAllTransactions: () -> Unit = {}
) {
    val topCategories = categoryPercentages
        .sortedByDescending { it.second }
        .take(3)
    val othersPercentage = 100.0 - topCategories.sumOf { it.second }

    val displayCategories = topCategories.toMutableList()

    if (othersPercentage > 0f) {
        displayCategories.add("Others" to othersPercentage)
    }

    val sheetHeightPx = remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = with(density) {
            val heightWithPadding = sheetHeightPx.floatValue.toDp() - 24.dp
            if (heightWithPadding > 0.dp) {
                heightWithPadding
            } else {
                400.dp
            }
        },
        sheetContent = {

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recent Transactions", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "View All",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            onViewAllTransactions()
                        })
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(transaction)
                    }
                }

                Spacer(Modifier.height(120.dp))
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text("Consolidated Account", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "$${"%,.2f".format(totalAmount)}",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                displayCategories.forEach { (label, percent) ->
                    Box(
                        modifier = Modifier
                            .weight(percent.toFloat())
                            .clip(RoundedCornerShape(8.dp))
                            .fillMaxHeight()
                            .background(getCategoryColor(label))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            displayCategories.forEach { (label, percent) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label)
                    Text("${percent.toInt()}%")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        val positionY = coordinates.positionInWindow().y
                        val calculatedPeekHeight = screenHeightPx - positionY
                        sheetHeightPx.floatValue = calculatedPeekHeight
                    }
            )
        }
    }
}

fun getCategoryColor(label: String): Color = when (label) {
    "Invest" -> Color(0xFF6FCF97)
    "Healthcare" -> Color(0xFFF29BBB)
    "Self Reward" -> Color(0xFFF2C94C)
    "Others" -> Color.Gray
    else -> Color.LightGray
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        totalAmount = 12345.67,
        categoryPercentages = listOf(
            "Invest" to 30.0,
            "Healthcare" to 20.0,
            "Self Reward" to 15.0,
            "Food" to 10.0,
            "Transport" to 5.0
        ),
        transactions = listOf(
            Transaction(
                categoryId = "1",
                amount = BigDecimal(120.00),
                createdAt = LocalDate.now(),
                updatedAt = LocalDate.now(),
                name = "Grocery Shopping",
                accountId = "1",
                transactionType = TransactionType.OUTFLOW
            ),
            Transaction(
                categoryId = "2",
                amount = BigDecimal(500.00),
                createdAt = LocalDate.now().minusDays(1),
                updatedAt = LocalDate.now().minusDays(1),
                name = "Salary",
                accountId = "1",
                transactionType = TransactionType.INFLOW
            )
        )
    )
}