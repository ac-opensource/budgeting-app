package dev.pandesal.sbp.presentation.goals

import dev.pandesal.sbp.domain.model.Goal

sealed interface GoalsUiState {
    data object Loading : GoalsUiState
    data class Success(val goals: List<Goal>) : GoalsUiState
    data class Error(val message: String) : GoalsUiState
}
