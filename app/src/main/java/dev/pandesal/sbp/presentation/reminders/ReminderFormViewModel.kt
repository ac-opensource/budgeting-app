package dev.pandesal.sbp.presentation.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Reminder
import dev.pandesal.sbp.domain.usecase.ReminderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReminderFormViewModel @Inject constructor(
    private val useCase: ReminderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReminderFormUiState>(ReminderFormUiState.Initial)
    val uiState: StateFlow<ReminderFormUiState> = _uiState.asStateFlow()

    fun load(reminder: Reminder?) {
        _uiState.value = ReminderFormUiState.Ready(reminder?.message ?: "")
    }

    fun save(
        date: LocalDate,
        text: String,
        id: String? = null,
        shouldNotify: Boolean = true,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val reminder = Reminder(
                id = id ?: java.util.UUID.randomUUID().toString(),
                date = date,
                message = text,
                shouldNotify = shouldNotify
            )
            useCase.upsertReminder(reminder)
            onDone()
        }
    }
}
