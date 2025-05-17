package dev.pandesal.sbp.presentation.accounts

import dev.pandesal.sbp.domain.model.Account

sealed interface AccountsUiState {
    data object Loading : AccountsUiState
    data class Success(val accounts: List<Account>) : AccountsUiState
    data class Error(val message: String) : AccountsUiState
}
