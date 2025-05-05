package dev.pandesal.sbp.presentation.categories

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState: MutableStateFlow<CategoriesUiState> = MutableStateFlow(CategoriesUiState.Initial)
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = CategoriesUiState.Loading


        val categoryGroups = listOf(
                CategoryGroup(id = "group1", name = "Essentials", description = "Groceries, Rent, Utilities", icon = ""),
                CategoryGroup(id = "group2", name = "Lifestyle", description = "Movies, Dining Out", icon = ""),
                CategoryGroup(id = "group3", name = "Wants", description = "Hobbies", icon = "")
            )

        val categories = listOf(
            Category(id = "1", name = "Groceries", categoryGroupId = "group1", description = "Groceries", icon = "", categoryType = TransactionType.OUTFLOW, weight = 0),
            Category(id = "2", name = "Rent", categoryGroupId = "group1", description = "Rent", icon = "", categoryType = TransactionType.OUTFLOW, weight = 0),
            Category(id = "3", name = "Movies", categoryGroupId = "group2", description = "Movies", icon = "", categoryType = TransactionType.OUTFLOW, weight = 0),
            Category(id = "4", name = "Dining Out", categoryGroupId = "group2", description = "Dining Out", icon = "", categoryType = TransactionType.OUTFLOW, weight = 0),
            Category(id = "5", name = "Telescope", categoryGroupId = "group3", description = "", icon = "", categoryType = TransactionType.OUTFLOW, weight = 0),
            Category(id = "6", name = "Camera", categoryGroupId = "group3", description = "", icon = "", categoryType = TransactionType.OUTFLOW, weight = 0),
            Category(id = "7", name = "Travel", categoryGroupId = "group3", description = "", icon = "", categoryType = TransactionType.OUTFLOW, weight = 0)
        )

        _uiState.value = CategoriesUiState.Success(
            categoryGroups = categoryGroups,
            categories = categories
        )
    }


}
