package dev.pandesal.sbp.presentation.categories

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.CategoryWithBudget
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.extensions.ReorderHapticFeedbackType
import dev.pandesal.sbp.extensions.rememberReorderHapticFeedback
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.categories.budget.SetBudgetSheet
import dev.pandesal.sbp.presentation.categories.new.NewCategoryGroupScreen
import dev.pandesal.sbp.presentation.categories.new.NewCategoryScreen
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriesContent(
    parentList: List<CategoryGroup>,
    categoriesWithBudget: List<CategoryWithBudget>,
    onAddCategoryGroup: (name: String) -> Unit,
    onAddCategory: (name: String, groupId: Int) -> Unit,
    onAddBudget: (amount: BigDecimal, categoryId: Int) -> Unit,
    reorderGroup: (from: Int, to: Int) -> Unit,
    reorderCategory: (groupId: Int, from: Int, to: Int) -> Unit,
) {
    var showNewCategoryGroup by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showNewCategorySheet by remember { mutableStateOf(false) }
    var selectedGroupId by remember { mutableStateOf<Int?>(null) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    val newCategorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var groupList by remember { mutableStateOf(parentList) }

    LaunchedEffect(parentList) {
        groupList = parentList
    }

    val lazyGroupsState = rememberLazyListState()
    val reorderableLazyGroupsColumnState =
        rememberReorderableLazyListState(lazyGroupsState) { from, to ->
            groupList = groupList.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
            reorderGroup(from.index, to.index)
        }

    // State for Set Budget sheet
    var showSetBudgetSheet by remember { mutableStateOf(false) }
    var budgetTargetAmount by remember { mutableStateOf(BigDecimal.ZERO) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { showNewCategoryGroup = true }) {
                Text("Add Category Group")
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyGroupsState,
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(groupList, key = { _, item -> item.id }) { index, item ->
                ReorderableItem(reorderableLazyGroupsColumnState, item.id) {
                    val interactionSource = remember { MutableInteractionSource() }
                    val childCategories =
                        categoriesWithBudget.filter { it.category.categoryGroupId == item.id }

                    Card(
                        onClick = {},
                        modifier = Modifier
                            .wrapContentHeight()
                            .semantics {
                                customActions = listOf(
                                    CustomAccessibilityAction(
                                        label = "Move Up",
                                        action = {
                                            if (index > 0) {
                                                groupList = groupList.toMutableList().apply {
                                                    add(index - 1, removeAt(index))
                                                }
                                                true
                                            } else {
                                                false
                                            }
                                        }
                                    ),
                                    CustomAccessibilityAction(
                                        label = "Move Down",
                                        action = {
                                            if (index < groupList.size - 1) {
                                                groupList = groupList.toMutableList().apply {
                                                    add(index + 1, removeAt(index))
                                                }
                                                true
                                            } else {
                                                false
                                            }
                                        }
                                    ),
                                )
                            },
                        interactionSource = interactionSource,
                    ) {
                        Row(
                            Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Spacer(Modifier.size(16.dp))

                            Text(
                                item.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = {
                                selectedGroupId = item.id
                                showNewCategorySheet = true
                            }) {
                                Text("Add Category")
                            }
                            IconButton(
                                modifier = Modifier
                                    .draggableHandle(
                                        onDragStarted = { /* Optionally haptic */ },
                                        onDragStopped = { /* Optionally haptic */ },
                                        interactionSource = interactionSource,
                                    )
                                    .clearAndSetSemantics { },
                                onClick = {},
                            ) {
                                Icon(Icons.Rounded.Menu, contentDescription = "Reorder")
                            }
                        }

                        ChildListContent(
                            childCategories = childCategories,
                            onAddBudgetClick = {
                                selectedCategoryId = it
                                showSetBudgetSheet = true
                            },
                            reorderCategory = { from, to ->
                                reorderCategory(item.id, from, to)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showNewCategoryGroup) {
        NewCategoryGroupScreen(
            sheetState = sheetState,
            onSubmit = {
                onAddCategoryGroup(it)
                showNewCategoryGroup = false
            },
            onCancel = { showNewCategoryGroup = false },
            onDismissRequest = { showNewCategoryGroup = false }
        )
    }

    if (showNewCategorySheet && selectedGroupId != null) {
        NewCategoryScreen(
            sheetState = newCategorySheetState,
            groupId = selectedGroupId!!,
            onSubmit = { name, groupId ->
                onAddCategory(name, groupId)
                showNewCategorySheet = false
                selectedGroupId = null
            },
            onCancel = {
                showNewCategorySheet = false
                selectedGroupId = null
            },
            onDismissRequest = {
                showNewCategorySheet = false
                selectedGroupId = null
            }
        )
    }

    if (showSetBudgetSheet) {
        SetBudgetSheet(
            initialAmount = budgetTargetAmount,
            onSubmit = { amount ->
                onAddBudget(amount, selectedCategoryId!!)
                budgetTargetAmount = amount
                showSetBudgetSheet = false
            },
            onCancel = {
                showSetBudgetSheet = false
            },
            onDismissRequest = {
                showSetBudgetSheet = false
            }
        )
    }
}

@Composable
private fun ChildListContent(
    childCategories: List<CategoryWithBudget>,
    onAddBudgetClick: (categoryId: Int) -> Unit,
    reorderCategory: (from: Int, to: Int) -> Unit
) {
    var childList by remember { mutableStateOf(childCategories) }

    LaunchedEffect(childCategories) {
        childList = childCategories
    }

    val lazyCategoriesListState = rememberLazyListState()
    val reorderableLazyCategoriesColumnState =
        rememberReorderableLazyListState(lazyCategoriesListState) { from, to ->
            childList = childList.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
            reorderCategory(from.index, to.index)
        }
    Row {
        LazyColumn(
            modifier = Modifier
                .height((60 * childList.size).dp)
                .fillMaxWidth(),
            state = lazyCategoriesListState,
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(childList, key = { _, item -> item.category.id }) { index, item ->
                ReorderableItem(reorderableLazyCategoriesColumnState, item.category.id) {
                    val interactionSource = remember { MutableInteractionSource() }

                    Card(
                        onClick = {},
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .wrapContentHeight()
                            .semantics {
                                customActions = listOf(
                                    CustomAccessibilityAction(
                                        label = "Move Up",
                                        action = {
                                            if (index > 0) {
                                                childList = childList.toMutableList().apply {
                                                    add(index - 1, removeAt(index))
                                                }
                                                true
                                            } else {
                                                false
                                            }
                                        }
                                    ),
                                    CustomAccessibilityAction(
                                        label = "Move Down",
                                        action = {
                                            if (index < childList.size - 1) {
                                                childList = childList.toMutableList().apply {
                                                    add(index + 1, removeAt(index))
                                                }
                                                true
                                            } else {
                                                false
                                            }
                                        }
                                    ),
                                )
                            },
                        interactionSource = interactionSource,
                    ) {
                        Row(
                            Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Spacer(Modifier.size(16.dp))
                            Text(
                                item.category.name,
                                color = MaterialTheme.colorScheme.onTertiary
                            )

                            Spacer(Modifier.weight(1f))


                            if (item.budget != null) {
                                item.budget.let {
                                    Text("₱${it.spent} / ₱${it.allocated}")
                                }
                            } else {
                                TextButton(
                                    onClick = {
                                        onAddBudgetClick(item.category.id)
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onTertiary
                                    )
                                ) {
                                    Text("Set Budget")
                                }
                            }

                            IconButton(
                                modifier = Modifier
                                    .draggableHandle(
                                        onDragStarted = { /* Optionally haptic */ },
                                        onDragStopped = { /* Optionally haptic */ },
                                        interactionSource = interactionSource,
                                    )
                                    .clearAndSetSemantics { },
                                onClick = {},
                            ) {
                                Icon(Icons.Rounded.Menu, contentDescription = "Reorder")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val navManager = LocalNavigationManager.current
    val haptic = rememberReorderHapticFeedback()

    if (uiState.value is CategoriesUiState.Success) {
        val state = uiState.value as CategoriesUiState.Success
        CategoriesContent(
            parentList = state.categoryGroups,
            categoriesWithBudget = state.categoriesWithBudget,
            onAddCategoryGroup = { name ->
                viewModel.createCategoryGroup(name)
            },
            onAddCategory = { name, groupId ->
                viewModel.createCategory(name, groupId)
            },
            onAddBudget = { amount, categoryId ->
                viewModel.setBudgetForCategory(categoryId, amount)
            },
            reorderGroup = { from, to ->
                haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
                viewModel.reorderGroup(from, to)
            },
            reorderCategory = { groupId, from, to ->
                haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
                viewModel.reorderCategory(from, to, groupId)
            }
        )
    }
}
