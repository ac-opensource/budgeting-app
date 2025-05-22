package dev.pandesal.sbp.presentation.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.usecase.AttendanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val useCase: AttendanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            useCase.getRecords().collect { records ->
                _uiState.value = AttendanceUiState.Ready(records)
            }
        }
    }

    fun markPresent(name: String) {
        viewModelScope.launch {
            useCase.markPresent(name)
        }
    }
}
