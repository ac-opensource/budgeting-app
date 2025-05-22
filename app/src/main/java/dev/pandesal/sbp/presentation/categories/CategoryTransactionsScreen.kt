package dev.pandesal.sbp.presentation.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import dev.pandesal.sbp.presentation.components.SkeletonLoader
import dev.pandesal.sbp.presentation.transactions.TransactionsContent
import dev.pandesal.sbp.presentation.transactions.TransactionsUiState

@Composable
fun CategoryTransactionsScreen(
    categoryId: Int,
    viewModel: CategoryTransactionsViewModel = hiltViewModel(),
) {
    val navManager = LocalNavigationManager.current
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(categoryId) { viewModel.load(categoryId.toString()) }

    when (val state = uiState.value) {
        TransactionsUiState.Initial, TransactionsUiState.Loading -> SkeletonLoader()
        is TransactionsUiState.Success -> {
            TransactionsContent(
                transactions = state.transactions,
                onNewTransactionClick = {
                    navManager.navigate(NavigationDestination.NewTransaction(null))
                },
                onTransactionClick = {
                    navManager.navigate(NavigationDestination.NewTransaction(it))
                }
            )
        }
        is TransactionsUiState.Error -> SkeletonLoader()
    }
}
