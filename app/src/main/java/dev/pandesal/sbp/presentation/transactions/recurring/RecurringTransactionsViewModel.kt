package dev.pandesal.sbp.presentation.transactions.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RecurringTransactionsViewModel @Inject constructor(
    private val useCase: RecurringTransactionUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<RecurringTransactionsUiState>(RecurringTransactionsUiState.Initial)
    val uiState: StateFlow<RecurringTransactionsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            useCase.getRecurringTransactions().collect { list ->
                _uiState.value = RecurringTransactionsUiState.Success(list)
            }
        }
    }

    fun remove(transaction: RecurringTransaction) {
        viewModelScope.launch { useCase.removeRecurringTransaction(transaction) }
    }

    fun nextDueDate(transaction: RecurringTransaction, now: LocalDate): LocalDate? {
        return useCase.nextDueDate(transaction, now)
    }
}
