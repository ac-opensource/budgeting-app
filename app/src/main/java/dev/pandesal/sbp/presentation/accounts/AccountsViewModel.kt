package dev.pandesal.sbp.presentation.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val useCase: AccountUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<AccountsUiState> =
        MutableStateFlow(AccountsUiState.Loading)
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            useCase.getAccounts().collect { accounts ->
                _uiState.value = AccountsUiState.Success(accounts)
            }
        }
    }

    fun addAccount(name: String, type: AccountType, currency: String = "PHP") {
        viewModelScope.launch {
            val account = Account(name = name, type = type, currency = currency)
            useCase.insertAccount(account)
        }
    }
}
