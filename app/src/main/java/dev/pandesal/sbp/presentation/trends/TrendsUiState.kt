package dev.pandesal.sbp.presentation.trends

import dev.pandesal.sbp.presentation.insights.TimePeriod
import dev.pandesal.sbp.presentation.model.TrendUiModel

sealed interface TrendsUiState {
    data object Loading : TrendsUiState
    data class Ready(
        val trends: Map<TimePeriod, List<TrendUiModel>>,
        val forecast: Double
    ) : TrendsUiState
    data class Error(val message: String) : TrendsUiState
}
