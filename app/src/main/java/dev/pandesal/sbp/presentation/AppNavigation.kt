package dev.pandesal.sbp.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navArgs
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.presentation.categories.CategoriesScreen
import dev.pandesal.sbp.presentation.accounts.AccountsScreen
import dev.pandesal.sbp.presentation.accounts.NewAccountScreen
import dev.pandesal.sbp.presentation.home.HomeScreen
import dev.pandesal.sbp.presentation.insights.InsightsScreen
import dev.pandesal.sbp.presentation.transactions.TransactionsScreen
import dev.pandesal.sbp.presentation.transactions.newtransaction.NewTransactionScreen
import dev.pandesal.sbp.presentation.transactions.newtransaction.NewRecurringTransactionScreen
import dev.pandesal.sbp.presentation.transactions.details.TransactionDetailsScreen
import dev.pandesal.sbp.presentation.settings.SettingsScreen
import dev.pandesal.sbp.presentation.notifications.NotificationCenterScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationDestination() {
    @Serializable
    data object Home : NavigationDestination()
    @Serializable
    data object Categories : NavigationDestination()
    @Serializable
    data object Insights : NavigationDestination()
    @Serializable
    data object More : NavigationDestination()
    @Serializable
    data object Settings : NavigationDestination()
    @Serializable
    data object Transactions : NavigationDestination()
    @Serializable
    data object NewTransaction : NavigationDestination()
    @Serializable
    data object Notifications : NavigationDestination()
    @Serializable
    data object Accounts : NavigationDestination()
    @Serializable
    data object NewAccount : NavigationDestination()
    @Serializable
    data object NewRecurringTransaction : NavigationDestination()
    @Serializable
    data class TransactionDetails(val transaction: Transaction) : NavigationDestination()

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

            composable<NavigationDestination.Transactions> {
                TransactionsScreen()
            }

            dialog<NavigationDestination.NewTransaction>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                NewTransactionScreen()
            }

            dialog<NavigationDestination.NewRecurringTransaction>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                NewRecurringTransactionScreen()
            }

            dialog<NavigationDestination.TransactionDetails>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
            ) { backStackEntry ->
                val args = backStackEntry.navArgs<NavigationDestination.TransactionDetails>()
                TransactionDetailsScreen(
                    transaction = args.transaction,
                    onDismiss = { navController.navigateUp() },
                    onSave = { navController.navigateUp() }
                )
            }

            composable<NavigationDestination.Notifications> {
                NotificationCenterScreen()
            }

            composable<NavigationDestination.Insights> {
                InsightsScreen()
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