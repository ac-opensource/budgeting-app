package dev.pandesal.sbp.presentation.transactions.recurring

import dev.pandesal.sbp.domain.model.RecurringTransaction

sealed interface RecurringTransactionsUiState {
    data object Initial : RecurringTransactionsUiState
    data object Loading : RecurringTransactionsUiState
    data class Success(val transactions: List<RecurringTransaction>) : RecurringTransactionsUiState
    data class Error(val message: String) : RecurringTransactionsUiState
}
