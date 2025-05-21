package dev.pandesal.sbp.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TravelTagScreen(
    viewModel: TravelTagViewModel = hiltViewModel(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    if (state is TravelTagUiState.Ready) {
        val tag = (state as TravelTagUiState.Ready).tag

        ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .imePadding(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.weight(1f))

                ElevatedCard(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
                ) {
                    IconButton(
                        modifier = Modifier
                            .height(24.dp)
                            .padding(4.dp),
                        onClick = onDismissRequest
                    ) { Icon(Icons.Filled.Close, contentDescription = null) }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ElevatedCard(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Travel Tag")
                        ElevatedCard(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .fillMaxWidth()
                        ) {
                            BasicTextField(
                                value = tag,
                                onValueChange = viewModel::updateTag,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalFloatingToolbar(
                    expanded = true,
                    floatingActionButton = {
                        FloatingToolbarDefaults.VibrantFloatingActionButton(
                            onClick = {
                                viewModel.save()
                                onDismissRequest()
                            },
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    },
                    content = {}
                )
            }
        }
    }
}

