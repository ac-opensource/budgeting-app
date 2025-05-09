package dev.pandesal.sbp.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.pandesal.sbp.presentation.categories.CategoriesScreen
import dev.pandesal.sbp.presentation.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationDestination() {
    @Serializable
    data object Home : NavigationDestination()
    @Serializable
    data object Categories : NavigationDestination()
    data object Insights : NavigationDestination()
    data object More : NavigationDestination()
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

        }
    }

}