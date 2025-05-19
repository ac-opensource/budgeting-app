package dev.pandesal.sbp.presentation.transactions.recurringdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecurringTransactionDetailsViewModel @Inject constructor(
    private val useCase: RecurringTransactionUseCase
) : ViewModel() {

    private val _transaction = MutableStateFlow<RecurringTransaction?>(null)
    val transaction: StateFlow<RecurringTransaction?> = _transaction.asStateFlow()

    fun loadTransaction(id: String) {
        viewModelScope.launch {
            useCase.getRecurringTransactionById(id).collect {
                _transaction.value = it
            }
        }
    }

    fun delete(onResult: (Boolean) -> Unit = {}) {
        val tx = _transaction.value ?: return
        viewModelScope.launch {
            runCatching { useCase.removeRecurringTransaction(tx) }
                .onSuccess { onResult(true) }
                .onFailure { onResult(false) }
        }
    }

    fun update(transaction: RecurringTransaction) {
        _transaction.value = transaction
    }

    fun save(onResult: (Boolean) -> Unit = {}) {
        val tx = _transaction.value ?: return
        viewModelScope.launch {
            runCatching { useCase.addRecurringTransaction(tx) }
                .onSuccess { onResult(true) }
                .onFailure { onResult(false) }
        }
    }
}
