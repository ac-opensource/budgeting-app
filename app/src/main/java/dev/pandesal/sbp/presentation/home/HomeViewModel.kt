package dev.pandesal.sbp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.domain.usecase.NetWorthUseCase
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase,
    private val accountUseCase: AccountUseCase,
    private val netWorthUseCase: NetWorthUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = HomeUiState.Loading

        val dummyBudgets = listOf(
            BudgetCategoryUiModel("Groceries", 5000.0, 3200.0),
            BudgetCategoryUiModel("Utilities", 3000.0, 1200.0),
            BudgetCategoryUiModel("Transport", 2000.0, 1800.0),
            BudgetCategoryUiModel("Dining Out", 1500.0, 800.0),
        )

        viewModelScope.launch {
            combine(
                accountUseCase.getAccounts(),
                netWorthUseCase.getCurrentNetWorth()
            ) { accounts, netWorth ->
                accounts.map { it.toUiModel() } to netWorth.map { it.toUiModel() }
            }.collect { (accounts, netWorth) ->
                _uiState.value = HomeUiState.Success(
                    favoriteBudgets = dummyBudgets,
                    accounts = accounts,
                    netWorthData = netWorth
                )
            }
        }
    }


}

private fun dev.pandesal.sbp.domain.model.Account.toUiModel(): AccountSummaryUiModel {
    val isSpendingWallet = when (type) {
        dev.pandesal.sbp.domain.model.AccountType.CASH_WALLET,
        dev.pandesal.sbp.domain.model.AccountType.MOBILE_DIGITAL_WALLET -> true
        else -> false
    }
    val isFundingWallet = type == dev.pandesal.sbp.domain.model.AccountType.BANK_ACCOUNT
    return AccountSummaryUiModel(
        name = name,
        balance = balance.toDouble(),
        isSpendingWallet = isSpendingWallet,
        isFundingWallet = isFundingWallet
    )
}

private fun dev.pandesal.sbp.domain.model.NetWorthRecord.toUiModel(): NetWorthUiModel =
    NetWorthUiModel(label, assets, liabilities)
