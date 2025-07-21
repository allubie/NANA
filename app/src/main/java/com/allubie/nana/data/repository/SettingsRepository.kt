package com.allubie.nana.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class SettingsRepository(private val context: Context) {
    
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_mode")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        private val AUTO_BACKUP_KEY = booleanPreferencesKey("auto_backup_enabled")
        private val CURRENCY_KEY = stringPreferencesKey("currency_format")
        private val TIME_FORMAT_24H_KEY = booleanPreferencesKey("time_format_24h")
        private val MONTHLY_BUDGET_KEY = doublePreferencesKey("monthly_budget")
    }
    
    // Theme settings
    val themeMode: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: "LIGHT"
    }
    
    suspend fun setTheme(theme: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
    
    // Notification settings
    val notificationsEnabled: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED_KEY] ?: false
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }
    
    // Auto backup settings
    val autoBackupEnabled: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[AUTO_BACKUP_KEY] ?: false
    }
    
    suspend fun setAutoBackupEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[AUTO_BACKUP_KEY] = enabled
        }
    }
    
    // Currency settings
    val currencyFormat: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[CURRENCY_KEY] ?: "USD ($)"
    }
    
    suspend fun setCurrencyFormat(currency: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency
        }
    }
    
    // Time format settings
    val is24HourFormat: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[TIME_FORMAT_24H_KEY] ?: true
    }
    
    suspend fun setTimeFormat24H(is24H: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[TIME_FORMAT_24H_KEY] = is24H
        }
    }
    
    // Budget settings
    val monthlyBudget: Flow<Double> = context.settingsDataStore.data.map { preferences ->
        preferences[MONTHLY_BUDGET_KEY] ?: 1000.0
    }
    
    suspend fun setMonthlyBudget(budget: Double) {
        context.settingsDataStore.edit { preferences ->
            preferences[MONTHLY_BUDGET_KEY] = budget
        }
    }
}
