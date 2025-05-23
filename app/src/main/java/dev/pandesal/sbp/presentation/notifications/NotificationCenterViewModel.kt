package dev.pandesal.sbp.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Notification
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import dev.pandesal.sbp.domain.usecase.SettingsUseCase
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.service.GeminiService
import dev.pandesal.sbp.notification.InAppNotificationCenter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationCenterViewModel @Inject constructor(
    private val recurringUseCase: RecurringTransactionUseCase,
    private val geminiService: GeminiService,
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {

    val notifications: StateFlow<List<Notification>> = combine(
        InAppNotificationCenter.notifications,
        recurringUseCase.getUpcomingNotifications()
    ) { posted, upcoming ->
        (posted + upcoming).distinctBy { it.message }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val notificationsEnabled: StateFlow<Boolean> =
        settingsUseCase.getSettings()
            .map { it.notificationsEnabled }
            .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsUseCase.setNotificationsEnabled(enabled) }
    }

    fun refresh() {
    }

    suspend fun parseTransaction(text: String): Transaction = geminiService.parseSms(text)
}
