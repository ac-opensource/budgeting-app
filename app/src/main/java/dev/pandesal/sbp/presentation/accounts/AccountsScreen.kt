package dev.pandesal.sbp.presentation.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNew by remember { mutableStateOf(false) }

    if (showNew) {
        NewAccountScreen(
            onSubmit = { name, type -> viewModel.addAccount(name, type) },
            onCancel = { },
            onDismissRequest = { showNew = false }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showNew = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is AccountsUiState.Loading -> {
                Text("Loading...", modifier = Modifier.padding(16.dp))
            }
            is AccountsUiState.Success -> {
                AccountsContent(state.accounts, modifier = Modifier.padding(padding))
            }
            is AccountsUiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun AccountsContent(accounts: List<dev.pandesal.sbp.domain.model.Account>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(accounts) { account ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(account.name, style = MaterialTheme.typography.titleMedium)
                Text(account.type.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
