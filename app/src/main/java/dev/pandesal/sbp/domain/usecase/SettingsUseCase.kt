package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.Settings
import dev.pandesal.sbp.domain.repository.SettingsRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val repository: SettingsRepositoryInterface
) {
    fun getSettings(): Flow<Settings> = repository.getSettings()

    suspend fun setDarkMode(enabled: Boolean) = repository.setDarkMode(enabled)
    suspend fun setNotificationsEnabled(enabled: Boolean) = repository.setNotificationsEnabled(enabled)
    suspend fun setDetectFinanceAppUsage(enabled: Boolean) = repository.setDetectFinanceAppUsage(enabled)
    suspend fun setCurrency(currency: String) = repository.setCurrency(currency)
    suspend fun setTravelMode(enabled: Boolean) = repository.setTravelMode(enabled)
    suspend fun setTravelCurrency(currency: String) = repository.setTravelCurrency(currency)
    suspend fun setTravelTag(tag: String) = repository.setTravelTag(tag)
    suspend fun setExchangeRate(rate: Float, date: String) = repository.setExchangeRate(rate, date)
}
