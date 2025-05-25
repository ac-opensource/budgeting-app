package dev.pandesal.sbp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Event
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
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.presentation.components.TravelModeBanner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.presentation.theme.StopBeingPoorTheme
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.Black.toArgb(),
                darkScrim = Color.Black.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color(0xFFCEEDDB).toArgb(),
                darkScrim = Color.Black.toArgb()
            )
        )
        setContent {
            val exitAlwaysScrollBehavior =
                FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = Bottom)
            val settingsViewModel: dev.pandesal.sbp.presentation.settings.SettingsViewModel =
                androidx.hilt.navigation.compose.hiltViewModel()
            val settings by settingsViewModel.settings.collectAsState()
            val travelSpent by settingsViewModel.travelSpent.collectAsState()
            val navController = rememberNavController()
            val entries = navController.visibleEntries.collectAsState()
            StopBeingPoorTheme(darkTheme = settings.darkMode) {
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

                val systemBarInsets = WindowInsets.systemBars.asPaddingValues(LocalDensity.current)
                val statusBarHeight = systemBarInsets.calculateTopPadding()

                val backgroundDiagonalGradient = Brush.linearGradient(
                    colors = listOf(
                        Color.White,
                        Color.White,
//                        if (isSystemInDarkTheme()) Color.Black else Color.White,
//                        MaterialTheme.colorScheme.surface,  // Honeydew (surface)
//                        MaterialTheme.colorScheme.background, // Cambridge Blue (background)
                    ),
                )

                Scaffold(
                    modifier = Modifier
                        .nestedScroll(exitAlwaysScrollBehavior)
                        .background(backgroundDiagonalGradient),
                    containerColor = Color.Transparent,
                    topBar = {

                        AnimatedVisibility(
                            visible = entries.value.lastOrNull()?.destination?.route?.lowercase()
                                ?.contains("home") == true,
                            modifier = Modifier.fillMaxWidth(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(statusBarHeight)
                                    .background(Color(0xFFECECEC))
                            ) {}
                        }

                        AnimatedVisibility(
                            visible = settings.isTravelMode,
                            modifier = Modifier.fillMaxWidth(),
                            enter = expandVertically(expandFrom = Alignment.Top) + slideInVertically(
                                initialOffsetY = { -it }) + fadeIn(),
                            exit = shrinkVertically(shrinkTowards = Alignment.Top) + slideOutVertically(
                                targetOffsetY = { -it }) + fadeOut()
                        ) {
                            TravelModeBanner(
                                tag = settings.travelTag,
                                currency = settings.travelCurrency,
                                total = travelSpent,
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

                        AppNavigation(navController)

                        HorizontalFloatingToolbar(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = -ScreenOffset),
                            expanded = true,
                            floatingActionButton = {
                                FloatingToolbarDefaults.VibrantFloatingActionButton(
                                    onClick = {
                                        navController.navigate(
                                            NavigationDestination.NewTransaction(
                                                null
                                            )
                                        ) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
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
                                    navController.navigate(NavigationDestination.Reminders) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }) {
                                    Icon(
                                        Icons.Filled.Event,
                                        contentDescription = "Reminders"
                                    )
                                }

                                IconButton(onClick = {
                                    navController.navigate(NavigationDestination.Insights) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
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
                                        Icons.Filled.AccountCircle,
                                        contentDescription = "Localized description"
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
