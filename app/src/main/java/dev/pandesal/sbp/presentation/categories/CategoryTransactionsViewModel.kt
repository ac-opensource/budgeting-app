package dev.pandesal.sbp.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import dev.pandesal.sbp.presentation.transactions.TransactionsUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CategoryTransactionsViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionsUiState>(TransactionsUiState.Initial)
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    fun load(categoryId: String) {
        viewModelScope.launch {
            transactionUseCase.getTransactionsByCategoryId(categoryId).collect {
                _uiState.value = TransactionsUiState.Success(it)
            }
        }
    }
}
