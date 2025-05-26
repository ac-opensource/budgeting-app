package dev.pandesal.sbp.presentation.transactions.newtransaction

import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.Account

sealed interface NewTransactionUiState {
    data object Initial : NewTransactionUiState
    data object Loading : NewTransactionUiState

    data class ValidationErrors(
        val amount: Boolean = false,
        val category: Boolean = false,
        val from: Boolean = false,
        val to: Boolean = false,
    )

    data class Success(
        val groupedCategories: Map<CategoryGroup, List<Category>>,
        val accounts: List<Account>,
        val transaction: Transaction,
        val merchants: List<String>,
        val tags: List<String>,
        val errors: ValidationErrors = ValidationErrors(),
    ) : NewTransactionUiState

    data class Error(val errorMessage: String) : NewTransactionUiState
}
