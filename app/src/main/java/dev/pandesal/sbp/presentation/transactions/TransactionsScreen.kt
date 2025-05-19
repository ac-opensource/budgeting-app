package dev.pandesal.sbp.presentation.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import dev.pandesal.sbp.presentation.components.TransactionItem
import dev.pandesal.sbp.presentation.components.SkeletonLoader
import java.time.LocalDate


@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val navManager = LocalNavigationManager.current

    if (uiState.value is TransactionsUiState.Initial) {
        SkeletonLoader()
    } else if (uiState.value is TransactionsUiState.Success) {
        val state = uiState.value as TransactionsUiState.Success
        TransactionsContent(
            state.transactions,
            onNewTransactionClick = { navManager.navigate(NavigationDestination.NewTransaction) },
            onTransactionClick = { transaction ->
                navManager.navigate(NavigationDestination.TransactionDetails(transaction))
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsContent(
    transactions: List<Transaction>,
    onNewTransactionClick: () -> Unit,
    onTransactionClick: (transaction: Transaction) -> Unit
) {
    var showNewCategoryGroup by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val sheetHeightPx = remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val screenHeightDp = with(density) { LocalConfiguration.current.screenHeightDp.dp }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

//    LaunchedEffect(Unit) {
//        scope.launch {
//            scaffoldState.bottomSheetState.expand()
//        }
//    }
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
    ) {

        val groupedTransactions = transactions.groupBy {
            when (it.createdAt) {
                LocalDate.now() -> "Today"
                LocalDate.now().minusDays(1) -> "Yesterday"
                else -> it.createdAt.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            groupedTransactions.forEach { (dateLabel, txList) ->
                stickyHeader {
                    ElevatedFilterChip(
                        modifier = Modifier
                            .wrapContentSize(),
                        onClick = {},
                        selected = dateLabel == "Today",
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White
                        ),
                        elevation = FilterChipDefaults.filterChipElevation(
                            elevation = 4.dp,
                        ),
                        label = {
                            Text(
                                text = dateLabel,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        })
                }
                items(txList) { transaction ->
                    TransactionItem(
                        tx = transaction,
                        modifier = Modifier.clickable { onTransactionClick(transaction) }
                    )
                }

                item {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        Spacer(Modifier.height(120.dp))
    }
}