package dev.pandesal.sbp.presentation.insights

import dev.pandesal.sbp.domain.model.Reminder
import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.domain.model.Transaction

sealed interface DayTooltipUiState {
    data object Loading : DayTooltipUiState
    data class Ready(
        val transactions: List<Transaction>,
        val recurring: List<RecurringTransaction>,
        val reminders: List<Reminder>
    ) : DayTooltipUiState
    data class Error(val message: String) : DayTooltipUiState
}
