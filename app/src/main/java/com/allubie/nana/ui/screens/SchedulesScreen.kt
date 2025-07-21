package com.allubie.nana.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.allubie.nana.data.entity.Schedule
import com.allubie.nana.data.database.NANADatabase
import com.allubie.nana.data.repository.ScheduleRepository
import com.allubie.nana.ui.viewmodel.ScheduleViewModel
import com.allubie.nana.ui.viewmodel.ScheduleViewModelFactory
import com.allubie.nana.ui.viewmodel.SettingsViewModel
import com.allubie.nana.ui.viewmodel.SettingsViewModelFactory
import com.allubie.nana.ui.theme.NANATheme
import kotlinx.datetime.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulesScreen(
    onNavigateToSchedule: (Long) -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val database = NANADatabase.getDatabase(context)
    val repository = ScheduleRepository(database.scheduleDao(), context)
    val viewModel: ScheduleViewModel = viewModel(factory = ScheduleViewModelFactory(repository))
    
    // Settings for time format
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val is24HourFormat by settingsViewModel.is24HourFormat.collectAsState()
    
    val schedules by viewModel.schedules.collectAsState()
    var showCalendarView by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All Events") }
    
    // Filter schedules based on selected filter
    val filteredSchedules = when (selectedFilter) {
        "Today" -> {
            val today = LocalDate.now().toString()
            schedules.filter { 
                try {
                    // Extract date part from startDateTime (YYYY-MM-DD from YYYY-MM-DDTHH:MM:SS)
                    val scheduleDate = it.startDateTime.split("T")[0]
                    scheduleDate == today
                } catch (e: Exception) {
                    false
                }
            }
        }
        "This Week" -> {
            val today = LocalDate.now()
            val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
            val endOfWeek = startOfWeek.plusDays(6)
            schedules.filter { schedule ->
                try {
                    // Extract date part from startDateTime
                    val scheduleDateStr = schedule.startDateTime.split("T")[0]
                    val scheduleDate = LocalDate.parse(scheduleDateStr)
                    scheduleDate.isAfter(startOfWeek.minusDays(1)) && scheduleDate.isBefore(endOfWeek.plusDays(1))
                } catch (e: Exception) {
                    false
                }
            }
        }
        "Pinned" -> schedules.filter { it.isPinned }
        "Completed" -> schedules.filter { it.isCompleted }
        else -> schedules
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedules") },
                actions = {
                    IconButton(onClick = { showCalendarView = !showCalendarView }) {
                        Icon(
                            if (showCalendarView) Icons.AutoMirrored.Filled.List else Icons.Default.DateRange, 
                            contentDescription = if (showCalendarView) "List view" else "Calendar view"
                        )
                    }
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Filter")
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = { 
                                    onNavigateToSettings()
                                    showFilterMenu = false 
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("All Events") },
                                onClick = { 
                                    selectedFilter = "All Events"
                                    showFilterMenu = false 
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Today") },
                                onClick = { 
                                    selectedFilter = "Today"
                                    showFilterMenu = false 
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("This Week") },
                                onClick = { 
                                    selectedFilter = "This Week"
                                    showFilterMenu = false 
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Pinned") },
                                onClick = { 
                                    selectedFilter = "Pinned"
                                    showFilterMenu = false 
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Completed Scheduleds") },
                                onClick = { 
                                    selectedFilter = "Completed"
                                    showFilterMenu = false 
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToSchedule(0) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add schedule")
            }
        }
    ) { paddingValues ->
        if (schedules.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    Text(
                        "No schedules yet",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Plan your events and appointments",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (filteredSchedules.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "No schedules match filter",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Try changing your filter selection",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            if (showCalendarView) {
                // Calendar View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Calendar View",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Simple calendar implementation - showing current month schedules
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Group schedules by date
                        val groupedSchedules = schedules.groupBy { schedule ->
                            try {
                                val instant = kotlinx.datetime.Instant.parse(schedule.startDateTime)
                                val localDate = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
                                localDate.toString()
                            } catch (e: Exception) {
                                schedule.startDateTime.split("T")[0] // Fallback to original method
                            }
                        }
                        
                        groupedSchedules.forEach { (date, schedulesForDate) ->
                            item {
                                Column {
                                    // Format the date header nicely
                                    val formattedDate = try {
                                        val localDate = kotlinx.datetime.LocalDate.parse(date)
                                        val javaDate = localDate.toJavaLocalDate()
                                        javaDate.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy"))
                                    } catch (e: Exception) {
                                        date // Fallback to original date string
                                    }
                                    
                                    Text(
                                        text = formattedDate,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    schedulesForDate.forEach { schedule ->
                                        ScheduleItem(
                                            schedule = schedule,
                                            is24HourFormat = is24HourFormat,
                                            onClick = { onNavigateToSchedule(schedule.id) },
                                            onCompleteClick = { viewModel.toggleCompletionStatus(schedule.id, !schedule.isCompleted) },
                                            onPinClick = { viewModel.togglePinStatus(schedule.id, !schedule.isPinned) },
                                            onDeleteClick = { viewModel.deleteSchedule(schedule) }
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // List View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSchedules) { schedule ->
                        ScheduleItem(
                            schedule = schedule,
                            is24HourFormat = is24HourFormat,
                            onClick = { onNavigateToSchedule(schedule.id) },
                            onCompleteClick = { viewModel.toggleCompletionStatus(schedule.id, !schedule.isCompleted) },
                            onPinClick = { viewModel.togglePinStatus(schedule.id, !schedule.isPinned) },
                            onDeleteClick = { viewModel.deleteSchedule(schedule) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleItem(
    schedule: Schedule,
    is24HourFormat: Boolean,
    onClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // Format datetime properly
    val formattedDateTime = formatScheduleDateTime(schedule.startDateTime, is24HourFormat)
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = schedule.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (schedule.isCompleted) TextDecoration.LineThrough else null,
                        color = if (schedule.isCompleted) 
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                    if (schedule.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = schedule.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formattedDateTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (schedule.location.isNotEmpty()) {
                        Text(
                            text = schedule.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Action buttons organized horizontally: Checkbox, Pin, Delete
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = schedule.isCompleted,
                        onCheckedChange = { onCompleteClick() }
                    )
                    IconButton(onClick = onPinClick) {
                        Icon(
                            if (schedule.isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                            contentDescription = if (schedule.isPinned) "Unpin" else "Pin",
                            tint = if (schedule.isPinned) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SchedulesScreenPreview() {
    NANATheme {
        SchedulesScreen(
            onNavigateToSchedule = {}
        )
    }
}

/**
 * Formats a schedule datetime string to a user-friendly format
 * Respects the user's 24-hour format preference
 */
fun formatScheduleDateTime(dateTimeString: String, is24HourFormat: Boolean): String {
    return try {
        // Add debug logging to see raw input
        android.util.Log.d("NANA_DEBUG", "formatScheduleDateTime input: '$dateTimeString', is24Hour: $is24HourFormat")
        
        // Parse the ISO datetime string
        val instant = kotlinx.datetime.Instant.parse(dateTimeString)
        val localDateTime = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        
        // Format date part
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        val datePart = localDateTime.toJavaLocalDateTime().format(dateFormatter)
        
        // Format time part based on user preference
        val timeFormatter = if (is24HourFormat) {
            DateTimeFormatter.ofPattern("HH:mm")
        } else {
            DateTimeFormatter.ofPattern("h:mm a")
        }
        val timePart = localDateTime.toJavaLocalDateTime().format(timeFormatter)
        
        val result = "$datePart at $timePart"
        android.util.Log.d("NANA_DEBUG", "formatScheduleDateTime result: '$result'")
        result
    } catch (e: Exception) {
        android.util.Log.e("NANA_DEBUG", "formatScheduleDateTime parse error for '$dateTimeString': ${e.message}")
        
        // Fallback for malformed datetime strings
        try {
            // Try to extract date and time parts from common formats
            val parts = dateTimeString.split("T")
            if (parts.size >= 2) {
                val datePart = parts[0] // YYYY-MM-DD
                val timePart = parts[1].take(5) // HH:MM
                
                // Parse and reformat date
                val localDate = java.time.LocalDate.parse(datePart)
                val formattedDate = localDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                
                // Parse and reformat time
                val localTime = java.time.LocalTime.parse(timePart)
                val timeFormatter = if (is24HourFormat) {
                    DateTimeFormatter.ofPattern("HH:mm")
                } else {
                    DateTimeFormatter.ofPattern("h:mm a")
                }
                val formattedTime = localTime.format(timeFormatter)
                
                val result = "$formattedDate at $formattedTime"
                android.util.Log.d("NANA_DEBUG", "formatScheduleDateTime fallback result: '$result'")
                result
            } else {
                // Last resort: clean up the original string
                val result = dateTimeString.replace("T", " at ").take(20)
                android.util.Log.d("NANA_DEBUG", "formatScheduleDateTime cleanup result: '$result'")
                result
            }
        } catch (e2: Exception) {
            android.util.Log.e("NANA_DEBUG", "formatScheduleDateTime fallback error: ${e2.message}")
            // Ultimate fallback
            val result = dateTimeString.replace("T", " at ").take(20)
            android.util.Log.d("NANA_DEBUG", "formatScheduleDateTime ultimate fallback: '$result'")
            result
        }
    }
}
