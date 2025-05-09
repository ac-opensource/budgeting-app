package dev.pandesal.sbp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase
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

        val dummyAccounts = listOf(
            AccountSummaryUiModel("GCash", 2200.0, isSpendingWallet = true, isFundingWallet = false),
            AccountSummaryUiModel("BPI Savings", 15000.0, isSpendingWallet = false, isFundingWallet = true),
            AccountSummaryUiModel("Wallet", 500.0, isSpendingWallet = true, isFundingWallet = false),
            AccountSummaryUiModel("UnionBank", 8500.0, isSpendingWallet = true, isFundingWallet = true),
        )

        val dummyNetWorth = listOf(
            NetWorthUiModel("Jan", 40000.0, 10000.0),
            NetWorthUiModel("Feb", 42000.0, 9500.0),
            NetWorthUiModel("Mar", 45000.0, 8700.0),
            NetWorthUiModel("Apr", 47000.0, 8000.0),
        )

        _uiState.value = HomeUiState.Success(
            favoriteBudgets = dummyBudgets,
            accounts = dummyAccounts,
            netWorthData = dummyNetWorth
        )
    }


}
