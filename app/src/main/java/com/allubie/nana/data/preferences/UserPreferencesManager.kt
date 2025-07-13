package com.allubie.nana.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Create a DataStore instance at the top level
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore = context.dataStore

    // Keys for preferences
    companion object {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val AMOLED_THEME = booleanPreferencesKey("amoled_theme")
        val FOLLOW_SYSTEM_THEME = booleanPreferencesKey("follow_system_theme")
    }

    // Get theme preferences as a Flow
    val themePreferences: Flow<ThemePreferences> = dataStore.data.map { preferences ->
        ThemePreferences(
            isDarkTheme = preferences[DARK_THEME] ?: false,
            isAmoledTheme = preferences[AMOLED_THEME] ?: false,
            followSystemTheme = preferences[FOLLOW_SYSTEM_THEME] ?: true
        )
    }

    // Update dark theme preference
    suspend fun updateDarkTheme(isDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME] = isDarkTheme
        }
    }

    // Update AMOLED theme preference
    suspend fun updateAmoledTheme(isAmoledTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[AMOLED_THEME] = isAmoledTheme
        }
    }

    // Update follow system theme preference
    suspend fun updateFollowSystemTheme(followSystemTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[FOLLOW_SYSTEM_THEME] = followSystemTheme
        }
    }
}

// Data class to hold theme preferences
data class ThemePreferences(
    val isDarkTheme: Boolean,
    val isAmoledTheme: Boolean,
    val followSystemTheme: Boolean
)