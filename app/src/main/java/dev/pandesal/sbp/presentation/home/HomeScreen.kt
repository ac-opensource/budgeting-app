package dev.pandesal.sbp.presentation.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.R
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import dev.pandesal.sbp.presentation.components.FilterTab
import dev.pandesal.sbp.presentation.theme.StopBeingPoorTheme
import dev.pandesal.sbp.presentation.transactions.TransactionsContent
import dev.pandesal.sbp.presentation.transactions.TransactionsUiState
import dev.pandesal.sbp.presentation.transactions.TransactionsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    transactionsViewModel: TransactionsViewModel = hiltViewModel()
) {
    val navController = LocalNavigationManager.current
    val homeState = viewModel.uiState.collectAsState()
    val transactionsState = transactionsViewModel.uiState.collectAsState()

    if (homeState.value is HomeUiState.Success &&
        transactionsState.value is TransactionsUiState.Success
    ) {
        val state = homeState.value as HomeUiState.Success
        val txState = transactionsState.value as TransactionsUiState.Success

        val totalAmount = state.accounts.sumOf { it.balance }
        val totalAllocated = state.favoriteBudgets.sumOf { it.allocated }
        val categoryPercentages = state.favoriteBudgets.map { budget ->
            val pct = if (totalAllocated != 0.0) (budget.allocated / totalAllocated) * 100.0 else 0.0
            budget.name to pct
        }

        HomeScreenContent(
            totalAmount = totalAmount,
            categoryPercentages = categoryPercentages,
            transactions = txState.transactions,
            onViewAllTransactions = {
                navController.navigate(NavigationDestination.Transactions)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeScreenContent(
    totalAmount: Double,
    categoryPercentages: List<Pair<String, Double>>,
    transactions: List<Transaction>,
    onViewAllTransactions: () -> Unit = {}
) {
    val topCategories = categoryPercentages
        .sortedByDescending { it.second }
        .take(3)
    val othersPercentage = 100.0 - topCategories.sumOf { it.second }

    val displayCategories = topCategories.toMutableList()

    if (othersPercentage > 0f) {
        displayCategories.add("Others" to othersPercentage)
    }

    val sheetHeightPx = remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val transactionTypes =
        listOf(TransactionType.INFLOW, TransactionType.OUTFLOW, TransactionType.TRANSFER)
    val selectedIndex = remember { mutableIntStateOf(0) }
    var isIconExpanded by remember { mutableStateOf(scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) }
    val systemBarInsets = WindowInsets.systemBars.asPaddingValues(LocalDensity.current)
    val navigationBarHeight = systemBarInsets.calculateBottomPadding()

    LaunchedEffect(scaffoldState.bottomSheetState.targetValue) {
        isIconExpanded = scaffoldState.bottomSheetState.targetValue == SheetValue.Expanded
    }

    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        containerColor = Color.Transparent,
        scaffoldState = scaffoldState,
        sheetPeekHeight = with(density) {
            val heightWithPadding = sheetHeightPx.floatValue.toDp() - navigationBarHeight - 24.dp
            if (heightWithPadding > 0.dp) {
                heightWithPadding
            } else {
                400.dp
            }
        },
        sheetShadowElevation = 16.dp,
        sheetContent = {
            TransactionsContent(
                transactions = transactions,
                onNewTransactionClick = {

                },
                onTransactionClick = {

                }
            )
        },
        sheetDragHandle = {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Surface(
                    modifier =
                        Modifier
                            .padding(top = 16.dp)
                            .semantics {
                                contentDescription = "drag handle"
                            },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(Modifier.size(width = 32.dp, height = 4.dp))
                }


                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text("Transactions", style = MaterialTheme.typography.titleMedium)

                    Spacer(Modifier.weight(1f))

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

                FilterTab(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 8.dp),
                    selectedIndex.value, listOf("All", "Inflow", "Outflow")
                ) { index ->
                    selectedIndex.intValue = index
                }
            }


        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            Image(
                painterResource(R.drawable.ic_notif),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Consolidated Account", style = MaterialTheme.typography.labelLarge)
            Text(
                text = "$${"%,.2f".format(totalAmount)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            val thickStrokeWidth = with(LocalDensity.current) { 8.dp.toPx() }
            val thickStroke =
                remember(thickStrokeWidth) {
                    Stroke(
                        width = thickStrokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {

                displayCategories.forEach { (label, percent) ->
                    LinearWavyProgressIndicator(
                        progress = {
                            1f
                        },
                        modifier = Modifier
                            .weight(percent.toFloat())
                            .fillMaxHeight(),
                        amplitude = {
                            1f
                        },
                        stroke = thickStroke,
                        color = getCategoryColor(label),
                        trackColor = Color.LightGray,
                        wavelength = 12.dp,
                        waveSpeed = (0.1).dp
                    )
//                    Box(
//                        modifier = Modifier
//                            .weight(percent.toFloat())
//                            .clip(RoundedCornerShape(8.dp))
//                            .fillMaxHeight()
//                            .background(getCategoryColor(label))
//                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            displayCategories.forEach { (label, percent) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(50))
                            .background(getCategoryColor(label))
                    )
                    Text(label, style = MaterialTheme.typography.labelLarge)
                    HorizontalDivider(
                        color = Color.LightGray,
                        thickness = 0.4.dp,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${percent.toInt()}%", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = Color.Transparent,
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        val positionY = coordinates.positionInWindow().y
                        val calculatedPeekHeight = screenHeightPx - positionY
                        sheetHeightPx.floatValue = calculatedPeekHeight
                    }
            )
        }
    }
}


fun getCategoryColor(label: String): Color = when (label) {
    "Invest" -> Color(0xFF4B3B60) // Deep muted violet – pairs well with Honeydew
    "Healthcare" -> Color(0xFF6E8894) // Slate gray – theme color, keeps things consistent
    "Self Reward" -> Color(0xFFAD6A6C) // Dusty rose – mellow but visible
    "Food" -> Color(0xFF5E8D74) // Darker Cambridge green
    "Others" -> Color(0xFF837060) // Warm taupe – grounded, neutral contrast
    "" -> Color(0xFFAAAAAA) // Medium gray for generic fallback
    else -> Color(0xFF999999) // Soft gray fallback
}

@Preview
@Composable
fun HomeScreenPreview() {
    StopBeingPoorTheme {
        HomeScreenContent(
            totalAmount = 12345.67,
            categoryPercentages = listOf(
                "Invest" to 30.0,
                "Healthcare" to 20.0,
                "Self Reward" to 15.0,
                "Food" to 10.0,
                "Transport" to 5.0
            ),
            transactions = listOf(
                Transaction(
                    amount = BigDecimal(120.00),
                    createdAt = LocalDate.now(),
                    updatedAt = LocalDate.now(),
                    name = "Grocery Shopping",
                    category = Category(
                        id = 1,
                        name = "Groceries",
                        categoryGroupId = 1,
                        isArchived = false,
                        description = "",
                        icon = "",
                        categoryType = TransactionType.OUTFLOW,
                        weight = 1
                    ),
                    accountId = "1",
                    transactionType = TransactionType.OUTFLOW
                ),
                Transaction(
                    amount = BigDecimal(500.00),
                    createdAt = LocalDate.now().minusDays(1),
                    updatedAt = LocalDate.now().minusDays(1),
                    name = "Salary",
                    category = Category(
                        id = 1,
                        name = "Income",
                        categoryGroupId = 1,
                        isArchived = false,
                        description = "",
                        icon = "",
                        categoryType = TransactionType.OUTFLOW,
                        weight = 1
                    ),
                    accountId = "1",
                    transactionType = TransactionType.INFLOW
                )
            )
        )
    }

}