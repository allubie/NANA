package com.allubie.nana.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NANADestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Notes : NANADestination("notes", "Notes", Icons.Default.Edit)
    object Routines : NANADestination("routines", "Routines", Icons.Default.AddTask)
    object Schedules : NANADestination("schedules", "Schedules", Icons.Default.DateRange)
    object Expenses : NANADestination("expenses", "Expenses", Icons.Default.MoneyOff)
    object Settings : NANADestination("settings", "Settings", Icons.Default.Settings)
    object About : NANADestination("about", "About", Icons.Default.Info)
    object Archive : NANADestination("archive", "Archive", Icons.Default.Archive)
    object RecycleBin : NANADestination("recycle_bin", "Recycle Bin", Icons.Default.Delete)
    
    // Detail destinations
    object NoteDetail : NANADestination("note_detail/{noteId}", "Note Detail", Icons.Default.Edit) {
        fun createRoute(noteId: Long = 0) = "note_detail/$noteId"
    }
    object RoutineDetail : NANADestination("routine_detail/{routineId}", "Routine Detail", Icons.Default.Refresh) {
        fun createRoute(routineId: Long = 0) = "routine_detail/$routineId"
    }
    object ScheduleDetail : NANADestination("schedule_detail/{scheduleId}", "Schedule Detail", Icons.Default.DateRange) {
        fun createRoute(scheduleId: Long = 0) = "schedule_detail/$scheduleId"
    }
    object ExpenseDetail : NANADestination("expense_detail/{expenseId}", "Expense Detail", Icons.Default.ShoppingCart) {
        fun createRoute(expenseId: Long = 0) = "expense_detail/$expenseId"
    }
}

val bottomNavDestinations = listOf(
    NANADestination.Notes,
    NANADestination.Routines,
    NANADestination.Schedules,
    NANADestination.Expenses
)
