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
        val CURRENCY = stringPreferencesKey("currency")
    }

    override fun getSettings(): Flow<Settings> =
        context.settingsDataStore.data.map { prefs ->
            Settings(
                darkMode = prefs[Keys.DARK_MODE] ?: false,
                notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
                currency = prefs[Keys.CURRENCY] ?: "PHP"
            )
        }

    override suspend fun setDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    override suspend fun setCurrency(currency: String) {
        context.settingsDataStore.edit { it[Keys.CURRENCY] = currency }
    }
}
