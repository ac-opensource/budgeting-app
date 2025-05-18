package dev.pandesal.sbp.fakes

import dev.pandesal.sbp.domain.model.Settings
import dev.pandesal.sbp.domain.repository.SettingsRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository : SettingsRepositoryInterface {
    val settingsFlow = MutableStateFlow(Settings())
    var darkModeSet: Boolean? = null
    var notificationsSet: Boolean? = null
    var currencySet: String? = null

    override fun getSettings(): Flow<Settings> = settingsFlow
    override suspend fun setDarkMode(enabled: Boolean) { darkModeSet = enabled }
    override suspend fun setNotificationsEnabled(enabled: Boolean) { notificationsSet = enabled }
    override suspend fun setCurrency(currency: String) { currencySet = currency }
}
