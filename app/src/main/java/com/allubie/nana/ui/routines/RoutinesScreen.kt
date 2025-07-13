package com.allubie.nana.ui.routines

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    viewModel: RoutinesViewModel = hiltViewModel()
) {
    // Collect routines from ViewModel
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    var showRoutineDialog by remember { mutableStateOf(false) }
    var selectedRoutine by remember { mutableStateOf<Routine?>(null) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Routines", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedRoutine = null
                showRoutineDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Routine")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (routines.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "No routines yet",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tap + to create your first routine",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // Routines list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(routines) { routine ->
                        RoutineCard(
                            routine = routine,
                            onTaskToggle = { taskId, isCompleted ->
                                // Use ViewModel to toggle task completion
                                viewModel.toggleTaskCompletion(routine.id, taskId, isCompleted)
                            },
                            onEditClick = {
                                selectedRoutine = routine
                                showRoutineDialog = true
                            },
                            onDeleteClick = {
                                // Use ViewModel to delete routine
                                viewModel.deleteRoutine(routine)
                            }
                        )
                    }
                }
            }

            // Routine dialog
            if (showRoutineDialog) {
                RoutineDialog(
                    routine = selectedRoutine,
                    onDismiss = { showRoutineDialog = false },
                    onSave = { name, tasks, icon, daysActive ->
                        if (selectedRoutine == null) {
                            // Add new routine via ViewModel
                            viewModel.addRoutine(name, tasks, icon, daysActive)
                        } else {
                            // Update existing routine via ViewModel
                            viewModel.updateRoutine(selectedRoutine!!, name, tasks, icon, daysActive)
                        }
                        showRoutineDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun RoutineCard(
    routine: Routine,
    onTaskToggle: (taskId: String, isCompleted: Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Routine header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(routine.icon.icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = routine.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                // Days active
                Text(
                    text = routine.daysActive.joinToString("") { it.name.first().toString() },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tasks
            routine.tasks.forEach { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { checked ->
                            onTaskToggle(task.id, checked)
                        }
                    )

                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    if (task.time.isNotBlank()) {
                        Text(
                            text = task.time,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Edit button
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit")
                }

                // Delete button
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDialog(
    routine: Routine?,
    onDismiss: () -> Unit,
    onSave: (name: String, tasks: List<RoutineTask>, icon: RoutineIcon, daysActive: List<DayOfWeek>) -> Unit
) {
    var name by remember { mutableStateOf(routine?.name ?: "") }
    var tasks by remember { mutableStateOf(routine?.tasks ?: emptyList()) }
    var icon by remember { mutableStateOf(routine?.icon ?: RoutineIcon.DEFAULT) }
    val defaultDays = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )
    var daysActive by remember { mutableStateOf(routine?.daysActive ?: defaultDays) }

    var newTaskName by remember { mutableStateOf("") }
    var newTaskTime by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (routine == null) "Add Routine" else "Edit Routine") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Routine Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Icon selection
                Text("Select Icon:", style = MaterialTheme.typography.titleSmall)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RoutineIcon.values().forEach { routineIcon ->
                        IconButton(
                            onClick = { icon = routineIcon },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (icon == routineIcon)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(
                                routineIcon.icon,
                                contentDescription = routineIcon.description
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Days selection
                Text("Active Days:", style = MaterialTheme.typography.titleSmall)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DayOfWeek.values().forEach { day ->
                        val isSelected = daysActive.contains(day)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                daysActive = if (isSelected) {
                                    daysActive - day
                                } else {
                                    daysActive + day
                                }
                            },
                            label = { Text(day.name.first().toString()) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tasks section
                Text("Tasks:", style = MaterialTheme.typography.titleSmall)

                // Existing tasks
                tasks.forEach { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = task.name,
                            modifier = Modifier.weight(1f)
                        )

                        if (task.time.isNotBlank()) {
                            Text(
                                text = task.time,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        IconButton(
                            onClick = { tasks = tasks - task }
                        ) {
                            Icon(Icons.Outlined.Close, contentDescription = "Remove")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Add new task
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newTaskName,
                        onValueChange = { newTaskName = it },
                        label = { Text("New Task") },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = newTaskTime,
                        onValueChange = { newTaskTime = it },
                        label = { Text("Time") },
                        modifier = Modifier.width(100.dp)
                    )

                    IconButton(
                        onClick = {
                            if (newTaskName.isNotBlank()) {
                                tasks = tasks + RoutineTask(name = newTaskName, time = newTaskTime)
                                newTaskName = ""
                                newTaskTime = ""
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = "Add Task")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, tasks, icon, daysActive) },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Sample routines for demonstration
private val sampleRoutines = listOf(
    Routine(
        name = "Morning Routine",
        tasks = listOf(
            RoutineTask(name = "Wake up", time = "06:30"),
            RoutineTask(name = "Drink water", time = "06:35"),
            RoutineTask(name = "Morning exercise", time = "06:45"),
            RoutineTask(name = "Shower", time = "07:15"),
            RoutineTask(name = "Breakfast", time = "07:30")
        ),
        icon = RoutineIcon.MORNING,
        daysActive = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        )
    ),
    Routine(
        name = "Study Session",
        tasks = listOf(
            RoutineTask(name = "Review notes", time = "14:00"),
            RoutineTask(name = "Practice problems", time = "14:30"),
            RoutineTask(name = "Take break", time = "15:30"),
            RoutineTask(name = "Continue studying", time = "15:45")
        ),
        icon = RoutineIcon.STUDY,
        daysActive = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.FRIDAY
        )
    )
)