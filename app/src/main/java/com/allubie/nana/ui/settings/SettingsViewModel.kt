package com.allubie.nana.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allubie.nana.data.preferences.ThemePreferences
import com.allubie.nana.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    // Expose theme preferences as StateFlow
    val themePreferences: StateFlow<ThemePreferences> = preferencesManager.themePreferences
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ThemePreferences(
                isDarkTheme = false,
                isAmoledTheme = false,
                followSystemTheme = true
            )
        )

    // Toggle dark theme
    fun setDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateDarkTheme(isDarkTheme)
            // If explicitly setting dark theme, turn off follow system
            if (themePreferences.value.followSystemTheme) {
                preferencesManager.updateFollowSystemTheme(false)
            }
        }
    }

    // Toggle AMOLED theme
    fun setAmoledTheme(isAmoledTheme: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateAmoledTheme(isAmoledTheme)
        }
    }

    // Toggle follow system theme
    fun setFollowSystemTheme(followSystemTheme: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateFollowSystemTheme(followSystemTheme)
        }
    }
}