package dev.pandesal.sbp.presentation.transactions.newtransaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import dev.pandesal.sbp.domain.model.RecurringInterval
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
    private val categoryUseCase: CategoryUseCase,
    private val accountUseCase: AccountUseCase,
    private val recurringTransactionUseCase: RecurringTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewTransactionUiState>(NewTransactionUiState.Initial)
    val uiState: StateFlow<NewTransactionUiState> = _uiState.asStateFlow()

    private val _merchants = MutableStateFlow<List<String>>(emptyList())
    private var merchantJob: kotlinx.coroutines.Job? = null


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
        subscribeUiState()
    }

    private fun subscribeUiState() {
        _uiState.value = NewTransactionUiState.Loading

        viewModelScope.launch {
            combine(
                categoryUseCase.getCategoryGroups(),
                categoryUseCase.getCategories(),
                accountUseCase.getAccounts(),
                _transaction,
                _merchants
            ) { groups, categories, accounts, transaction, merchants ->
                NewTransactionUiState.Success(
                    groupedCategories = groups.associateWith { group ->
                        categories.filter { it.categoryGroupId == group.id }
                    },
                    accounts = accounts,
                    transaction = transaction,
                    merchants = merchants
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun loadMerchants(categoryId: String) {
        merchantJob?.cancel()
        merchantJob = viewModelScope.launch {
            transactionUseCase.getMerchantsByCategoryId(categoryId)
                .collect { merchants ->
                    _merchants.value = merchants
                    val current = _uiState.value
                    if (current is NewTransactionUiState.Success) {
                        _uiState.value = current.copy(merchants = merchants)
                    }
                }
        }
    }

    fun updateTransaction(newTransaction: Transaction) {
        var newTransaction = newTransaction.copy(
            name = if ((_transaction.value.category != newTransaction.category || newTransaction.name.trim().isEmpty()) && newTransaction.category != null) {
                newTransaction.category.name + " " + "Payment"
            } else {
                newTransaction.name
            }
        )

        if (_transaction.value.transactionType != newTransaction.transactionType &&
            newTransaction.transactionType == TransactionType.INFLOW
        ) {
            val current = _uiState.value
            if (current is NewTransactionUiState.Success) {
                val salary = current.groupedCategories.values.flatten()
                    .firstOrNull { it.name.equals("Salary", ignoreCase = true) }
                if (salary != null) {
                    newTransaction = newTransaction.copy(category = salary)
                }
            }
        } else if (_transaction.value.transactionType != newTransaction.transactionType &&
            newTransaction.transactionType == TransactionType.TRANSFER
        ) {
            val current = _uiState.value
            if (current is NewTransactionUiState.Success) {
                val adjustment = current.groupedCategories.values.flatten()
                    .firstOrNull { it.name.equals("Adjustment", ignoreCase = true) }
                if (adjustment != null) {
                    newTransaction = newTransaction.copy(category = adjustment)
                }
            }
        }

        if (_transaction.value.category?.id != newTransaction.category?.id && newTransaction.category != null) {
            loadMerchants(newTransaction.category.id.toString())
        }

        _transaction.value = newTransaction
        _uiState.value = NewTransactionUiState.Success(
            groupedCategories = (_uiState.value as NewTransactionUiState.Success).groupedCategories,
            accounts = (_uiState.value as NewTransactionUiState.Success).accounts,
            transaction = newTransaction,
            merchants = _merchants.value
        )
    }

    fun saveTransaction(
        isRecurring: Boolean,
        interval: RecurringInterval,
        cutoffDays: Int,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (_transaction.value.amount <= BigDecimal.ZERO) {
                    _uiState.value = NewTransactionUiState.Error("Amount is required")
                    onResult(false)
                    return@launch
                }
                if (_transaction.value.accountId.isBlank()) {
                    _uiState.value = NewTransactionUiState.Error("Account is required")
                    onResult(false)
                    return@launch
                }
                if (_transaction.value.category == null) {
                    _uiState.value = NewTransactionUiState.Error("Category is required")
                    onResult(false)
                    return@launch
                }

                transactionUseCase.insert(_transaction.value)
                if (isRecurring) {
                    val recurring = dev.pandesal.sbp.domain.model.RecurringTransaction(
                        transaction = _transaction.value,
                        interval = interval,
                        cutoffDays = cutoffDays
                    )
                    recurringTransactionUseCase.addRecurringTransaction(recurring)
                }
                onResult(true)
            } catch (e: Exception) {
                _uiState.value = NewTransactionUiState.Error("Save failed: ${e.localizedMessage}")
                onResult(false)
            }
        }
    }

    fun loadTransaction(id: String) {
        viewModelScope.launch {
            transactionUseCase.getTransactionById(id).collect { tx ->
                _transaction.value = tx
            }
        }
    }

    fun deleteTransaction(transaction: Transaction, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            runCatching { transactionUseCase.delete(transaction) }
                .onSuccess { onResult(true) }
                .onFailure {
                    _uiState.value = NewTransactionUiState.Error("Delete failed: ${it.localizedMessage}")
                    onResult(false)
                }
        }
    }
}