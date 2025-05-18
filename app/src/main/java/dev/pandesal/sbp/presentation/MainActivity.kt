package dev.pandesal.sbp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.pandesal.sbp.presentation.theme.StopBeingPoorTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val exitAlwaysScrollBehavior =
                FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = Bottom)
            StopBeingPoorTheme {
                val navController = rememberNavController()
                var fabVisible by remember { mutableStateOf(true) }
                var expanded by rememberSaveable { mutableStateOf(true) }
                val scrollConnection = remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            if (available.y < -5) fabVisible = false  // scrolling down
                            else if (available.y > 5) fabVisible = true // scrolling up
                            return Offset.Zero
                        }
                    }
                }

                val backgroundDiagonalGradient = Brush.linearGradient(
                    colors = listOf(
                        if (isSystemInDarkTheme()) Color.Black else Color.White,
                        MaterialTheme.colorScheme.surface,  // Honeydew (surface)
                        MaterialTheme.colorScheme.background, // Cambridge Blue (background)
                    ),
                )

                Scaffold(
                    modifier = Modifier
                        .nestedScroll(exitAlwaysScrollBehavior)
                        .background(backgroundDiagonalGradient),
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
//                            .nestedScroll(scrollConnection)
                    ) {
                        AppNavigation(navController)

                        HorizontalFloatingToolbar(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = -ScreenOffset),
                            expanded = expanded,
                            floatingActionButton = {
                                FloatingToolbarDefaults.VibrantFloatingActionButton(
                                    onClick = {
                                        navController.navigate(NavigationDestination.NewTransaction) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        navController.navigate(NavigationDestination.NewTransaction)
                                    },
                                ) {
                                    Icon(Icons.Filled.Add, "Localized description")
                                }
                            },
                            content = {
                                IconButton(onClick = {
                                    navController.navigate(NavigationDestination.Home) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }) {
                                    Icon(
                                        Icons.Filled.Home,
                                        contentDescription = "Localized description"
                                    )
                                }
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .offset(y = 8.dp, x = (-8).dp),
                                        ) {
                                            Icon(
                                                Icons.Filled.AttachMoney,
                                                contentDescription = "Localized description"
                                            )
                                        }
                                    }
                                ) {
                                    IconButton(onClick = {
                                        navController.navigate(NavigationDestination.Categories) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }) {
                                        Icon(
                                            Icons.Filled.PieChart,
                                            contentDescription = "Localized description"
                                        )
                                    }

                                }

                                IconButton(onClick = {
                                    navController.navigate(NavigationDestination.Accounts) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }) {
                                    Icon(
                                        Icons.Filled.AccountBalanceWallet,
                                        contentDescription = "Localized description"
                                    )
                                }

                                IconButton(onClick = {
                                    navController.navigate(NavigationDestination.Insights) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }) {
                                    Icon(
                                        Icons.Filled.BarChart,
                                        contentDescription = "Localized description"
                                    )
                                }
                                IconButton(onClick = {
                                    navController.navigate(NavigationDestination.Settings) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }) {
                                    Icon(Icons.Filled.MoreVert, contentDescription = "Settings")
                                }
                            },
                            scrollBehavior = exitAlwaysScrollBehavior
                        )
                    }
                }
            }
        }
    }
}
