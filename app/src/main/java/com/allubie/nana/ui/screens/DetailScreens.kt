package com.allubie.nana.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.*
import com.allubie.nana.data.entity.Note
import com.allubie.nana.data.entity.Routine
import com.allubie.nana.data.entity.RoutineFrequency
import com.allubie.nana.data.entity.Schedule
import com.allubie.nana.data.entity.Expense
import com.allubie.nana.ui.components.SimpleRichTextEditor
import com.allubie.nana.data.database.NANADatabase
import com.allubie.nana.data.repository.NoteRepository
import com.allubie.nana.data.repository.RoutineRepository
import com.allubie.nana.data.repository.ScheduleRepository
import com.allubie.nana.data.repository.ExpenseRepository
import com.allubie.nana.ui.viewmodel.NoteViewModel
import com.allubie.nana.ui.viewmodel.NoteViewModelFactory
import com.allubie.nana.ui.viewmodel.RoutineViewModel
import com.allubie.nana.ui.viewmodel.RoutineViewModelFactory
import com.allubie.nana.ui.viewmodel.ScheduleViewModel
import com.allubie.nana.ui.viewmodel.ScheduleViewModelFactory
import com.allubie.nana.ui.viewmodel.ExpenseViewModel
import com.allubie.nana.ui.viewmodel.ExpenseViewModelFactory
import com.allubie.nana.ui.viewmodel.SettingsViewModel
import com.allubie.nana.ui.viewmodel.SettingsViewModelFactory
import com.allubie.nana.ui.theme.NANATheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Long,
    onNavigateBack: () -> Unit,
    isEditing: Boolean = false
) {
    val context = LocalContext.current
    val database = NANADatabase.getDatabase(context)
    val repository = NoteRepository(database.noteDao())
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModelFactory(repository))
    
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var richContent by remember { mutableStateOf("") }  // Now stores HTML content
    var isPinned by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var isInEditMode by remember { mutableStateOf(isEditing) }
    
    // Load existing note if editing
    LaunchedEffect(noteId) {
        if (noteId != 0L) {
            viewModel.loadNoteById(noteId)
        }
    }
    
    // Update UI when note is loaded
    val currentNote by viewModel.currentNote.collectAsState()
    LaunchedEffect(currentNote) {
        currentNote?.let { note ->
            title = note.title
            content = note.content
            richContent = if (note.richContent.isNotBlank()) {
                note.richContent
            } else {
                note.content
            }
            isPinned = note.isPinned
            selectedCategory = note.category
        }
    }
    
    val categories = listOf("Personal", "Study", "Work", "Ideas", "Important")
    
    fun saveNote() {
        val hasRichContent = richContent.isNotBlank() && richContent != content
        
        val note = if (noteId == 0L) {
            Note(
                title = title,
                content = if (hasRichContent) richContent else content,
                richContent = if (hasRichContent) richContent else "",
                category = selectedCategory,
                isPinned = isPinned,
                hasImages = richContent.contains("<img"),
                hasCheckboxes = richContent.contains("checkbox"),
                hasLinks = richContent.contains("<a "),
                hasFormatting = richContent.contains("<b>") || richContent.contains("<i>") || richContent.contains("<u>"),
                createdAt = "",
                updatedAt = ""
            )
        } else {
            currentNote?.copy(
                title = title,
                content = if (hasRichContent) richContent else content,
                richContent = if (hasRichContent) richContent else "",
                category = selectedCategory,
                isPinned = isPinned,
                hasImages = richContent.contains("<img"),
                hasCheckboxes = richContent.contains("checkbox"),
                hasLinks = richContent.contains("<a "),
                hasFormatting = richContent.contains("<b>") || richContent.contains("<i>") || richContent.contains("<u>")
            ) ?: return
        }
        viewModel.saveNote(note)
        onNavigateBack()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == 0L) "New Note" else if (isInEditMode) "Edit Note" else "Note") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isInEditMode) {
                        // Edit mode actions
                        IconButton(onClick = { 
                            isPinned = !isPinned
                            if (noteId != 0L) {
                                viewModel.togglePinStatus(noteId, isPinned)
                            }
                        }) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = if (isPinned) "Unpin" else "Pin",
                                tint = if (isPinned) MaterialTheme.colorScheme.primary 
                                      else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                        IconButton(onClick = { saveNote() }) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    } else {
                        // View mode actions
                        IconButton(onClick = { 
                            isPinned = !isPinned
                            if (noteId != 0L) {
                                viewModel.togglePinStatus(noteId, isPinned)
                            }
                        }) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = if (isPinned) "Unpin" else "Pin",
                                tint = if (isPinned) MaterialTheme.colorScheme.primary 
                                      else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                        IconButton(onClick = { isInEditMode = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Category selection
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Category",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            FilterChip(
                                onClick = { selectedCategory = if (selectedCategory == category) "" else category },
                                label = { Text(category) },
                                selected = selectedCategory == category
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title field
            if (isInEditMode) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                Text(
                    text = title.ifEmpty { "Untitled Note" },
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content/Rich Text
            if (isInEditMode) {
                // Rich Text Editor
                SimpleRichTextEditor(
                    html = richContent,
                    onTextChange = { newContent ->
                        richContent = newContent
                        // Extract plain text for content field if needed
                        content = newContent.replace(Regex("<[^>]*>"), "")
                    },
                    placeholder = "Start writing your note...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .heightIn(min = 300.dp)
                )
            } else {
                // Read-only content display
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        if (content.isNotEmpty()) {
                            Text(
                                text = content,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = "No content",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(
    routineId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val database = NANADatabase.getDatabase(context)
    val repository = RoutineRepository(database.routineDao(), context)
    val viewModel: RoutineViewModel = viewModel(factory = RoutineViewModelFactory(repository))
    
    // Settings for time format
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val is24HourFormat by settingsViewModel.is24HourFormat.collectAsState()
    
    var routineName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(setOf<String>()) }
    var startTime by remember { mutableStateOf("09:00") }
    var isEnabled by remember { mutableStateOf(true) }
    var reminderEnabled by remember { mutableStateOf(false) }
    var reminderMinutes by remember { mutableStateOf(5) }
    
    // Time picker states - Use state variables to track picker values
    var showStartTimePicker by remember { mutableStateOf(false) }
    
    // Track picker state values separately to avoid recreation issues
    var startTimePickerHour by remember { mutableStateOf(9) }
    var startTimePickerMinute by remember { mutableStateOf(0) }
    
    // Update picker values when time strings change
    LaunchedEffect(startTime) {
        try {
            val hour = startTime.substring(0, 2).toIntOrNull() ?: 9
            val minute = startTime.substring(3, 5).toIntOrNull() ?: 0
            startTimePickerHour = hour
            startTimePickerMinute = minute
        } catch (e: Exception) {
            startTimePickerHour = 9
            startTimePickerMinute = 0
        }
    }

    // Load existing routine if editing
    LaunchedEffect(routineId) {
        if (routineId != 0L) {
            viewModel.loadRoutineById(routineId)
        }
    }
    
    // Update UI when routine is loaded
    val currentRoutine by viewModel.currentRoutine.collectAsState()
    LaunchedEffect(currentRoutine) {
        currentRoutine?.let { routine ->
            routineName = routine.title
            description = routine.description
            selectedDays = routine.daysOfWeek.map { it.name.take(3) }.toSet()
            startTime = routine.time ?: "09:00"
            // For routines, we only use start time since it's a single event
            isEnabled = routine.isActive
            reminderEnabled = routine.reminderEnabled
            reminderMinutes = routine.reminderMinutesBefore
        }
    }
    
    fun saveRoutine() {
        val routine = if (routineId == 0L) {
            Routine(
                title = routineName,
                description = description,
                daysOfWeek = emptyList(), // Simplified for now
                frequency = RoutineFrequency.DAILY,
                time = startTime,
                isActive = isEnabled,
                reminderEnabled = reminderEnabled,
                reminderMinutesBefore = reminderMinutes,
                createdAt = ""
            )
        } else {
            currentRoutine?.copy(
                title = routineName,
                description = description,
                time = startTime,
                isActive = isEnabled,
                reminderEnabled = reminderEnabled,
                reminderMinutesBefore = reminderMinutes
            ) ?: return
        }
        viewModel.saveRoutine(routine)
        onNavigateBack()
    }
    
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val reminderOptions = listOf(5, 10, 15, 30, 60)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (routineId == 0L) "New Routine" else "Edit Routine") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { saveRoutine() }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Routine Name
            OutlinedTextField(
                value = routineName,
                onValueChange = { routineName = it },
                label = { Text("Routine Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            // Days Selection
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Days of Week",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(daysOfWeek) { day ->
                            FilterChip(
                                onClick = { 
                                    selectedDays = if (selectedDays.contains(day)) 
                                        selectedDays - day 
                                    else 
                                        selectedDays + day 
                                },
                                label = { Text(day) },
                                selected = selectedDays.contains(day)
                            )
                        }
                    }
                }
            }
            
            // Time Settings
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Time Settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Time") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showStartTimePicker = true }) {
                                    Icon(Icons.Default.AccessTime, contentDescription = "Pick time")
                                }
                            }
                        )
                    }
                }
            }
            
            // Notifications
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Enable Notifications",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it }
                        )
                    }
                    
                    if (reminderEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Remind me before",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(reminderOptions) { minutes ->
                                FilterChip(
                                    onClick = { reminderMinutes = minutes },
                                    label = { Text("${minutes}m") },
                                    selected = reminderMinutes == minutes
                                )
                            }
                        }
                    }
                }
            }
            
            // Enable/Disable Routine
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Enable Routine",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Routine will be active and send notifications",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it }
                    )
                }
            }
        }
    }
    
    // Start Time Picker Dialog
    if (showStartTimePicker) {
        val startTimePickerState = remember(key1 = startTimePickerHour, key2 = startTimePickerMinute, key3 = is24HourFormat) {
            TimePickerState(
                initialHour = startTimePickerHour,
                initialMinute = startTimePickerMinute,
                is24Hour = is24HourFormat
            )
        }
        
        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            title = { Text("Select Time") },
            text = {
                TimePicker(state = startTimePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        val hour = startTimePickerState.hour
                        val minute = startTimePickerState.minute
                        startTime = String.format("%02d:%02d", hour, minute)
                        showStartTimePicker = false 
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailScreen(
    scheduleId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val database = NANADatabase.getDatabase(context)
    val repository = ScheduleRepository(database.scheduleDao(), context)
    val viewModel: ScheduleViewModel = viewModel(factory = ScheduleViewModelFactory(repository))
    
    // Settings for time format
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val is24HourFormat by settingsViewModel.is24HourFormat.collectAsState()
    
    var eventTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }
    var location by remember { mutableStateOf("") }
    var isRecurring by remember { mutableStateOf(false) }
    var recurringType by remember { mutableStateOf("Weekly") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var reminderMinutes by remember { mutableStateOf(15) }
    var priority by remember { mutableStateOf("Medium") }
    var isPinned by remember { mutableStateOf(false) }
    
    // Date and Time picker states - Use state variables to track picker values
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState(
        yearRange = 2025..2030  // Restrict years from 2025 to 2030
    )
    
    // Track picker state values separately to avoid recreation issues
    var startTimePickerHour by remember { mutableStateOf(9) }
    var startTimePickerMinute by remember { mutableStateOf(0) }
    var endTimePickerHour by remember { mutableStateOf(10) }
    var endTimePickerMinute by remember { mutableStateOf(0) }
    
    // Update picker values when time strings change
    LaunchedEffect(startTime) {
        try {
            val hour = startTime.substring(0, 2).toIntOrNull() ?: 9
            val minute = startTime.substring(3, 5).toIntOrNull() ?: 0
            startTimePickerHour = hour
            startTimePickerMinute = minute
        } catch (e: Exception) {
            startTimePickerHour = 9
            startTimePickerMinute = 0
        }
    }
    
    LaunchedEffect(endTime) {
        try {
            val hour = endTime.substring(0, 2).toIntOrNull() ?: 10
            val minute = endTime.substring(3, 5).toIntOrNull() ?: 0
            endTimePickerHour = hour
            endTimePickerMinute = minute
        } catch (e: Exception) {
            endTimePickerHour = 10
            endTimePickerMinute = 0
        }
    }
    
    // Load existing schedule if editing
    LaunchedEffect(scheduleId) {
        if (scheduleId != 0L) {
            viewModel.loadScheduleById(scheduleId)
        }
    }
    
    val currentSchedule by viewModel.currentSchedule.collectAsState()
    LaunchedEffect(currentSchedule) {
        currentSchedule?.let { schedule ->
            eventTitle = schedule.title
            description = schedule.description
            
            // Parse the ISO format datetime safely
            try {
                // Use simple string parsing since we know the format is "YYYY-MM-DDTHH:MM:SS"
                val startDatePart = schedule.startDateTime.substring(0, 10) // YYYY-MM-DD
                val startTimePart = schedule.startDateTime.substring(11, 16) // HH:MM
                val endTimePart = schedule.endDateTime.substring(11, 16) // HH:MM
                
                selectedDate = startDatePart
                startTime = startTimePart
                endTime = endTimePart
                
            } catch (e: Exception) {
                // Fallback to default values if parsing fails
                selectedDate = ""
                startTime = "09:00"
                endTime = "10:00"
            }
            
            location = schedule.location
            isPinned = schedule.isPinned
            reminderEnabled = schedule.reminderEnabled
            reminderMinutes = schedule.reminderMinutesBefore
            isRecurring = schedule.isRecurring
            recurringType = schedule.recurrencePattern ?: "Weekly"
        }
    }
    
    fun saveSchedule() {
        // Validate mandatory date field
        if (selectedDate.isEmpty()) {
            android.util.Log.e("NANA_DEBUG", "Date is mandatory - cannot save schedule without date")
            return
        }
        
        if (eventTitle.trim().isEmpty()) {
            android.util.Log.e("NANA_DEBUG", "Title is mandatory - cannot save schedule without title")
            return
        }
        
        android.util.Log.d("NANA_DEBUG", "=== SaveSchedule Debug ===")
        android.util.Log.d("NANA_DEBUG", "Event Title: '$eventTitle'")
        android.util.Log.d("NANA_DEBUG", "Selected Date: '$selectedDate'")
        android.util.Log.d("NANA_DEBUG", "Start Time: '$startTime'")
        android.util.Log.d("NANA_DEBUG", "End Time: '$endTime'")
        android.util.Log.d("NANA_DEBUG", "Times Equal: ${startTime == endTime}")
        
        // Create proper ISO format datetime strings using kotlinx.datetime for consistency
        val currentInstant = kotlinx.datetime.Clock.System.now()
        val currentTimeZone = kotlinx.datetime.TimeZone.currentSystemDefault()
        
        val startDateTime = try {
            val dateTimeParts = selectedDate.split("-")
            val timeParts = startTime.split(":")
            
            if (dateTimeParts.size == 3 && timeParts.size == 2) {
                val localDateTime = kotlinx.datetime.LocalDateTime(
                    year = dateTimeParts[0].toInt(),
                    monthNumber = dateTimeParts[1].toInt(),
                    dayOfMonth = dateTimeParts[2].toInt(),
                    hour = timeParts[0].toInt(),
                    minute = timeParts[1].toInt(),
                    second = 0
                )
                // Store as ISO string for consistent timezone handling
                localDateTime.toInstant(currentTimeZone).toString()
            } else {
                // Fallback - create proper ISO format with current timezone offset
                val localDateTime = kotlinx.datetime.LocalDateTime.parse("${selectedDate}T${startTime}:00")
                localDateTime.toInstant(currentTimeZone).toString()
            }
        } catch (e: Exception) {
            // Safe fallback - create a valid ISO instant
            try {
                val localDateTime = kotlinx.datetime.LocalDateTime.parse("${selectedDate}T${startTime}:00")
                localDateTime.toInstant(kotlinx.datetime.TimeZone.UTC).toString()
            } catch (e2: Exception) {
                // Ultimate fallback - use current time
                kotlinx.datetime.Clock.System.now().toString()
            }
        }
        
        val endDateTime = try {
            val dateTimeParts = selectedDate.split("-")
            val timeParts = endTime.split(":")
            
            if (dateTimeParts.size == 3 && timeParts.size == 2) {
                val localDateTime = kotlinx.datetime.LocalDateTime(
                    year = dateTimeParts[0].toInt(),
                    monthNumber = dateTimeParts[1].toInt(),
                    dayOfMonth = dateTimeParts[2].toInt(),
                    hour = timeParts[0].toInt(),
                    minute = timeParts[1].toInt(),
                    second = 0
                )
                // Store as ISO string for consistent timezone handling
                localDateTime.toInstant(currentTimeZone).toString()
            } else {
                // Fallback - create proper ISO format with current timezone offset
                val localDateTime = kotlinx.datetime.LocalDateTime.parse("${selectedDate}T${endTime}:00")
                localDateTime.toInstant(currentTimeZone).toString()
            }
        } catch (e: Exception) {
            // Safe fallback - create a valid ISO instant
            try {
                val localDateTime = kotlinx.datetime.LocalDateTime.parse("${selectedDate}T${endTime}:00")
                localDateTime.toInstant(kotlinx.datetime.TimeZone.UTC).toString()
            } catch (e2: Exception) {
                // Ultimate fallback - use current time
                kotlinx.datetime.Clock.System.now().toString()
            }
        }
        
        android.util.Log.d("NANA_DEBUG", "Generated Start DateTime: '$startDateTime'")
        android.util.Log.d("NANA_DEBUG", "Generated End DateTime: '$endDateTime'")
        android.util.Log.d("NANA_DEBUG", "DateTime Strings Equal: ${startDateTime == endDateTime}")
        
        val currentTimestamp = currentInstant.toString()
        
        val schedule = if (scheduleId == 0L) {
            Schedule(
                title = eventTitle,
                description = description,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                location = location,
                isPinned = isPinned,
                isCompleted = false,
                reminderEnabled = reminderEnabled,
                reminderMinutesBefore = reminderMinutes,
                isRecurring = isRecurring,
                recurrencePattern = if (isRecurring) recurringType else null,
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            )
        } else {
            currentSchedule?.copy(
                title = eventTitle,
                description = description,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                location = location,
                isPinned = isPinned,
                reminderEnabled = reminderEnabled,
                reminderMinutesBefore = reminderMinutes,
                isRecurring = isRecurring,
                recurrencePattern = if (isRecurring) recurringType else null,
                updatedAt = currentTimestamp
            ) ?: return
        }
        viewModel.saveSchedule(schedule)
        onNavigateBack()
    }
    
    val recurringTypes = listOf("Daily", "Weekly", "Monthly", "Yearly")
    val reminderOptions = listOf(5, 10, 15, 30, 60, 120)
    val priorities = listOf("Low", "Medium", "High", "Urgent")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (scheduleId == 0L) "New Event" else "Edit Event") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { saveSchedule() }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Event Title
            OutlinedTextField(
                value = eventTitle,
                onValueChange = { eventTitle = it },
                label = { Text("Event Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            // Date and Time Selection
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Date & Time",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Date Selection
                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = { },
                        label = { Text("Date") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Time Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { },
                            label = { Text("Start Time") },
                            modifier = Modifier.weight(1f),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showStartTimePicker = true }) {
                                    Icon(Icons.Default.AccessTime, contentDescription = "Pick start time")
                                }
                            }
                        )
                        
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { },
                            label = { Text("End Time") },
                            modifier = Modifier.weight(1f),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showEndTimePicker = true }) {
                                    Icon(Icons.Default.AccessTime, contentDescription = "Pick end time")
                                }
                            }
                        )
                    }
                }
            }
            
            // Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Recurring Settings
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Recurring Event",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Switch(
                            checked = isRecurring,
                            onCheckedChange = { isRecurring = it }
                        )
                    }
                    
                    if (isRecurring) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Repeat",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(recurringTypes) { type ->
                                FilterChip(
                                    onClick = { recurringType = type },
                                    label = { Text(type) },
                                    selected = recurringType == type
                                )
                            }
                        }
                    }
                }
            }
            
            // Notifications
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Enable Notifications",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it }
                        )
                    }
                    
                    if (reminderEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Remind me before",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(reminderOptions) { minutes ->
                                FilterChip(
                                    onClick = { reminderMinutes = minutes },
                                    label = { Text("${minutes}m") },
                                    selected = reminderMinutes == minutes
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { 
                        val selectedDateMillis = datePickerState.selectedDateMillis
                        if (selectedDateMillis != null) {
                            val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(selectedDateMillis)
                            val localDate = instant.toLocalDateTime(kotlinx.datetime.TimeZone.UTC).date
                            selectedDate = localDate.toString()
                        }
                        showDatePicker = false 
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Time Picker Dialogs
    if (showStartTimePicker) {
        val startTimePickerState = remember(key1 = startTimePickerHour, key2 = startTimePickerMinute, key3 = is24HourFormat) {
            TimePickerState(
                initialHour = startTimePickerHour,
                initialMinute = startTimePickerMinute,
                is24Hour = is24HourFormat
            )
        }
        
        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            title = { Text("Select Start Time") },
            text = {
                TimePicker(state = startTimePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        val hour = startTimePickerState.hour
                        val minute = startTimePickerState.minute
                        val newStartTime = String.format("%02d:%02d", hour, minute)
                        android.util.Log.d("NANA_DEBUG", "Schedule Start Time Picker - Hour: $hour, Minute: $minute, New Time: $newStartTime, Current End Time: $endTime")
                        startTime = newStartTime
                        showStartTimePicker = false 
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    if (showEndTimePicker) {
        val endTimePickerState = remember(key1 = endTimePickerHour, key2 = endTimePickerMinute, key3 = is24HourFormat) {
            TimePickerState(
                initialHour = endTimePickerHour,
                initialMinute = endTimePickerMinute,
                is24Hour = is24HourFormat
            )
        }
        
        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            title = { Text("Select End Time") },
            text = {
                TimePicker(state = endTimePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        val hour = endTimePickerState.hour
                        val minute = endTimePickerState.minute
                        val newEndTime = String.format("%02d:%02d", hour, minute)
                        android.util.Log.d("NANA_DEBUG", "Schedule End Time Picker - Hour: $hour, Minute: $minute, New Time: $newEndTime, Current Start Time: $startTime")
                        endTime = newEndTime
                        showEndTimePicker = false 
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
