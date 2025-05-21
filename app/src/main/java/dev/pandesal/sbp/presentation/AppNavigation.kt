package dev.pandesal.sbp.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import dev.pandesal.sbp.presentation.categories.CategoriesScreen
import dev.pandesal.sbp.presentation.accounts.AccountsScreen
import dev.pandesal.sbp.presentation.accounts.NewAccountScreen
import dev.pandesal.sbp.presentation.categories.new.NewCategoryGroupScreen
import dev.pandesal.sbp.presentation.categories.new.NewCategoryScreen
import dev.pandesal.sbp.presentation.home.HomeScreen
import dev.pandesal.sbp.presentation.insights.InsightsScreen
import dev.pandesal.sbp.presentation.transactions.TransactionsScreen
import dev.pandesal.sbp.presentation.transactions.newtransaction.NewTransactionScreen
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.presentation.transactions.newtransaction.NewRecurringTransactionScreen
import dev.pandesal.sbp.presentation.transactions.recurringdetails.RecurringTransactionDetailsScreen
import dev.pandesal.sbp.presentation.transactions.recurring.RecurringTransactionsScreen
import dev.pandesal.sbp.presentation.categories.budget.SetBudgetScreen
import dev.pandesal.sbp.presentation.categories.CategoryTransactionsScreen
import dev.pandesal.sbp.presentation.settings.SettingsScreen
import dev.pandesal.sbp.presentation.notifications.NotificationCenterScreen
import dev.pandesal.sbp.presentation.goals.NewGoalScreen
import dev.pandesal.sbp.presentation.nav.parcelableTypeMap
import java.math.BigDecimal
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
sealed class NavigationDestination() {
    @Serializable
    data object Home : NavigationDestination()
    @Serializable
    data object Categories : NavigationDestination()
    @Serializable
    data object Insights : NavigationDestination()
    @Serializable
    data object Trends : NavigationDestination()
    @Serializable
    data object More : NavigationDestination()
    @Serializable
    data object Settings : NavigationDestination()
    @Serializable
    data object Transactions : NavigationDestination()
    @Serializable
    data class NewTransaction(val transaction: Transaction? = null) : NavigationDestination()
    @Serializable
    data object Notifications : NavigationDestination()
    @Serializable
    data object Accounts : NavigationDestination()
    @Serializable
    data object NewAccount : NavigationDestination()
    @Serializable
    data object NewGoal : NavigationDestination()
    @Serializable
    data object NewRecurringTransaction : NavigationDestination()
    @Serializable
    data object RecurringTransactions : NavigationDestination()
    @Serializable
    data object NewCategoryGroup : NavigationDestination()
    @Serializable
    data class SetBudget(val categoryId: Int, val amount: String? = null) : NavigationDestination()
    @Serializable
    data class NewCategory(val groupId: Int, val groupName: String) : NavigationDestination()
    @Serializable
    data class CategoryTransactions(val categoryId: Int) : NavigationDestination()
    @Serializable
    data class TransactionDetails(val transactionId: String) : NavigationDestination()
    @Serializable
    data class RecurringTransactionDetails(val id: String) : NavigationDestination()

}

val LocalNavigationManager = compositionLocalOf<NavHostController> { error("No nav host found") }

@Composable
fun AppNavigation(navController: NavHostController) {
    // It is recommended to not pass navController into the the composables
    // https://developer.android.com/develop/ui/compose/navigation#testing .
    // What I did is to create a composition local of NavHostController and then bind it to navController
    // so I can just access LocalNavigationManager.current at any point below the NavHost hierarchy
    CompositionLocalProvider(
        LocalNavigationManager provides navController
    ) {
        NavHost(navController = navController, startDestination = NavigationDestination.Home) {
            composable<NavigationDestination.Home> {
                HomeScreen()
            }
            composable<NavigationDestination.Categories> {
                CategoriesScreen()
            }
            composable<NavigationDestination.Accounts> {
                AccountsScreen()
            }

            dialog<NavigationDestination.NewAccount>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                NewAccountScreen()
            }
            dialog<NavigationDestination.NewGoal>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                NewGoalScreen()
            }

            dialog<NavigationDestination.NewCategoryGroup>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                NewCategoryGroupScreen()
            }

            dialog<NavigationDestination.NewCategory>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<NavigationDestination.NewCategory>()
                NewCategoryScreen(groupId = args.groupId, groupName = args.groupName)
            }

            dialog<NavigationDestination.SetBudget>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<NavigationDestination.SetBudget>()
                SetBudgetScreen(
                    categoryId = args.categoryId,
                    initialAmount = args.amount?.toBigDecimalOrNull() ?: BigDecimal.ZERO
                )
            }

            composable<NavigationDestination.CategoryTransactions> { backStackEntry ->
                val args = backStackEntry.toRoute<NavigationDestination.CategoryTransactions>()
                CategoryTransactionsScreen(categoryId = args.categoryId)
            }


            composable<NavigationDestination.Transactions> {
                TransactionsScreen()
            }

            composable<NavigationDestination.RecurringTransactions> {
                RecurringTransactionsScreen()
            }

            dialog<NavigationDestination.NewTransaction>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
                typeMap          = parcelableTypeMap<Transaction>(isNullableAllowed = true),
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<NavigationDestination.NewTransaction>()
                NewTransactionScreen(initialTransaction = args.transaction)
            }

            dialog<NavigationDestination.NewRecurringTransaction>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                NewRecurringTransactionScreen()
            }

            composable<NavigationDestination.RecurringTransactions> {
                RecurringTransactionsScreen()
            }

            dialog<NavigationDestination.TransactionDetails>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<NavigationDestination.TransactionDetails>()
                NewTransactionScreen(
                    transactionId = args.transactionId,
                    readOnly = true
                )
            }

            dialog<NavigationDestination.RecurringTransactionDetails>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<NavigationDestination.RecurringTransactionDetails>()
                RecurringTransactionDetailsScreen(id = args.id)
            }

            composable<NavigationDestination.Notifications> {
                NotificationCenterScreen()
            }

            composable<NavigationDestination.Insights> {
                InsightsScreen()
            }

            composable<NavigationDestination.Trends> {
                dev.pandesal.sbp.presentation.trends.TrendsScreen()
            }

            composable<NavigationDestination.More> {
//                MoreScreen()
                HomeScreen()
            }

            composable<NavigationDestination.Settings> {
                SettingsScreen()
            }

        }
    }

}