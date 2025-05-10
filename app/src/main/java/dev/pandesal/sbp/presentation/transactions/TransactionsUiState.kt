package dev.pandesal.sbp.presentation.transactions

import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.CategoryWithBudget
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.model.Transaction
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface TransactionsUiState {
    data object Initial : TransactionsUiState
    data object Loading : TransactionsUiState
    data class Success(
        val transactions: List<Transaction>
    ) : TransactionsUiState
    data class Error(val errorMessage: String) : TransactionsUiState
}