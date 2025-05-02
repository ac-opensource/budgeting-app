package dev.pandesal.sbp.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.extensions.format
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel

@Composable
fun BudgetCategoryCard(budget: BudgetCategoryUiModel) {
    val remaining = budget.allocated - budget.spent
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(budget.name, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = {
                    (budget.spent / budget.allocated).coerceIn(0.0, 1.0).toFloat()
                },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.inverseSurface,

            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("₱${remaining.format()} left", style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Composable
@Preview
fun BudgetHomeScreenPreview() {
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
