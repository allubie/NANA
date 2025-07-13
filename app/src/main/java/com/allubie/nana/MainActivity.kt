package com.allubie.nana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allubie.nana.ui.NANAApp
import com.allubie.nana.ui.settings.SettingsViewModel
import com.allubie.nana.ui.theme.NANATheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Collect theme preferences
            val themePreferences by settingsViewModel.themePreferences.collectAsStateWithLifecycle()

            // Determine theme based on preferences
            val systemDarkTheme = isSystemInDarkTheme()
            val darkTheme = if (themePreferences.followSystemTheme) systemDarkTheme else themePreferences.isDarkTheme
            val amoledTheme = themePreferences.isAmoledTheme

            NANATheme(
                darkTheme = darkTheme,
                amoledTheme = amoledTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NANAApp(
                        isDarkTheme = darkTheme,
                        isAmoledTheme = amoledTheme,
                        onThemeChanged = { isDark, isAmoled ->
                            // Update theme preferences when changed from the UI
                            if (!themePreferences.followSystemTheme) {
                                settingsViewModel.setDarkTheme(isDark)
                            }
                            settingsViewModel.setAmoledTheme(isAmoled)
                        }
                    )
                }
            }
        }
    }
}