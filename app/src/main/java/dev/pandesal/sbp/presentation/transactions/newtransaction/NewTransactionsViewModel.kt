package dev.pandesal.sbp.presentation.transactions.newtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.domain.usecase.ReceiptUseCase
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NewTransactionsViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase,
    private val categoryUseCase: CategoryUseCase,
    private val accountUseCase: AccountUseCase,
    private val recurringTransactionUseCase: RecurringTransactionUseCase,
    private val receiptUseCase: ReceiptUseCase,
    private val travelModeUseCase: dev.pandesal.sbp.domain.usecase.TravelModeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewTransactionUiState>(NewTransactionUiState.Initial)
    val uiState: StateFlow<NewTransactionUiState> = _uiState.asStateFlow()

    private val _merchants = MutableStateFlow<List<String>>(emptyList())
    private var merchantJob: kotlinx.coroutines.Job? = null

    private val _validationErrors: MutableStateFlow<NewTransactionUiState.ValidationErrors> =
        MutableStateFlow(NewTransactionUiState.ValidationErrors())

    private val _transaction = MutableStateFlow(
        Transaction(
            name = "",
            amount = BigDecimal.ZERO,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now(),
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
                _merchants,
            ) { groups, categories, accounts, transaction, merchants ->
                NewTransactionUiState.Success(
                    groupedCategories = groups.associateWith { group ->
                        categories.filter { it.categoryGroupId == group.id }
                    }, accounts = accounts, transaction = transaction, merchants = merchants
                )
            }.zip(
                    _validationErrors
                ) { uiState, validationErrors ->
                    uiState.copy(errors = validationErrors)
                }.catch { e ->
                    _uiState.value =
                        NewTransactionUiState.Error(e.localizedMessage ?: "Unknown error")
                }.collect { state ->
                    _uiState.value = state
                }
        }
    }

    private fun loadMerchants(categoryId: String) {
        merchantJob?.cancel()
        merchantJob = viewModelScope.launch {
            transactionUseCase.getMerchantsByCategoryId(categoryId).collect { merchants ->
                    _merchants.value = merchants
                    val current = _uiState.value
                    if (current is NewTransactionUiState.Success) {
                        _uiState.value = current.copy(
                            merchants = merchants,
                            errors = _validationErrors.value,
                        )
                    }
                }
        }
    }

    fun attachReceipt(uri: android.net.Uri, context: android.content.Context) {
        viewModelScope.launch {
            val image = InputImage.fromFilePath(context, uri)
            val data = receiptUseCase.parse(image)
            val updated = _transaction.value.copy(
                merchantName = data.merchantName ?: _transaction.value.merchantName,
                amount = data.amount ?: _transaction.value.amount,
                createdAt = data.date ?: _transaction.value.createdAt,
                attachment = uri.toString()
            )
            updateTransaction(updated)
        }
    }

    fun updateTransaction(newTransaction: Transaction) {
        var newTransaction = newTransaction.copy(
            name = if ((_transaction.value.category != newTransaction.category || newTransaction.name.trim()
                    .isEmpty()) && newTransaction.category != null
            ) {
                newTransaction.category.name
            } else {
                newTransaction.name
            }
        )

        if (_transaction.value.transactionType != newTransaction.transactionType && newTransaction.transactionType == TransactionType.INFLOW) {
            val current = _uiState.value
            if (current is NewTransactionUiState.Success) {
                val salary = current.groupedCategories.values.flatten()
                    .firstOrNull { it.name.equals("Salary", ignoreCase = true) }
                if (salary != null) {
                    newTransaction = newTransaction.copy(category = salary)
                }
            }
        } else if (_transaction.value.transactionType != newTransaction.transactionType && newTransaction.transactionType == TransactionType.TRANSFER) {
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

        _validationErrors.value = _validationErrors.value.copy(
            amount = if (newTransaction.amount > BigDecimal.ZERO) false else _validationErrors.value.amount,
            category = if (newTransaction.category != null) false else _validationErrors.value.category,
            from = if (newTransaction.transactionType == TransactionType.INFLOW) false else { if (newTransaction.from != null) false else _validationErrors.value.from },
            to = if (newTransaction.transactionType == TransactionType.OUTFLOW) {
                if (newTransaction.merchantName != null) false else _validationErrors.value.to
            } else {
                if (newTransaction.to != null) false else _validationErrors.value.to
            },
        )

        _uiState.value = NewTransactionUiState.Success(
            groupedCategories = (_uiState.value as? NewTransactionUiState.Success)?.groupedCategories
                ?: mapOf(),
            accounts = (_uiState.value as? NewTransactionUiState.Success)?.accounts ?: listOf(),
            transaction = newTransaction,
            merchants = _merchants.value,
            errors = _validationErrors.value,
        )
    }

    fun saveTransaction(
        isRecurring: Boolean,
        interval: RecurringInterval,
        cutoffDays: Int,
        reminderEnabled: Boolean,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val amountMissing = _transaction.value.amount <= BigDecimal.ZERO
                val categoryMissing = _transaction.value.category == null
                val fromMissing = if (_transaction.value.transactionType == TransactionType.INFLOW) false else _transaction.value.from == null
                val toMissing = if (_transaction.value.transactionType == TransactionType.OUTFLOW) {
                    _transaction.value.merchantName == null
                } else {
                    _transaction.value.to == null
                }

                _validationErrors.value = NewTransactionUiState.ValidationErrors(
                    amount = amountMissing,
                    category = categoryMissing,
                    from = fromMissing,
                    to = toMissing
                )

                if (amountMissing || categoryMissing || fromMissing || toMissing) {
                    val current = _uiState.value as? NewTransactionUiState.Success
                    if (current != null) {
                        _uiState.value = current.copy(errors = _validationErrors.value)
                    }
                    onResult(false)
                    return@launch
                }

                val settings = travelModeUseCase.getSettings().first()
                var tx = _transaction.value
                if (settings.isTravelMode) {
                    tx = tx.copy(
                        currency = settings.travelCurrency,
                        tags = (tx.tags + settings.travelTag).distinct()
                    )
                }
                transactionUseCase.insert(tx)
                if (isRecurring) {
                    val recurring = dev.pandesal.sbp.domain.model.RecurringTransaction(
                        transaction = tx,
                        interval = interval,
                        cutoffDays = cutoffDays,
                        reminderEnabled = reminderEnabled
                    )
                    recurringTransactionUseCase.addRecurringTransaction(recurring)
                }
                _validationErrors.value = NewTransactionUiState.ValidationErrors()
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
                if (tx != null) {
                    _transaction.value = tx
                }
            }
        }
    }

    fun deleteTransaction(transaction: Transaction, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            runCatching { transactionUseCase.delete(transaction) }.onSuccess { onResult(true) }
                .onFailure {
                    _uiState.value =
                        NewTransactionUiState.Error("Delete failed: ${it.localizedMessage}")
                    onResult(false)
                }
        }
    }
}