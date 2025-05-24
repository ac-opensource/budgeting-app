package dev.pandesal.sbp.presentation.reminders

import dev.pandesal.sbp.domain.model.UpcomingReminder

sealed interface RemindersUiState {
    data object Loading : RemindersUiState
    data class Success(val reminders: List<UpcomingReminder>) : RemindersUiState
    data class Error(val message: String) : RemindersUiState
}
