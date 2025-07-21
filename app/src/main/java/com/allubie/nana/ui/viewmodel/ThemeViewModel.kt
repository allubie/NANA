package com.allubie.nana.ui.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allubie.nana.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeViewModel(private val context: Context) : ViewModel() {
    
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_mode")
    }
    
    private val _currentTheme = MutableStateFlow(ThemeMode.LIGHT)
    val currentTheme: StateFlow<ThemeMode> = _currentTheme.asStateFlow()
    
    init {
        loadThemeFromDataStore()
    }
    
    private fun loadThemeFromDataStore() {
        viewModelScope.launch {
            val themeString = context.dataStore.data.map { preferences ->
                preferences[THEME_KEY] ?: ThemeMode.LIGHT.name
            }.first()
            
            _currentTheme.value = try {
                ThemeMode.valueOf(themeString)
            } catch (e: IllegalArgumentException) {
                ThemeMode.LIGHT
            }
        }
    }

    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch {
            _currentTheme.value = theme
            context.dataStore.edit { preferences ->
                preferences[THEME_KEY] = theme.name
            }
        }
    }
}
