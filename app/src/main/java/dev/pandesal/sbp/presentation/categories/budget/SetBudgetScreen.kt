package dev.pandesal.sbp.presentation.categories.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.twotone.ArrowDropDown
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.categories.CategoriesViewModel
import dev.pandesal.sbp.presentation.categories.CategoriesUiState
import dev.pandesal.sbp.presentation.goals.GoalsViewModel
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun SetBudgetScreen(
    categoryId: Int,
    initialAmount: BigDecimal = BigDecimal.ZERO,
    categoriesViewModel: CategoriesViewModel = hiltViewModel(),
    goalsViewModel: GoalsViewModel = hiltViewModel()
) {
    val nav = LocalNavigationManager.current
    SetBudgetContent(
        initialAmount = initialAmount,
        onSubmit = { amount, isGoal, date ->
            if (isGoal) {
                val name = (categoriesViewModel.uiState.value as? CategoriesUiState.Success)
                    ?.categoriesWithBudget
                    ?.firstOrNull { it.category.id == categoryId }
                    ?.category?.name ?: ""
                goalsViewModel.addGoal(
                    name = name,
                    target = amount,
                    dueDate = date,
                    categoryId = categoryId
                )
            } else {
                categoriesViewModel.setBudgetForCategory(categoryId, amount)
            }
        },
        onCancel = { },
        onDismissRequest = { nav.navigateUp() }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SetBudgetContent(
    initialAmount: BigDecimal = BigDecimal.ZERO,
    onSubmit: (amount: BigDecimal, isGoal: Boolean, dueDate: LocalDate?) -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    var amountText by remember {
        mutableStateOf(
            if (initialAmount == BigDecimal.ZERO) "" else initialAmount.toPlainString()
        )
    }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.End
    ) {
        Spacer(modifier = Modifier.weight(1f))

        ElevatedCard(
            shape = RoundedCornerShape(50),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 16.dp
            ),
        ) {
            IconButton(
                modifier = Modifier
                    .height(24.dp)
                    .padding(4.dp),
                onClick = {
                    onCancel()
                    onDismissRequest()
                }
            ) {
                Icon(Icons.Filled.Close, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedCard(
            shape = RoundedCornerShape(10),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {


                Column {
                    Text(
                        if (selectedTab == 0) "Budget" else "Target Amount",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    ElevatedCard(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                    ) {
                        BasicTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                if (selectedTab == 1) {
                    ElevatedCard(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = selectedDate?.toString() ?: "Select Target Date",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )

                            Icon(
                                Icons.TwoTone.DateRange, contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
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
                        val amount = amountText.toBigDecimalOrNull() ?: BigDecimal.ZERO
                        onSubmit(amount, selectedTab == 1, selectedDate.takeIf { selectedTab == 1 })
                        onDismissRequest()
                    },
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            },
            content = {

                Row(
                    Modifier.padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val options = listOf("Budget", "Goal")
                    options.forEachIndexed { index, label ->
                        ToggleButton(
                            checked = selectedTab == index,
                            onCheckedChange = { selectedTab = index },
                            modifier = Modifier.wrapContentSize(),
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            }
                        ) {
                            Text(
                                label,
                                color = if (selectedTab == index) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    IconButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate =
                                Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                        }
                        showDatePicker = false
                    }) { Icon(Icons.Default.Check, contentDescription = null) }
                },
                dismissButton = {
                    IconButton(onClick = { showDatePicker = false }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
