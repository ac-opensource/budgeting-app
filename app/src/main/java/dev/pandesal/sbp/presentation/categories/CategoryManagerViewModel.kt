package dev.pandesal.sbp.presentation.categories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.presentation.home.HomeUiState
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CategoryManagerViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState: MutableStateFlow<CategoryManagerUiState> = MutableStateFlow(CategoryManagerUiState.Initial)
    val uiState: StateFlow<CategoryManagerUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = CategoryManagerUiState.Loading


        val categoryGroups = listOf(
                CategoryGroup(id = "group1", name = "Essentials", description = "Groceries, Rent, Utilities", icon = ""),
                CategoryGroup(id = "group2", name = "Lifestyle", description = "Movies, Dining Out", icon = "")
            )

        val categories = listOf(
            Category(id = "1", name = "Groceries", categoryGroupId = "group1", description = "Groceries", icon = "", categoryType = TransactionType.OUTFLOW, weight = 0),
            Category(id = "2", name = "Rent", categoryGroupId = "group1", description = "Rent", icon = "", categoryType = TransactionType.OUTFLOW, weight = 0),
            Category(id = "3", name = "Movies", categoryGroupId = "group2", description = "Movies", icon = "", categoryType = TransactionType.OUTFLOW, weight = 0),
            Category(id = "4", name = "Dining Out", categoryGroupId = "group2", description = "Dining Out", icon = "", categoryType = TransactionType.OUTFLOW, weight = 0)
        )
        _uiState.value = CategoryManagerUiState.Success(
            categoryGroups = categoryGroups,
            categories = categories
        )
    }


}
