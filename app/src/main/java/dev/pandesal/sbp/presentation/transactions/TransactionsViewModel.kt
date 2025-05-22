package dev.pandesal.sbp.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val useCase: TransactionUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<TransactionsUiState> = MutableStateFlow(TransactionsUiState.Initial)
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = TransactionsUiState.Loading
            useCase.getPagedTransactions(50, 0)
                .collect { transactions ->
                    _uiState.value = TransactionsUiState.Success(transactions)
                }
        }
    }

}
