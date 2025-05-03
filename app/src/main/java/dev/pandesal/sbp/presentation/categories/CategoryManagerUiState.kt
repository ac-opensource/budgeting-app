package dev.pandesal.sbp.presentation.categories

import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel

sealed interface CategoryManagerUiState {
    data object Initial : CategoryManagerUiState
    data object Loading : CategoryManagerUiState
    data class Success(
        val categoryGroups: List<CategoryGroup>,
        val categories: List<Category>,
    ) : CategoryManagerUiState
    data class Error(val errorMessage: String) : CategoryManagerUiState
}