package com.allubie.nana.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onThemeChanged: (isDark: Boolean, isAmoled: Boolean) -> Unit
) {
    // Collect theme preferences from the ViewModel
    val themePreferences by viewModel.themePreferences.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Theme Settings",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Follow system theme toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Brightness6, contentDescription = null)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Follow System Theme",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "Automatically switch between light and dark themes",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Switch(
                            checked = themePreferences.followSystemTheme,
                            onCheckedChange = {
                                viewModel.setFollowSystemTheme(it)
                                // If turning on follow system, use system dark mode setting
                                if (it) {
                                    val systemDarkMode = android.os.Build.VERSION.SDK_INT >= 29 &&
                                            android.view.Window.inNightMode()
                                    onThemeChanged(systemDarkMode, themePreferences.isAmoledTheme)
                                }
                            }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Dark theme toggle (disabled if following system)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.DarkMode, contentDescription = null)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Dark Theme",
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = themePreferences.isDarkTheme,
                            onCheckedChange = {
                                viewModel.setDarkTheme(it)
                                onThemeChanged(it, themePreferences.isAmoledTheme)
                            },
                            enabled = !themePreferences.followSystemTheme
                        )
                    }

                    // AMOLED theme toggle (only enabled when dark theme is on)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Contrast, contentDescription = null)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "AMOLED Black Theme",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "True black for OLED screens",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Switch(
                            checked = themePreferences.isAmoledTheme,
                            onCheckedChange = {
                                viewModel.setAmoledTheme(it)
                                onThemeChanged(themePreferences.isDarkTheme, it)
                            },
                            enabled = themePreferences.isDarkTheme ||
                                    (themePreferences.followSystemTheme &&
                                            android.os.Build.VERSION.SDK_INT >= 29 &&
                                            android.view.Window.inNightMode())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "About NANA",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "NANA is a minimal productivity app for students, offering note-taking, routine management, scheduling, and finance tracking.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Version 1.0",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Developed by Allubie",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// Helper extension function for system dark mode detection
fun android.view.Window.Companion.inNightMode(): Boolean {
    return android.content.res.Configuration.UI_MODE_NIGHT_YES ==
            android.app.Application().resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK
}