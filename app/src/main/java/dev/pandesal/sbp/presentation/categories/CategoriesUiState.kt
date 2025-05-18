package dev.pandesal.sbp.presentation.categories

import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.CategoryWithBudget
import dev.pandesal.sbp.domain.model.MonthlyBudget
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface CategoriesUiState {
    data object Initial : CategoriesUiState
    data object Loading : CategoriesUiState
    data class Success(
        val categoryGroups: List<CategoryGroup>,
        val categoriesWithBudget: List<CategoryWithBudget>,
        val showTemplatePrompt: Boolean,
    ) : CategoriesUiState
    data class Error(val errorMessage: String) : CategoriesUiState
}