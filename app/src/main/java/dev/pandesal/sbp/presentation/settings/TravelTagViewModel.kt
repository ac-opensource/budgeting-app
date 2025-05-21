package dev.pandesal.sbp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.usecase.TravelModeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TravelTagViewModel @Inject constructor(
    private val travelUseCase: TravelModeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TravelTagUiState>(TravelTagUiState.Loading)
    val uiState: StateFlow<TravelTagUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            travelUseCase.getSettings()
                .map { it.travelTag }
                .collect { tag ->
                    _uiState.value = TravelTagUiState.Ready(tag)
                }
        }
    }

    fun updateTag(tag: String) {
        val current = _uiState.value
        if (current is TravelTagUiState.Ready) {
            _uiState.value = current.copy(tag = tag)
        }
    }

    fun save() {
        val current = _uiState.value as? TravelTagUiState.Ready ?: return
        viewModelScope.launch { travelUseCase.setTravelTag(current.tag) }
    }
}
