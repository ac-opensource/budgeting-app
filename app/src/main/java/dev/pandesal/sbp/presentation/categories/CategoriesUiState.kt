package dev.pandesal.sbp.presentation.categories

import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface CategoriesUiState {
    data object Initial : CategoriesUiState
    data object Loading : CategoriesUiState
    data class Success(
        val categoryGroups: List<CategoryGroup>,
        val categories: List<Category>,
    ) : CategoriesUiState
    data class Error(val errorMessage: String) : CategoriesUiState
}