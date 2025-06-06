package dev.pandesal.sbp.presentation.categories.new

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.Alignment
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingToolbarDefaults.vibrantFloatingToolbarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.categories.CategoriesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewCategoryScreen(
    groupId: Int,
    groupName: String,
    categoriesViewModel: CategoriesViewModel = hiltViewModel(),
) {
    val nav = LocalNavigationManager.current
    NewCategoryScreen(
        groupId = groupId,
        groupName = groupName,
        onSubmit = { name, id ->
            categoriesViewModel.createCategory(name, id)
        },
        onCancel = { },
        onDismissRequest = { nav.navigateUp() }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewCategoryScreen(
    sheetState: SheetState = rememberModalBottomSheetState(),
    groupId: Int,
    groupName: String,
    initialName: String = "",
    onSubmit: (name: String, groupId: Int) -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.End
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
                onClick = {
                    onCancel()
                    onDismissRequest()
                }
            ) {
                Icon(Icons.Filled.Close, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedCard(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(10),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Add Category to Group: $groupName")

                Column {
                    Text("Category Name")
                    ElevatedCard(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                    ) {
                        BasicTextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        FloatingActionButton(
            onClick = {
                onSubmit(name, groupId)
                onDismissRequest()
            },
            shape = FloatingActionButtonDefaults.shape,
            containerColor = vibrantFloatingToolbarColors().fabContainerColor,
            contentColor = vibrantFloatingToolbarColors().fabContentColor,
        ) {
            Icon(Icons.Default.Check, contentDescription = null)

        }
    }
}