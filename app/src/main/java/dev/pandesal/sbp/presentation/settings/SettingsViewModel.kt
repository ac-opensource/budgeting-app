package dev.pandesal.sbp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Settings
import dev.pandesal.sbp.domain.usecase.SettingsUseCase
import dev.pandesal.sbp.notification.SmsTransactionScanner
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val useCase: SettingsUseCase,
    private val smsScanner: SmsTransactionScanner
) : ViewModel() {

    val settings: StateFlow<Settings> = useCase.getSettings()
        .stateIn(viewModelScope, SharingStarted.Lazily, Settings())

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { useCase.setDarkMode(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { useCase.setNotificationsEnabled(enabled) }
    }

    fun setCurrency(currency: String) {
        viewModelScope.launch { useCase.setCurrency(currency) }
    }

    fun scanSms() {
        viewModelScope.launch { smsScanner.scan() }
    }
}
