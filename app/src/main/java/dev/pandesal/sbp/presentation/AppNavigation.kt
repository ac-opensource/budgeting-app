package dev.pandesal.sbp.presentation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.pandesal.sbp.presentation.categories.CategoriesScreen
import dev.pandesal.sbp.presentation.categories.new.NewCategoryGroupScreen
import dev.pandesal.sbp.presentation.categories.new.NewCategoryScreen
import dev.pandesal.sbp.presentation.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationDestination() {
    @Serializable
    data object Home : NavigationDestination()
    @Serializable
    data object Categories : NavigationDestination()
    @Serializable
    data object NewCategoryGroup : NavigationDestination()
    @Serializable
    data class NewCategory(val groupId: String) : NavigationDestination()
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
            composable<NavigationDestination.NewCategoryGroup> {
                NewCategoryGroupScreen(onSubmit = {}, onCancel = {
                    navController.navigateUp()
                })
            }
            composable<NavigationDestination.NewCategory> {
                val args = it.toRoute<NavigationDestination.NewCategory>()

                NewCategoryScreen(groupId = args.groupId, onSubmit = { _, _ -> }, onCancel = {
                    navController.navigateUp()
                })
            }

        }
    }

}