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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
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
    }
}