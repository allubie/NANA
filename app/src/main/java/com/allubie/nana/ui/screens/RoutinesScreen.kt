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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.allubie.nana.data.entity.Routine
import com.allubie.nana.data.entity.RoutineFrequency
import com.allubie.nana.data.database.NANADatabase
import com.allubie.nana.data.repository.RoutineRepository
import com.allubie.nana.data.repository.SettingsRepository
import com.allubie.nana.ui.viewmodel.RoutineViewModel
import com.allubie.nana.ui.viewmodel.RoutineViewModelFactory
import com.allubie.nana.ui.viewmodel.SettingsViewModel
import com.allubie.nana.ui.viewmodel.SettingsViewModelFactory
import com.allubie.nana.ui.theme.NANATheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    onNavigateToRoutine: (Long) -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToStats: () -> Unit = {}
) {
    val context = LocalContext.current
    val database = NANADatabase.getDatabase(context)
    val repository = RoutineRepository(database.routineDao(), context)
    val viewModel: RoutineViewModel = viewModel(factory = RoutineViewModelFactory(repository))
    
    // Settings for time format
    val settingsRepository = SettingsRepository(context)
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val is24HourFormat by settingsViewModel.is24HourFormat.collectAsState()
    
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    
    val routines by viewModel.routines.collectAsState()
    
    // Filter routines based on selected filter
    val filteredRoutines = when (selectedFilter) {
        "Daily" -> routines.filter { it.frequency == RoutineFrequency.DAILY }
        "Weekly" -> routines.filter { it.frequency == RoutineFrequency.WEEKLY }
        "Pinned" -> routines.filter { it.isPinned }
        "Completed" -> routines.filter { it.isCompleted }
        else -> routines
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Routines") },
                actions = {
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
                                text = { Text("Statistics") },
                                onClick = { 
                                    onNavigateToStats()
                                    showFilterMenu = false 
                                }
                            )
                            listOf("All", "Daily", "Weekly", "Pinned", "Completed").forEach { filter ->
                                DropdownMenuItem(
                                    text = { Text(filter) },
                                    onClick = { 
                                        selectedFilter = filter
                                        showFilterMenu = false 
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToRoutine(0) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add routine")
            }
        }
    ) { paddingValues ->
        if (filteredRoutines.isEmpty()) {
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
                        Icons.AutoMirrored.Filled.List,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (selectedFilter == "All") "No routines yet" else "No $selectedFilter routines",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Create routines to build good habits",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredRoutines) { routine ->
                    RoutineItem(
                        routine = routine,
                        is24HourFormat = is24HourFormat,
                        onClick = { onNavigateToRoutine(routine.id) },
                        onCompleteClick = { viewModel.toggleCompletionStatus(routine.id, !routine.isCompleted) },
                        onPinClick = { viewModel.togglePin(routine.id, !routine.isPinned) },
                        onDeleteClick = { viewModel.deleteRoutine(routine) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineItem(
    routine: Routine,
    is24HourFormat: Boolean,
    onClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (routine.isPinned) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (routine.isPinned) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Pinned",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = routine.title,
                            style = if (routine.isCompleted) {
                                MaterialTheme.typography.titleMedium.copy(
                                    textDecoration = TextDecoration.LineThrough
                                )
                            } else {
                                MaterialTheme.typography.titleMedium
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (routine.isCompleted) 
                                MaterialTheme.colorScheme.onSurfaceVariant 
                            else 
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (routine.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = routine.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    // Show frequency and time
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        // Frequency badge
                        if (routine.frequency != RoutineFrequency.CUSTOM) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = routine.frequency.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        
                        if (routine.time != null) {
                            Text(
                                text = formatRoutineTime(routine.time, is24HourFormat),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                // Action buttons in order: Checkbox, Pin, Delete
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = routine.isCompleted,
                        onCheckedChange = { onCompleteClick() }
                    )
                    IconButton(onClick = onPinClick) {
                        Icon(
                            if (routine.isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                            contentDescription = if (routine.isPinned) "Unpin" else "Pin",
                            tint = if (routine.isPinned) 
                                MaterialTheme.colorScheme.primary 
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/**
 * Formats routine time string to respect user's 24-hour format preference
 */
fun formatRoutineTime(timeString: String?, is24HourFormat: Boolean): String {
    if (timeString == null) return ""
    
    return try {
        // Parse the time assuming HH:mm format
        val time = LocalTime.parse(timeString)
        
        val formatter = if (is24HourFormat) {
            DateTimeFormatter.ofPattern("HH:mm")
        } else {
            DateTimeFormatter.ofPattern("h:mm a")
        }
        
        time.format(formatter)
    } catch (e: Exception) {
        // If parsing fails, return original string
        timeString
    }
}

@Preview(showBackground = true)
@Composable
fun RoutinesScreenPreview() {
    NANATheme {
        RoutinesScreen(
            onNavigateToRoutine = {}
        )
    }
}
