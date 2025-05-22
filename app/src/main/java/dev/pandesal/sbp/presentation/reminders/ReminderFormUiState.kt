package dev.pandesal.sbp.presentation.reminders

sealed interface ReminderFormUiState {
    data object Initial : ReminderFormUiState
    data object Loading : ReminderFormUiState
    data class Ready(val text: String) : ReminderFormUiState
    data class Error(val message: String) : ReminderFormUiState
}
