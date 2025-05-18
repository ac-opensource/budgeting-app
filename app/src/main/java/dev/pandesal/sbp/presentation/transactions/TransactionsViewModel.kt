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

    private var searchJob: Job? = null

    init {
        loadTransactions()
    }

    private fun loadTransactions(query: String = "") {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val flow = if (query.isBlank()) {
                useCase.getPagedTransactions(50, 0)
            } else {
                useCase.searchTransactions(query)
            }
            flow.collect { transactions ->
                _uiState.value = TransactionsUiState.Success(transactions)
            }
        }
    }

    fun search(query: String) {
        loadTransactions(query)
    }

}
