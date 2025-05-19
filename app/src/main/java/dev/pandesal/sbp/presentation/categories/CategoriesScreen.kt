package dev.pandesal.sbp.presentation.categories

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.CategoryWithBudget
import dev.pandesal.sbp.extensions.ReorderHapticFeedbackType
import dev.pandesal.sbp.extensions.rememberReorderHapticFeedback
import dev.pandesal.sbp.extensions.currencySymbol
import dev.pandesal.sbp.extensions.format
import dev.pandesal.sbp.presentation.categories.budget.SetBudgetScreen
import dev.pandesal.sbp.presentation.categories.new.NewCategoryGroupScreen
import dev.pandesal.sbp.presentation.categories.new.NewCategoryScreen
import dev.pandesal.sbp.presentation.components.SkeletonLoader
import dev.pandesal.sbp.presentation.categories.components.CategoryBudgetPieChart
import dev.pandesal.sbp.presentation.home.components.BudgetSummaryHeader
import dev.pandesal.sbp.presentation.goals.GoalsViewModel
import dev.pandesal.sbp.domain.model.Goal
import dev.pandesal.sbp.presentation.goals.GoalsUiState
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import java.time.temporal.ChronoUnit
import java.time.LocalDate
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CategoriesListContent(
    parentList: List<CategoryGroup>,
    categoriesWithBudget: List<CategoryWithBudget>,
    goals: List<Goal>,
    onAddCategoryGroup: (name: String) -> Unit,
    onAddCategory: (name: String, groupId: Int) -> Unit,
    onAddBudget: (amount: BigDecimal, categoryId: Int) -> Unit,
    reorderGroup: (from: Int, to: Int) -> Unit,
    reorderCategory: (groupId: Int, from: Int, to: Int) -> Unit,
    onEditGroup: (CategoryGroup, String) -> Unit,
    onDeleteGroup: (CategoryGroup) -> Unit,
    onEditCategory: (Category, String) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    editMode: Boolean,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    val newCategorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var groupList by remember { mutableStateOf(parentList) }

    val navManager = LocalNavigationManager.current


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

    var editGroup by remember { mutableStateOf<CategoryGroup?>(null) }
    var editCategory by remember { mutableStateOf<Category?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {

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
                            .longPressDraggableHandle(
                                onDragStarted = {},
                                onDragStopped = {},
                                interactionSource = interactionSource,
                            )
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
                            if (editMode) {
                                IconButton(onClick = { editGroup = item }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit Group")
                                }
                                IconButton(onClick = { onDeleteGroup(item) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete Group")
                                }
                            }
                            val navManager = LocalNavigationManager.current
                            TextButton(onClick = {
                                navManager.navigate(
                                    NavigationDestination.NewCategory(
                                        item.id,
                                        item.name
                                    )
                                )
                            }) {
                                Text("Add Category")
                            }
                            Spacer(Modifier.size(16.dp))
                        }

                        ChildListContent(
                            childCategories = childCategories,
                            goals = goals,
                            onAddBudgetClick = { id ->
                                navManager.navigate(
                                    NavigationDestination.SetBudget(id, null)
                                )
                            },
                            reorderCategory = { from, to ->
                                reorderCategory(item.id, from, to)
                            },
                            onEditCategory = { editCategory = it },
                            onDeleteCategory = { onDeleteCategory(it) },
                            onEditBudget = { amount, id ->
                                navManager.navigate(
                                    NavigationDestination.SetBudget(
                                        id,
                                        amount.toString()
                                    )
                                )
                            },
                            editMode = editMode
                        )
                    }
                }
            }
        }
    }





    if (editGroup != null) {
        NewCategoryGroupScreen(
            sheetState = sheetState,
            initialName = editGroup!!.name,
            onSubmit = { name ->
                onEditGroup(editGroup!!, name)
                editGroup = null
            },
            onCancel = { editGroup = null },
            onDismissRequest = { editGroup = null }
        )
    }

    if (editCategory != null) {
        NewCategoryScreen(
            sheetState = newCategorySheetState,
            groupId = editCategory!!.categoryGroupId,
            groupName = groupList.firstOrNull { it.id == editCategory!!.categoryGroupId }?.name
                ?: "",
            initialName = editCategory!!.name,
            onSubmit = { name, _ ->
                onEditCategory(editCategory!!, name)
                editCategory = null
            },
            onCancel = { editCategory = null },
            onDismissRequest = { editCategory = null }
        )
    }
}

@Composable
private fun ChildListContent(
    childCategories: List<CategoryWithBudget>,
    goals: List<Goal>,
    onAddBudgetClick: (categoryId: Int) -> Unit,
    reorderCategory: (from: Int, to: Int) -> Unit,
    onEditCategory: (Category) -> Unit,
    onEditBudget: (
        budgetTargetAmount: BigDecimal,
        selectedCategoryId: Int
    ) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    editMode: Boolean
) {
    var childList by remember { mutableStateOf(childCategories) }
    val goalMap = remember(goals) { goals.associateBy { it.categoryId } }

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
                .height((68 * childList.size).dp + 16.dp)
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
                            .height(60.dp)
                            .longPressDraggableHandle(
                                onDragStarted = {},
                                onDragStopped = {},
                                interactionSource = interactionSource,
                            )
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

                            if (editMode) {
                                if (item.budget != null) {
                                    IconButton(onClick = {
                                        onEditBudget(
                                            item.budget.allocated,
                                            item.category.id
                                        )
                                    }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Edit Budget")
                                    }
                                } else {
                                    IconButton(onClick = { onAddBudgetClick(item.category.id) }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Set Budget")
                                    }
                                }
                                IconButton(onClick = { onEditCategory(item.category) }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit Category")
                                }
                                IconButton(onClick = { onDeleteCategory(item.category) }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Delete Category"
                                    )
                                }
                            } else {
                                if (item.budget != null) {
                                    item.budget.let {
                                        val symbol = it.currency.currencySymbol()
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("$symbol${it.spent.format()} / $symbol${it.allocated.format()}")
                                            LinearProgressIndicator(
                                                progress = {
                                                    if (it.allocated > BigDecimal.ZERO)
                                                        (it.spent / it.allocated).toFloat()
                                                            .coerceIn(0f, 1f)
                                                    else 0f
                                                },
                                                modifier = Modifier.fillMaxWidth(0.5f)
                                            )
                                        }
                                    }
                                } else {
                                    val goal = goalMap[item.category.id]
                                    if (goal != null) {
                                        Column(horizontalAlignment = Alignment.End) {
                                            goal.dueDate?.let {
                                                val months = ChronoUnit.MONTHS.between(
                                                    LocalDate.now().withDayOfMonth(1),
                                                    it.withDayOfMonth(1)
                                                ).toInt()
                                                Text("Due: ${it}")
                                                Text("$months months left")
                                            }
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
                                }
                            }

                            Spacer(Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
fun CategoriesScreen(
    viewModel: CategoriesViewModel = hiltViewModel(),
    goalsViewModel: GoalsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val goalsState = goalsViewModel.uiState.collectAsState()
    val haptic = rememberReorderHapticFeedback()

    if (uiState.value is CategoriesUiState.Initial) {
        SkeletonLoader()
    } else if (uiState.value is CategoriesUiState.Success) {
        val state = uiState.value as CategoriesUiState.Success
        val goals = (goalsState.value as? GoalsUiState.Success)?.goals ?: emptyList()

        val scaffoldState = rememberBottomSheetScaffoldState()
        val scope = rememberCoroutineScope()
        var isIconExpanded by remember { mutableStateOf(scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) }
        val sheetHeightPx = remember { mutableFloatStateOf(0f) }
        val density = LocalDensity.current
        val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
        val systemBarInsets = WindowInsets.systemBars.asPaddingValues(LocalDensity.current)
        val navigationBarHeight = systemBarInsets.calculateBottomPadding()

        val newGroupSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var showTemplateDialog by remember { mutableStateOf(state.showTemplatePrompt) }
        var editMode by remember { mutableStateOf(false) }

        if (showTemplateDialog) {
            AlertDialog(
                onDismissRequest = {
                    showTemplateDialog = false
                    viewModel.dismissTemplatePrompt()
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.seedDefaultTemplate()
                        showTemplateDialog = false
                    }) { Text("Yes") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showTemplateDialog = false
                        viewModel.dismissTemplatePrompt()
                    }) { Text("No") }
                },
                title = { Text("Load Default Categories?") },
                text = { Text("Would you like to add default outflow categories?") }
            )
        }

        LaunchedEffect(scaffoldState.bottomSheetState.targetValue) {
            isIconExpanded = scaffoldState.bottomSheetState.targetValue == SheetValue.Expanded
        }

        val budgets = state.categoriesWithBudget.mapNotNull {
            it.budget?.allocated?.toDouble()?.let { amt -> it.category.name to amt }
        }
        val totalAllocated = budgets.sumOf { it.second }
        val percentages =
            if (totalAllocated != 0.0) budgets.map { it.first to (it.second / totalAllocated) * 100 } else emptyList()

        BottomSheetScaffold(
            containerColor = Color.Transparent,
            scaffoldState = scaffoldState,
            sheetPeekHeight = with(density) {
                val heightWithPadding =
                    sheetHeightPx.floatValue.toDp() - navigationBarHeight - 24.dp
                if (heightWithPadding > 0.dp) heightWithPadding else 400.dp
            },
            sheetShadowElevation = 16.dp,
            sheetDragHandle = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier
                            .padding(top = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Box(Modifier.size(width = 32.dp, height = 4.dp))
                    }
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Categories", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = { editMode = !editMode }) {
                            Text(if (editMode) "Done" else "Edit")
                        }
                        val navManager = LocalNavigationManager.current
                        TextButton(onClick = {
                            navManager.navigate(NavigationDestination.NewCategoryGroup)
                        }) {
                            Text("Add Category Group")
                        }
                        IconButton(
                            onClick = {
                                isIconExpanded = !isIconExpanded
                                scope.launch {
                                    if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                        scaffoldState.bottomSheetState.partialExpand()
                                    } else {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }
                            }
                        ) {
                            Crossfade(
                                targetState = isIconExpanded,
                                animationSpec = tween(200),
                                label = "icon crossfade"
                            ) { targetIsExpanded ->
                                if (targetIsExpanded) {
                                    Icon(Icons.Default.CloseFullscreen, contentDescription = null)
                                } else {
                                    Icon(Icons.Default.Fullscreen, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            },
            sheetContent = {
                CategoriesListContent(
                    parentList = state.categoryGroups,
                    categoriesWithBudget = state.categoriesWithBudget,
                    goals = goals,
                    onAddCategoryGroup = { name -> viewModel.createCategoryGroup(name) },
                    onAddCategory = { name, groupId ->
                        viewModel.createCategory(name, groupId)
                    },
                    onAddBudget = { amount, categoryId ->
                        viewModel.setBudgetForCategory(
                            categoryId,
                            amount
                        )
                    },
                    reorderGroup = { from, to ->
                        haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
                        viewModel.reorderGroup(from, to)
                    },
                    reorderCategory = { groupId, from, to ->
                        haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
                        viewModel.reorderCategory(from, to, groupId)
                    },
                    onEditGroup = { group, name -> viewModel.updateCategoryGroup(group, name) },
                    onDeleteGroup = { viewModel.deleteCategoryGroup(it) },
                    onEditCategory = { category, name -> viewModel.updateCategory(category, name) },
                    onDeleteCategory = { viewModel.deleteCategory(it) },
                    editMode = editMode
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Categories & Budgets",
                    style = MaterialTheme.typography.titleLargeEmphasized
                )

                Spacer(modifier = Modifier.height(16.dp))

                BudgetSummaryHeader(
                    unassigned = state.budgetSummary.unassigned,
                    assigned = state.budgetSummary.assigned,
                    currency = "PHP"
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (percentages.isNotEmpty()) {
                    CategoryBudgetPieChart(percentages)
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Create categories and budgets to see breakdown")
                    }
                }
                HorizontalDivider(
                    color = Color.Transparent,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        val positionY = coordinates.positionInWindow().y
                        val calculatedPeekHeight = screenHeightPx - positionY
                        sheetHeightPx.floatValue = calculatedPeekHeight
                    }
                )
            }
        }

    }
}
