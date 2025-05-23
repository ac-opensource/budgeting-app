package dev.pandesal.sbp.presentation.home

import dev.pandesal.sbp.domain.model.Transaction
import java.math.BigDecimal

sealed interface DayPopupUiState {
    data object Loading : DayPopupUiState
    data class Ready(
        val inflow: BigDecimal,
        val outflow: BigDecimal,
        val budgetChange: BigDecimal,
        val transactions: List<Transaction>
    ) : DayPopupUiState
    data class Error(val message: String) : DayPopupUiState
}
