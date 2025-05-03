package dev.pandesal.sbp.presentation.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.presentation.LocalNavigationManager
import dev.pandesal.sbp.presentation.NavigationDestination

@Composable
fun CategoriesScreen(
    viewModel: CategoryManagerViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val navManager = LocalNavigationManager.current

    CategoriesScreen(
        categoryGroups = (uiState.value as? CategoryManagerUiState.Success)?.categoryGroups ?: emptyList(),
        categories = (uiState.value as? CategoryManagerUiState.Success)?.categories ?: emptyList(),
        onAddCategoryGroupClick = {
            navManager.navigate(NavigationDestination.NewCategoryGroup)
        },
        onAddCategoryClick = { categoryId ->
            navManager.navigate(NavigationDestination.NewCategory(categoryId))
        }
    )
}

@Composable
private fun CategoriesScreen(
    categoryGroups: List<CategoryGroup>,
    categories: List<Category>,
    onAddCategoryGroupClick: () -> Unit,
    onAddCategoryClick: (categoryId: String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = onAddCategoryGroupClick) {
                Text("Add Category Group")
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            categoryGroups.forEach { group ->
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = group.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = { onAddCategoryClick(group.id) }) {
                                Text("Add Category")
                            }
                        }
                    }
                }

                val children = categories.filter { it.categoryGroupId == group.id }

                items(children) { category ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Text(category.name)
                        }
                    }
                }
            }
        }
    }
}