package dev.pandesal.sbp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import dev.pandesal.sbp.presentation.components.SkeletonLoader
import dev.pandesal.sbp.presentation.components.TransactionItem
import dev.pandesal.sbp.presentation.home.components.AccountCard
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.BudgetSummaryUiModel
import dev.pandesal.sbp.presentation.theme.StopBeingPoorTheme
import dev.pandesal.sbp.presentation.transactions.TransactionsContent
import dev.pandesal.sbp.presentation.transactions.TransactionsUiState
import dev.pandesal.sbp.presentation.transactions.TransactionsViewModel
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    transactionsViewModel: TransactionsViewModel = hiltViewModel()
) {
    val navController = LocalNavigationManager.current
    val homeState by viewModel.uiState.collectAsState()
    val transactionsState by transactionsViewModel.uiState.collectAsState()

    if (homeState is HomeUiState.Initial || transactionsState is TransactionsUiState.Initial) {
        SkeletonLoader()
    } else if (homeState is HomeUiState.Success && transactionsState is TransactionsUiState.Success) {
        val state = homeState as HomeUiState.Success
        val txState = transactionsState as TransactionsUiState.Success
        HomeScreenContent(
            state = state,
            transactions = txState.transactions,
            onTransactionClicked = { id ->
                navController.navigate(NavigationDestination.TransactionDetails(id))
            },
            onViewNotifications = { navController.navigate(NavigationDestination.Notifications) }
        )
    }
}

@Composable
private fun HomeScreenContent(
    state: HomeUiState.Success,
    transactions: List<Transaction>,
    onTransactionClicked: (String) -> Unit,
    onViewNotifications: () -> Unit
) {
    val totalAmount = state.accounts.sumOf { it.balance }
    val totalAllocated = state.favoriteBudgets.sumOf { it.allocated }
    val categoryPercentages = state.favoriteBudgets.map { budget ->
        val pct = if (totalAllocated != 0.0) (budget.allocated / totalAllocated) * 100.0 else 0.0
        budget.name to pct
    }
    val topCategories = categoryPercentages.sortedByDescending { it.second }.take(3)
    val othersPercentage = 100.0 - topCategories.sumOf { it.second }
    val displayCategories = topCategories.toMutableList().apply {
        if (othersPercentage > 0) add("Others" to othersPercentage)
    }

    fun LazyListState.isSticking(index: Int): State<Boolean> {
        return derivedStateOf {
            val firstVisible = layoutInfo.visibleItemsInfo.firstOrNull()
            firstVisible?.index == index && firstVisible.offset == -layoutInfo.beforeContentPadding
        }
    }

    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState) {
        stickyHeader {
            HeaderSection(totalAmount, onViewNotifications)
        }

        if (othersPercentage != 100.0) {
            item {
                BudgetBreakdownSection(
                    categories = displayCategories,
                    unassigned = state.budgetSummary.unassigned,
                    assigned = state.budgetSummary.assigned
                )
            }
        }

        item { AccountsSection(state.accounts) }

        item {

            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("Transactions", style = MaterialTheme.typography.titleMedium)
            }

        }

        transactionsSection(transactions, onTransactionClicked)

        item { Spacer(Modifier.height(600.dp)) }
    }
}

private fun LazyListScope.transactionsSection(
    transactions: List<Transaction>,
    onTransactionClicked: (String) -> Unit
) {
    val groupedTransactions = transactions.groupBy {
        when (it.createdAt) {
            LocalDate.now() -> "Today"
            LocalDate.now().minusDays(1) -> "Yesterday"
            else -> it.createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        }
    }

    groupedTransactions.forEach { (dateLabel, txList) ->
        item {
            Column {
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

        }
        items(txList) { transaction ->
            TransactionItem(
                tx = transaction,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
                    .clickable { onTransactionClicked(transaction.id) }
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeaderSection(
    totalAmount: BigDecimal,
    onViewNotifications: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .padding(bottom = 16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 16.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AccountSummarySection(totalAmount)
            HomeToolbar(onViewNotifications)
        }
    }
}

@Composable
private fun HomeToolbar(onViewNotifications: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onViewNotifications) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AccountSummarySection(totalAmount: BigDecimal) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Consolidated Account", style = MaterialTheme.typography.labelLarge)
        Text(
            text = "$${"%,.2f".format(totalAmount)}",
            style = MaterialTheme.typography.headlineLargeEmphasized,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun AccountsSection(accounts: List<AccountSummaryUiModel>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Accounts", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        accounts.forEachIndexed { index, account ->
            AccountCard(account)
            if (index != accounts.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BudgetBreakdownSection(
    categories: List<Pair<String, Double>>,
    unassigned: Double,
    assigned: Double
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        Text("Budget Allocation Breakdown", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        val thickStrokeWidth = with(LocalDensity.current) { 8.dp.toPx() }
        val thickStroke =
            remember(thickStrokeWidth) {
                Stroke(
                    width = thickStrokeWidth,
                    cap = StrokeCap.Round
                )
            }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {

            categories.forEachIndexed { index, (label, percent) ->
                if (percent < 0) return@forEachIndexed
                LinearWavyProgressIndicator(
                    progress = {
                        1f
                    },
                    modifier = Modifier
                        .weight( percent.coerceAtLeast(0.1).toFloat())
                        .fillMaxHeight(),
                    amplitude = {
                        1f
                    },
                    stroke = thickStroke,
                    color = getCategoryColor(index),
                    trackColor = Color.LightGray,
                    wavelength = 12.dp,
                    waveSpeed = (0.1).dp
                )
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        categories.forEachIndexed { index, (label, percent) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(50))
                        .background(getCategoryColor(index))
                )
                Text(label, style = MaterialTheme.typography.labelLarge)
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 0.4.dp,
                    modifier = Modifier.weight(1f)
                )
                Text("${percent.toInt()}%", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TransactionsSection(
    transactions: List<Transaction>,
    onTransactionClicked: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Recent Transactions", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        TransactionsContent(
            transactions = transactions,
            onNewTransactionClick = {},
            onTransactionClick = { onTransactionClicked(it.id) }
        )
    }
}

private val categoryColors = listOf(
    Color(0xFF4B3B60),
    Color(0xFF6E8894),
    Color(0xFFAD6A6C),
    Color(0xFF5E8D74),
    Color(0xFF837060)
)

fun getCategoryColor(index: Int): Color =
    categoryColors.getOrElse(index) { Color(0xFF999999) }

@Preview
@Composable
fun HomeScreenPreview() {
    StopBeingPoorTheme {
        HomeScreenContent(
            state = HomeUiState.Success(
                favoriteBudgets = listOf(
                    BudgetCategoryUiModel("Invest", 30.0, 0.0, "PHP"),
                    BudgetCategoryUiModel("Healthcare", 20.0, 0.0, "PHP"),
                    BudgetCategoryUiModel("Self Reward", 15.0, 0.0, "PHP")
                ),
                accounts = listOf(
                    AccountSummaryUiModel("Main", BigDecimal("1200.00"), true, false, "USD")
                ),
                netWorthData = emptyList(),
                budgetSummary = BudgetSummaryUiModel(0.0, 0.0)
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
                    from = 1,
                    fromAccountName = "Sample Bank",
                    transactionType = TransactionType.OUTFLOW
                )
            ),
            onTransactionClicked = {},
            onViewNotifications = {}
        )
    }
}

@Preview
@Composable
fun AccountsSectionPreview() {
    StopBeingPoorTheme {
        AccountsSection(
            accounts = listOf(
                AccountSummaryUiModel("Wallet", BigDecimal("200.00"), true, false, "USD"),
                AccountSummaryUiModel("Bank", BigDecimal("500.00"), false, true, "USD")
            )
        )
    }
}
