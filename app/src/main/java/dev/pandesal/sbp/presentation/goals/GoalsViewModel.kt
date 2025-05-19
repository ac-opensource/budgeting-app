package dev.pandesal.sbp.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Goal
import dev.pandesal.sbp.domain.usecase.GoalUseCase
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.AccountType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val useCase: GoalUseCase,
    private val accountUseCase: AccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GoalsUiState>(GoalsUiState.Loading)
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            useCase.getGoals().collect { goals ->
                _uiState.value = GoalsUiState.Success(goals)
            }
        }
    }

    fun addGoal(
        name: String,
        target: BigDecimal,
        current: BigDecimal = BigDecimal.ZERO,
        dueDate: LocalDate? = null,
        categoryId: Int? = null
    ) {
        viewModelScope.launch {
            useCase.insertGoal(
                Goal(
                    name = name,
                    target = target,
                    current = current,
                    dueDate = dueDate,
                    categoryId = categoryId
                )
            )
            val account = Account(name = "$name Goal Jar", type = AccountType.CASH_WALLET)
            accountUseCase.insertAccount(account)
        }
    }
}
