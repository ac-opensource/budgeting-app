package dev.pandesal.sbp.presentation.transactions.recurring

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.components.TransactionItem

@Composable
fun RecurringTransactionsScreen(
    viewModel: RecurringTransactionsViewModel = hiltViewModel()
) {
    val navManager = LocalNavigationManager.current
    val state = viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navManager.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
            Text(
                text = "Recurring Transactions",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (state.value is RecurringTransactionsUiState.Success) {
            val list = (state.value as RecurringTransactionsUiState.Success).transactions
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(list) { rec ->
                    TransactionItem(
                        tx = rec.transaction,
                        modifier = Modifier.clickable { /* TODO: open details */ }
                    )
                }
            }
        }
    }
}
