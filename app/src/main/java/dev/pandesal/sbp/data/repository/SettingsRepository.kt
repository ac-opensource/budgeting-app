package dev.pandesal.sbp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.pandesal.sbp.domain.model.Settings
import dev.pandesal.sbp.domain.repository.SettingsRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepositoryInterface {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DETECT_FINANCE_APP_USAGE = booleanPreferencesKey("detect_finance_app_usage")
        val CURRENCY = stringPreferencesKey("currency")
        val TRAVEL_MODE = booleanPreferencesKey("travel_mode")
        val TRAVEL_CURRENCY = stringPreferencesKey("travel_currency")
        val TRAVEL_TAG = stringPreferencesKey("travel_tag")
        val EXCHANGE_RATE = stringPreferencesKey("exchange_rate")
        val RATE_DATE = stringPreferencesKey("rate_date")
    }

    override fun getSettings(): Flow<Settings> =
        context.settingsDataStore.data.map { prefs ->
            Settings(
                darkMode = prefs[Keys.DARK_MODE] ?: false,
                notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
                detectFinanceAppUsage = prefs[Keys.DETECT_FINANCE_APP_USAGE] ?: false,
                currency = prefs[Keys.CURRENCY] ?: "PHP",
                isTravelMode = prefs[Keys.TRAVEL_MODE] ?: false,
                travelCurrency = prefs[Keys.TRAVEL_CURRENCY] ?: "",
                travelTag = prefs[Keys.TRAVEL_TAG] ?: "Travel",
                exchangeRate = prefs[Keys.EXCHANGE_RATE]?.toFloatOrNull() ?: 1f,
                lastRateDate = prefs[Keys.RATE_DATE] ?: ""
            )
        }

    override suspend fun setDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    override suspend fun setDetectFinanceAppUsage(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.DETECT_FINANCE_APP_USAGE] = enabled }
    }

    override suspend fun setCurrency(currency: String) {
        context.settingsDataStore.edit { it[Keys.CURRENCY] = currency }
    }

    override suspend fun setTravelMode(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.TRAVEL_MODE] = enabled }
    }

    override suspend fun setTravelCurrency(currency: String) {
        context.settingsDataStore.edit { it[Keys.TRAVEL_CURRENCY] = currency }
    }

    override suspend fun setTravelTag(tag: String) {
        context.settingsDataStore.edit { it[Keys.TRAVEL_TAG] = tag }
    }

    override suspend fun setExchangeRate(rate: Float, date: String) {
        context.settingsDataStore.edit {
            it[Keys.EXCHANGE_RATE] = rate.toString()
            it[Keys.RATE_DATE] = date
        }
    }
}
