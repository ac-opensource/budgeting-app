package dev.pandesal.sbp.presentation.transactions.newtransaction

import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.Account

sealed interface NewTransactionUiState {
    data object Initial : NewTransactionUiState
    data object Loading : NewTransactionUiState
    data class Success(
        val groupedCategories: Map<CategoryGroup, List<Category>>,
        val accounts: List<Account>,
        val transaction: Transaction,
        val merchants: List<String>
    ) : NewTransactionUiState
    data class Error(val errorMessage: String) : NewTransactionUiState
}