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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import dev.pandesal.sbp.presentation.components.TransactionItem
import dev.pandesal.sbp.presentation.theme.StopBeingPoorTheme
import java.math.BigDecimal
import java.time.LocalDate


@Composable
fun HomeScreen() {

    val navController = LocalNavigationManager.current

    val testCategories = listOf("Invest", "Healthcare", "Self Reward", "Food", "Transport")

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
            val randomCategoryName = testCategories.random()
            val isInflow = (0..1).random() == 1
            Transaction(
                amount = if (isInflow) BigDecimal(100 + index * 10) else BigDecimal(-(100 + index * 10)),
                createdAt = LocalDate.now().minusDays((index / 6).toLong()),
                updatedAt = LocalDate.now().minusDays((index / 6).toLong()),
                name = "Transaction #${index + 1}",
                accountId = "1",
                category = Category(
                    id = 1,
                    name = randomCategoryName,
                    categoryGroupId = 1,
                    isArchived = false,
                    description = "",
                    icon = "",
                    categoryType = TransactionType.OUTFLOW,
                    weight = 1
                ),
                transactionType = if (isInflow) TransactionType.INFLOW else TransactionType.OUTFLOW
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
        containerColor = Color.Transparent,
        scaffoldState = scaffoldState,
        sheetPeekHeight = with(density) {
            val heightWithPadding = sheetHeightPx.floatValue.toDp() - 24.dp
            if (heightWithPadding > 0.dp) {
                heightWithPadding
            } else {
                400.dp
            }
        },
        sheetShadowElevation = 16.dp,
        sheetContent = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
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

                val groupedTransactions = transactions.groupBy {
                    when (it.createdAt) {
                        LocalDate.now() -> "Today"
                        LocalDate.now().minusDays(1) -> "Yesterday"
                        else -> it.createdAt.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    groupedTransactions.forEach { (dateLabel, txList) ->
                        stickyHeader {
                            ElevatedFilterChip(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(vertical = 4.dp)
                                    .padding(top = 12.dp),
                                onClick = {},
                                selected = dateLabel == "Today",
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.White
                                ),
                                elevation = FilterChipDefaults.filterChipElevation(
                                    elevation = 8.dp,
                                ),
                                label = {
                                    Text(
                                        text = dateLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                })
                        }
                        items(txList) { transaction ->
                            TransactionItem(transaction)
                        }
                    }
                }

                Spacer(Modifier.height(120.dp))
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Consolidated Account", style = MaterialTheme.typography.labelLarge)
            Text(
                text = "$${"%,.2f".format(totalAmount)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 8.dp)
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
                    Text(label, style = MaterialTheme.typography.labelLarge)
                    Text("${percent.toInt()}%", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = Color.Transparent,
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
    "Invest" -> Color(0xFF4B3B60) // Deep muted violet – pairs well with Honeydew
    "Healthcare" -> Color(0xFF6E8894) // Slate gray – theme color, keeps things consistent
    "Self Reward" -> Color(0xFFAD6A6C) // Dusty rose – mellow but visible
    "Food" -> Color(0xFF5E8D74) // Darker Cambridge green
    "Others" -> Color(0xFF837060) // Warm taupe – grounded, neutral contrast
    "" -> Color(0xFFAAAAAA) // Medium gray for generic fallback
    else -> Color(0xFF999999) // Soft gray fallback
}

@Preview
@Composable
fun HomeScreenPreview() {
    StopBeingPoorTheme {
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
                    amount = BigDecimal(120.00),
                    createdAt = LocalDate.now(),
                    updatedAt = LocalDate.now(),
                    name = "Grocery Shopping",
                    category = Category(
                        id = 1,
                        name = "Groceries",
                        categoryGroupId = 1,
                        isArchived = false,
                        description = "",
                        icon = "",
                        categoryType = TransactionType.OUTFLOW,
                        weight = 1
                    ),
                    accountId = "1",
                    transactionType = TransactionType.OUTFLOW
                ),
                Transaction(
                    amount = BigDecimal(500.00),
                    createdAt = LocalDate.now().minusDays(1),
                    updatedAt = LocalDate.now().minusDays(1),
                    name = "Salary",
                    category = Category(
                        id = 1,
                        name = "Income",
                        categoryGroupId = 1,
                        isArchived = false,
                        description = "",
                        icon = "",
                        categoryType = TransactionType.OUTFLOW,
                        weight = 1
                    ),
                    accountId = "1",
                    transactionType = TransactionType.INFLOW
                )
            )
        )
    }

}