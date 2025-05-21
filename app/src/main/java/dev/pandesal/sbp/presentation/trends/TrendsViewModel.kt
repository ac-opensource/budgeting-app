package dev.pandesal.sbp.presentation.trends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.usecase.TrendUseCase
import dev.pandesal.sbp.presentation.insights.TimePeriod
import dev.pandesal.sbp.presentation.model.TrendUiModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class TrendsViewModel @Inject constructor(
    private val useCase: TrendUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrendsUiState>(TrendsUiState.Loading)
    val uiState: StateFlow<TrendsUiState> = _uiState.asStateFlow()

    private val _period = MutableStateFlow(TimePeriod.MONTHLY)
    val period: StateFlow<TimePeriod> = _period.asStateFlow()

    init {
        observeData()
    }

    fun setPeriod(period: TimePeriod) {
        _period.value = period
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                useCase.getWeeklySpending(),
                useCase.getMonthlySpending(),
                useCase.getYearlySpending(),
                useCase.forecastNextMonth()
            ) { weekly, monthly, yearly, forecast ->
                val map = mapOf(
                    TimePeriod.WEEKLY to weekly.map { TrendUiModel(it.first, it.second) },
                    TimePeriod.MONTHLY to monthly.map { TrendUiModel(it.first, it.second) },
                    TimePeriod.YEARLY to yearly.map { TrendUiModel(it.first, it.second) }
                )
                TrendsUiState.Ready(map, forecast)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
