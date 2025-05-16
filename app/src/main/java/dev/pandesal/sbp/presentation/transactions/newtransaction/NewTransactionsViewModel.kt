package dev.pandesal.sbp.presentation.transactions.newtransaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import dev.pandesal.sbp.presentation.categories.CategoriesUiState
import dev.pandesal.sbp.presentation.transactions.TransactionsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class NewTransactionsViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase,
    private val categoryUseCase: CategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewTransactionUiState>(NewTransactionUiState.Initial)
    val uiState: StateFlow<NewTransactionUiState> = _uiState.asStateFlow()

    private val _transaction = MutableStateFlow(
        Transaction(
            name = "",
            amount = BigDecimal.ZERO,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now(),
            accountId = "", // you can update this later as needed
            transactionType = TransactionType.OUTFLOW
        )
    )

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _uiState.value = NewTransactionUiState.Loading

        viewModelScope.launch {
            combine(
                categoryUseCase.getCategoryGroups(), // List<CategoryGroup>
                categoryUseCase.getCategories() // List<Category>
            ) { groups, categories ->
                NewTransactionUiState.Success(
                    groupedCategories = groups.associateWith { group ->
                        categories.filter { it.categoryGroupId == group.id }
                    },
                    transaction = _transaction.value
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updateTransaction(newTransaction: Transaction) {
        val newTransaction = newTransaction.copy(
            name = if ((_transaction.value.category != newTransaction.category || newTransaction.name.trim().isEmpty()) && newTransaction.category != null) {
                newTransaction.category.name + " " + "Payment"
            } else {
                newTransaction.name
            }
        )

        _transaction.value = newTransaction
        _uiState.value = NewTransactionUiState.Success(
            groupedCategories = (_uiState.value as NewTransactionUiState.Success).groupedCategories,
            transaction = newTransaction
        )
    }

    fun saveTransaction(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                transactionUseCase.insert(_transaction.value)
                onResult(true)
            } catch (e: Exception) {
                _uiState.value = NewTransactionUiState.Error("Save failed: ${e.localizedMessage}")
                onResult(false)
            }
        }
    }
}