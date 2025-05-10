package dev.pandesal.sbp.presentation.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.extensions.rememberReorderHapticFeedback
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination
import dev.pandesal.sbp.presentation.categories.CategoriesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionsContent(
    list: List<Transaction>,
    onNewTransactionClick: () -> Unit,
    onTransactionClick: (transaction: Transaction) -> Unit
) {
    var showNewCategoryGroup by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Transactions", style = MaterialTheme.typography.titleLarge)
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

        }
    }
}

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val navManager = LocalNavigationManager.current

    if (uiState.value is TransactionsUiState.Success) {
        val state = uiState.value as TransactionsUiState.Success
        TransactionsContent(
            state.transactions,
            onNewTransactionClick = { navManager.navigate(NavigationDestination.NewTransaction) },
            onTransactionClick = { navManager.navigate(NavigationDestination.TransactionDetails) }
        )
    }
}
