package com.allubie.nana.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.allubie.nana.R
import com.allubie.nana.ui.finance.FinanceScreen
import com.allubie.nana.ui.notes.NotesScreen
import com.allubie.nana.ui.routines.RoutinesScreen
import com.allubie.nana.ui.schedule.ScheduleScreen
import com.allubie.nana.ui.settings.SettingsScreen

sealed class Screen(val route: String, val resourceId: Int, val icon: @Composable () -> Unit) {
    object Notes : Screen("notes", R.string.notes, { Icon(Icons.Outlined.EditNote, contentDescription = null) })
    object Routines : Screen("routines", R.string.routines, { Icon(Icons.Outlined.Schedule, contentDescription = null) })
    object Schedule : Screen("schedule", R.string.schedule, { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) })
    object Finance : Screen("finance", R.string.finance, { Icon(Icons.Outlined.AttachMoney, contentDescription = null) })
    object Settings : Screen("settings", R.string.settings, { Icon(Icons.Outlined.Settings, contentDescription = null) })
}

@Composable
fun NANAApp(
    isDarkTheme: Boolean,
    isAmoledTheme: Boolean,
    onThemeChanged: (isDark: Boolean, isAmoled: Boolean) -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Notes,
        Screen.Routines,
        Screen.Schedule,
        Screen.Finance,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { screen.icon() },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Notes.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Notes.route) {
                NotesScreen()
            }
            composable(Screen.Routines.route) {
                RoutinesScreen()
            }
            composable(Screen.Schedule.route) {
                ScheduleScreen()
            }
            composable(Screen.Finance.route) {
                FinanceScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onThemeChanged = onThemeChanged
                )
            }
        }
    }
}