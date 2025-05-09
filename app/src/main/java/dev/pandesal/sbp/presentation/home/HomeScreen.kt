package dev.pandesal.sbp.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.presentation.home.components.AccountCard
import dev.pandesal.sbp.presentation.home.components.BudgetCategoryCard
import dev.pandesal.sbp.presentation.home.components.BudgetSummaryHeader
import dev.pandesal.sbp.presentation.home.components.NetWorthBarChart
import dev.pandesal.sbp.presentation.home.components.QuickActionCard
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    if (uiState is HomeUiState.Loading) {
        Dialog(onDismissRequest = {  }, properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )) {
        }
    }

    if (uiState is HomeUiState.Success) {
        HomeScreen(
            favoriteBudgets = (uiState as HomeUiState.Success).favoriteBudgets,
            accounts = (uiState as HomeUiState.Success).accounts,
            netWorthData = (uiState as HomeUiState.Success).netWorthData,
            onAddExpense = { println("Add Expense") },
            onAddFund = { println("Add Fund") },
            onAddLoan = { println("Add Loan") },
            onViewReports = { println("View Reports") },
            onViewAllBudgets = { println("View All Budgets") }
        )
    }



}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    favoriteBudgets: List<BudgetCategoryUiModel>,
    accounts: List<AccountSummaryUiModel>,
    netWorthData: List<NetWorthUiModel>,
    onAddExpense: () -> Unit,
    onAddFund: () -> Unit,
    onAddLoan: () -> Unit,
    onViewReports: () -> Unit,
    onViewAllBudgets: () -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        val assignedAmount = favoriteBudgets.sumOf { it.allocated }
        val totalAvailable = accounts.sumOf { it.balance }
        val unassignedAmount = totalAvailable - assignedAmount

        BudgetSummaryHeader(
            unassigned = unassignedAmount,
            assigned = assignedAmount
        )
        Spacer(modifier = Modifier.height(24.dp))

        NetWorthBarChart(data = netWorthData)

        Spacer(modifier = Modifier.height(24.dp))
        Text("Your Budget", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(favoriteBudgets) { budget ->
                BudgetCategoryCard(budget)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onViewAllBudgets) {
            Text("View All Budgets")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.height(80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { QuickActionCard("Add Expense", Icons.Default.ShoppingCart, onAddExpense) }
            item { QuickActionCard("Add Fund", Icons.Default.AccountBox, onAddFund) }
            item { QuickActionCard("Add Loan", Icons.Default.AccountCircle, onAddLoan) }
            item { QuickActionCard("Reports", Icons.Default.Create, onViewReports) }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Accounts", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        accounts.forEach { account ->
            AccountCard(account)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(130.dp))
    }

}


@Composable
@Preview
fun HomeScreenPreview() {
    val dummyBudgets = listOf(
        BudgetCategoryUiModel("Groceries", 5000.0, 3200.0),
        BudgetCategoryUiModel("Utilities", 3000.0, 1200.0),
        BudgetCategoryUiModel("Transport", 2000.0, 1800.0),
        BudgetCategoryUiModel("Dining Out", 1500.0, 800.0),
    )

    val dummyAccounts = listOf(
        AccountSummaryUiModel("GCash", 2200.0, isSpendingWallet = true, isFundingWallet = false),
        AccountSummaryUiModel("BPI Savings", 15000.0, isSpendingWallet = false, isFundingWallet = true),
        AccountSummaryUiModel("Wallet", 500.0, isSpendingWallet = true, isFundingWallet = false),
        AccountSummaryUiModel("UnionBank", 8500.0, isSpendingWallet = true, isFundingWallet = true),
    )

    val dummyNetWorth = listOf(
        NetWorthUiModel("Jan", 40000.0, 10000.0),
        NetWorthUiModel("Feb", 42000.0, 9500.0),
        NetWorthUiModel("Mar", 45000.0, 8700.0),
        NetWorthUiModel("Apr", 47000.0, 8000.0),
    )

    HomeScreen(
        favoriteBudgets = dummyBudgets,
        accounts = dummyAccounts,
        netWorthData = dummyNetWorth,
        onAddExpense = { println("Add Expense") },
        onAddFund = { println("Add Fund") },
        onAddLoan = { println("Add Loan") },
        onViewReports = { println("View Reports") },
        onViewAllBudgets = { println("View All Budgets") }
    )
}
