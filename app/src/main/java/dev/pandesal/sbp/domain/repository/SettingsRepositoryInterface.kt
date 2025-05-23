package dev.pandesal.sbp.domain.repository

import dev.pandesal.sbp.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepositoryInterface {
    fun getSettings(): Flow<Settings>
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setDetectFinanceAppUsage(enabled: Boolean)
    suspend fun setCurrency(currency: String)
    suspend fun setTravelMode(enabled: Boolean)
    suspend fun setTravelCurrency(currency: String)
    suspend fun setTravelTag(tag: String)
    suspend fun setExchangeRate(rate: Float, date: String)
}
