package dev.pandesal.sbp.presentation.transactions.newtransaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.presentation.LocalNavigationManager

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewRecurringTransactionScreen() {
    val navManager = LocalNavigationManager.current

    var selectedInterval by remember { mutableStateOf(RecurringInterval.MONTHLY) }
    var cutoffDays by remember { mutableIntStateOf(21) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.End
    ) {
        Spacer(modifier = Modifier.weight(1f))

        ElevatedCard(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
        ) {
            IconButton(
                modifier = Modifier
                    .height(24.dp)
                    .padding(4.dp),
                onClick = { navManager.navigateUp() }
            ) {
                Icon(Icons.Filled.Close, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedCard(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(10),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                ) {
                    val options = listOf(
                        RecurringInterval.MONTHLY,
                        RecurringInterval.WEEKLY,
                        RecurringInterval.DAILY,
                        RecurringInterval.AFTER_CUTOFF
                    )
                    options.forEachIndexed { index, option ->
                        ToggleButton(
                            checked = selectedInterval == option,
                            onCheckedChange = { selectedInterval = option },
                            modifier = Modifier.weight(1f),
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            }
                        ) {
                            val label = option.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercaseChar() }
                            Text(
                                label,
                                color = if (selectedInterval == option) Color.White else Color.Black
                            )
                        }
                    }
                }

                if (selectedInterval == RecurringInterval.AFTER_CUTOFF) {
                    OutlinedTextField(
                        value = cutoffDays.toString(),
                        onValueChange = { input ->
                            cutoffDays = input.toIntOrNull() ?: 21
                        },
                        label = { Text("Days After Cutoff") },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalFloatingToolbar(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            expanded = true,
            floatingActionButton = {
                FloatingToolbarDefaults.VibrantFloatingActionButton(
                    onClick = {
                        navManager.navigateUp()
                    }
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            },
            content = {}
        )
    }
}
