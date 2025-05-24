package dev.pandesal.sbp.presentation.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.UpcomingReminder
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import dev.pandesal.sbp.domain.usecase.ReminderUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val recurringUseCase: RecurringTransactionUseCase,
    private val reminderUseCase: ReminderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RemindersUiState>(RemindersUiState.Loading)
    val uiState: StateFlow<RemindersUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            recurringUseCase.getUpcomingReminders(LocalDate.now()).collect { list ->
                _uiState.value = RemindersUiState.Success(list)
            }
        }
    }

    fun markDone(item: UpcomingReminder) {
        if (!item.isRecurring) {
            viewModelScope.launch {
                reminderUseCase.deleteReminder(
                    dev.pandesal.sbp.domain.model.Reminder(
                        id = item.id,
                        date = item.dueDate,
                        message = item.title,
                        shouldNotify = false
                    )
                )
            }
        }
    }
}
