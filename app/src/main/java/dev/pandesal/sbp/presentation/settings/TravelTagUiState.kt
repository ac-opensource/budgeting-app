package dev.pandesal.sbp.presentation.settings

sealed interface TravelTagUiState {
    data object Loading : TravelTagUiState
    data class Ready(val tag: String) : TravelTagUiState
}
