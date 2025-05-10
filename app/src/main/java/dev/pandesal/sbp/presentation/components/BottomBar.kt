package dev.pandesal.sbp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import dev.pandesal.sbp.R
import dev.pandesal.sbp.presentation.NavigationDestination

data class BottomNavItemData(
    val name: String,
    val icon: Int,
    val destination: NavigationDestination
)

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination: NavigationDestination? = navBackStackEntry.value?.toRoute()

    val bottomNavItems = listOf(
        BottomNavItemData("Home", R.drawable.ic_home, NavigationDestination.Home),
        BottomNavItemData("Budget", R.drawable.ic_budget, NavigationDestination.Categories),
        BottomNavItemData("Insights", R.drawable.ic_graph, NavigationDestination.Insights),
        BottomNavItemData("More", R.drawable.ic_more, NavigationDestination.More)
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.take(2).forEach {
                    BottomNavItem(
                        name = it.name,
                        icon = it.icon,
                        isSelected = currentDestination == it.destination,
                        onClick = {
                            navController.navigate(it.destination) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(80.dp))

                bottomNavItems.drop(2).forEach {
                    BottomNavItem(
                        name = it.name,
                        icon = it.icon,
                        isSelected = currentDestination == it.destination,
                        onClick = {
                            navController.navigate(it.destination) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }

        Row {
            Card(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "New Transaction",
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.size(16.dp))
        }

    }
}

@Composable
fun BottomNavItem(
    modifier: Modifier = Modifier,
    name: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            modifier = modifier.size(24.dp),
            painter = painterResource(id = icon),
            contentDescription = name,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black
        )
        Text(
            text = name,
            fontSize = 12.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black
        )
    }
}