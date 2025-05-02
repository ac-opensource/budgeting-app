package dev.pandesal.sbp.presentation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.pandesal.sbp.presentation.home.HomeScreen
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel

sealed class NavigationDestination(val route: String) {
    data object Home : NavigationDestination("home")
    data object History : NavigationDestination("history")
}

val LocalNavigationManager = compositionLocalOf<NavHostController> { error("No nav host found") }

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // It is recommended to not pass navController into the the composables
    // https://developer.android.com/develop/ui/compose/navigation#testing .
    // What I did is to create a composition local of NavHostController and then bind it to navController
    // so I can just access LocalNavigationManager.current at any point below the NavHost hierarchy
    CompositionLocalProvider(
        LocalNavigationManager provides navController
    ) {
        NavHost(navController = navController, startDestination = NavigationDestination.Home.route) {
            composable(NavigationDestination.Home.route) {
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
            composable(NavigationDestination.History.route,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                }) {

            }
        }
    }

}