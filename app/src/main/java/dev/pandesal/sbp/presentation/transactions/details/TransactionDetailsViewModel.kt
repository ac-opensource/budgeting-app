package dev.pandesal.sbp.presentation.transactions.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(
    private val useCase: TransactionUseCase
) : ViewModel() {

    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction.asStateFlow()

    fun getTransactionById(id: String) {
        viewModelScope.launch {
            useCase.getTransactionById(id).collect {
                _transaction.value = it
            }
        }
    }

    fun setTransaction(txId: String) {
        getTransactionById(txId)
    }

    fun updateTransaction(tx: Transaction) {
        _transaction.value = tx
    }

    fun save(onResult: (Boolean) -> Unit = {}) {
        val tx = _transaction.value ?: return
        viewModelScope.launch {
            runCatching { useCase.insert(tx) }
                .onSuccess { onResult(true) }
                .onFailure { onResult(false) }
        }
    }
}
