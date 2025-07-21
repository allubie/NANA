package com.allubie.nana.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.allubie.nana.ui.screens.*
import com.allubie.nana.ui.viewmodel.ExpenseViewModel
import com.allubie.nana.ui.viewmodel.ExpenseViewModelFactory
import com.allubie.nana.ui.viewmodel.SettingsViewModel
import com.allubie.nana.ui.viewmodel.SettingsViewModelFactory
import com.allubie.nana.data.repository.ExpenseRepository
import com.allubie.nana.data.repository.SettingsRepository
import com.allubie.nana.data.database.NANADatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NANANavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (shouldShowBottomNav(currentDestination?.route)) {
                NavigationBar(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    bottomNavDestinations.forEach { destination ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    destination.icon, 
                                    contentDescription = destination.title
                                ) 
                            },
                            label = { 
                                Text(
                                    text = destination.title,
                                    style = MaterialTheme.typography.labelSmall
                                ) 
                            },
                            selected = isSelected,
                            alwaysShowLabel = false,
                            onClick = {
                                navController.navigate(destination.route) {
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
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NANADestination.Notes.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NANADestination.Notes.route) {
                NotesScreen(
                    onNavigateToNote = { noteId ->
                        navController.navigate(NANADestination.NoteDetail.createRoute(noteId))
                    },
                    onNavigateToSettings = {
                        navController.navigate(NANADestination.Settings.route)
                    },
                    onNavigateToArchive = {
                        navController.navigate(NANADestination.Archive.route)
                    },
                    onNavigateToRecycleBin = {
                        navController.navigate(NANADestination.RecycleBin.route)
                    }
                )
            }
            
            composable(NANADestination.Routines.route) {
                RoutinesScreen(
                    onNavigateToRoutine = { routineId ->
                        navController.navigate(NANADestination.RoutineDetail.createRoute(routineId))
                    },
                    onNavigateToStats = {
                        navController.navigate("routine_stats")
                    },
                    onNavigateToSettings = {
                        navController.navigate(NANADestination.Settings.route)
                    }
                )
            }
            
            composable(NANADestination.Schedules.route) {
                SchedulesScreen(
                    onNavigateToSchedule = { scheduleId ->
                        navController.navigate(NANADestination.ScheduleDetail.createRoute(scheduleId))
                    },
                    onNavigateToSettings = {
                        navController.navigate(NANADestination.Settings.route)
                    }
                )
            }
            
            composable(NANADestination.Expenses.route) {
                ExpensesScreen(
                    onNavigateToExpense = { expenseId ->
                        navController.navigate(NANADestination.ExpenseDetail.createRoute(expenseId))
                    }
                )
            }
            
            composable(NANADestination.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAbout = {
                        navController.navigate(NANADestination.About.route)
                    }
                )
            }
            
            composable(NANADestination.About.route) {
                AboutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(NANADestination.Archive.route) {
                ArchiveScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNote = { noteId ->
                        navController.navigate(NANADestination.NoteDetail.createRoute(noteId))
                    }
                )
            }
            
            composable(NANADestination.RecycleBin.route) {
                RecycleBinScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(NANADestination.NoteDetail.route) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId")?.toLongOrNull() ?: 0L
                NoteDetailScreen(
                    noteId = noteId,
                    onNavigateBack = { navController.popBackStack() },
                    isEditing = noteId == 0L // New notes start in edit mode, existing notes start in view mode
                )
            }
            
            composable(NANADestination.RoutineDetail.route) { backStackEntry ->
                val routineId = backStackEntry.arguments?.getString("routineId")?.toLongOrNull() ?: 0L
                RoutineDetailScreen(
                    routineId = routineId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(NANADestination.ScheduleDetail.route) { backStackEntry ->
                val scheduleId = backStackEntry.arguments?.getString("scheduleId")?.toLongOrNull() ?: 0L
                ScheduleDetailScreen(
                    scheduleId = scheduleId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable("routine_stats") {
                RoutineStatsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(NANADestination.ExpenseDetail.route) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getString("expenseId")?.toLongOrNull() ?: 0L
                val context = LocalContext.current
                val database = NANADatabase.getDatabase(context)
                val repository = ExpenseRepository(database.expenseDao())
                val expenseViewModel: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory(repository))
                val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
                
                // Placeholder for expense detail - using AddExpenseScreenNew2 for editing
                AddExpenseScreenNew2(
                    expenseViewModel = expenseViewModel,
                    settingsViewModel = settingsViewModel,
                    expenseId = expenseId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

private fun shouldShowBottomNav(route: String?): Boolean {
    return route in bottomNavDestinations.map { it.route }
}
