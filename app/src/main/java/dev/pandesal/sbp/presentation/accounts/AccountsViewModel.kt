package dev.pandesal.sbp.presentation.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val useCase: AccountUseCase,
    private val categoryUseCase: CategoryUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<AccountsUiState> =
        MutableStateFlow(AccountsUiState.Loading)
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            useCase.getAccounts().collect { accounts ->
                _uiState.value = AccountsUiState.Success(accounts)
            }
        }
    }

    fun addAccount(
        name: String,
        type: AccountType,
        initialBalance: BigDecimal = BigDecimal.ZERO,
        currency: String = "PHP",
        contractValue: String? = null,
        monthlyPayment: String? = null,
    ) {
        viewModelScope.launch {
            val contract = contractValue?.takeIf { it.isNotBlank() }?.let { BigDecimal(it) }
            val monthly = monthlyPayment?.takeIf { it.isNotBlank() }?.let { BigDecimal(it) }
            val start = LocalDate.now().toString()
            val end = if (contract != null && monthly != null && monthly > BigDecimal.ZERO) {
                val months = contract.divide(monthly, 0, java.math.RoundingMode.CEILING).toLong()
                LocalDate.now().plusMonths(months).toString()
            } else null

            val account = Account(
                name = name,
                type = type,
                balance = initialBalance,
                currency = currency,
                contractValue = contract,
                monthlyPayment = monthly,
                startDate = if (contract != null && monthly != null) start else null,
                endDate = end
            )
            useCase.insertAccount(account)

            if (type == AccountType.LOAN) {
                categoryUseCase.insertCategoryGroup(CategoryGroup(id = 20, name = "Liabilities", description = "", icon = ""))
                categoryUseCase.insertCategory(
                    Category(
                        name = name,
                        description = "Loan Payment",
                        icon = "",
                        categoryGroupId = 20,
                        categoryType = TransactionType.OUTFLOW,
                        weight = 0,
                    )
                )
            }
        }
    }

    fun updateAccountName(account: Account, name: String) {
        viewModelScope.launch {
            useCase.insertAccount(account.copy(name = name))
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            useCase.deleteAccount(account)
        }
    }
}
