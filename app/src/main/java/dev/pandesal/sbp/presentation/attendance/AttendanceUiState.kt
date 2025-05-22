package dev.pandesal.sbp.presentation.attendance

import dev.pandesal.sbp.domain.model.AttendanceRecord

sealed interface AttendanceUiState {
    data object Loading : AttendanceUiState
    data class Ready(val records: List<AttendanceRecord>) : AttendanceUiState
}
