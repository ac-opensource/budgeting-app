package dev.pandesal.sbp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.pandesal.sbp.presentation.components.BottomBar
import dev.pandesal.sbp.presentation.theme.StopBeingPoorTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StopBeingPoorTheme {
                val navController = rememberNavController()
                var fabVisible by remember { mutableStateOf(true) }

                val scrollConnection = remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                            if (available.y < -5) fabVisible = false  // scrolling down
                            else if (available.y > 5) fabVisible = true // scrolling up
                            return Offset.Zero
                        }
                    }
                }
                Scaffold(
                    floatingActionButton = {
                        AnimatedVisibility(
                            visible = fabVisible,
                            enter = slideInVertically { fullHeight -> fullHeight }, // slides in from bottom
                            exit = slideOutVertically { fullHeight -> fullHeight }  // slides out to bottom
                        ) {
                            BottomBar(navController)
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .nestedScroll(scrollConnection)
                    ) {
                        AppNavigation(navController)
                    }
                }
            }
        }
    }
}
