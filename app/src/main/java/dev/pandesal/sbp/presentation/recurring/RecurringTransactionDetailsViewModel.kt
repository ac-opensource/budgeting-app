package dev.pandesal.sbp.presentation.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecurringTransactionDetailsViewModel @Inject constructor(
    private val useCase: RecurringTransactionUseCase
) : ViewModel() {

    private val _transaction = MutableStateFlow<RecurringTransaction?>(null)
    val transaction: StateFlow<RecurringTransaction?> = _transaction.asStateFlow()

    fun setTransaction(id: String) {
        viewModelScope.launch {
            val list = useCase.getRecurringTransactions().first()
            _transaction.value = list.firstOrNull { it.transaction.id == id }
        }
    }

    fun save() {
        val tx = _transaction.value ?: return
        viewModelScope.launch { useCase.addRecurringTransaction(tx) }
    }
}
