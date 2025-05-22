package dev.pandesal.sbp.presentation.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.AttendanceRecord
import dev.pandesal.sbp.presentation.LocalNavigationManager
import kotlinx.coroutines.delay

@Composable
fun AttendanceScreen(viewModel: AttendanceViewModel = hiltViewModel()) {
    val nav = LocalNavigationManager.current
    val state by viewModel.uiState.collectAsState()
    AttendanceContent(
        state = state,
        onMarkPresent = viewModel::markPresent,
        onBack = { nav.navigateUp() }
    )
}

@Composable
private fun AttendanceContent(
    state: AttendanceUiState,
    onMarkPresent: (String) -> Unit,
    onBack: () -> Unit
) {
    var showWow by remember { mutableStateOf(false) }
    val snackbarHostState = rememberSnackbarHostState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Attendance", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = onBack) { Icon(Icons.Filled.Star, contentDescription = "Back") }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        when (state) {
            AttendanceUiState.Loading -> Text("Loading...")
            is AttendanceUiState.Ready -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.records) { record ->
                        AttendanceRow(record, onMarkPresent)
                    }
                }
            }
        }
        Button(
            onClick = {
                showWow = true
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) { Text("Wow") }
    }

    if (showWow) {
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar("Wow!")
            delay(2000)
            showWow = false
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}

@Composable
private fun AttendanceRow(record: AttendanceRecord, onMarkPresent: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(record.name, style = MaterialTheme.typography.bodyLarge)
                Text(record.checkIn?.toString() ?: "Absent")
                if (record.lateCount > 0) {
                    Text("Late count: ${record.lateCount}")
                }
            }
            Button(onClick = { onMarkPresent(record.name) }) { Text("Check In") }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun AttendancePreview() {
    val records = listOf(
        AttendanceRecord("Alice"),
        AttendanceRecord("Bob"),
        AttendanceRecord("Charlie")
    )
    AttendanceContent(
        state = AttendanceUiState.Ready(records),
        onMarkPresent = {},
        onBack = {}
    )
}
