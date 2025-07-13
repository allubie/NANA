package com.allubie.nana.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    // Collect state from ViewModel
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val events by viewModel.eventsForSelectedDate.collectAsStateWithLifecycle()

    // Week dates
    val weekDates = remember {
        val dates = mutableListOf<Date>()
        val cal = Calendar.getInstance()
        // Start from today and show next 7 days
        for (i in 0..6) {
            dates.add(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        dates
    }

    var showEventDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<ScheduleEvent?>(null) }

    val today = remember { Calendar.getInstance().time }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Schedule", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedEvent = null
                showEventDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Event")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Week navigation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Month and year
                    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                    Text(
                        text = monthYearFormat.format(selectedDate),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        textAlign = TextAlign.Center
                    )

                    // Days of week
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 0..6) {
                            val date = weekDates[i]
                            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
                            val dayOfMonth = Calendar.getInstance().apply {
                                time = date
                            }.get(Calendar.DAY_OF_MONTH)

                            val isSelected = dateFormat.format(date) == dateFormat.format(selectedDate)
                            val isToday = dateFormat.format(date) == dateFormat.format(today)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        // Use ViewModel to set selected date
                                        viewModel.setSelectedDate(date)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = dayFormat.format(date),
                                    style = MaterialTheme.typography.labelMedium
                                )

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            color = when {
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                isToday -> MaterialTheme.colorScheme.primaryContainer
                                                else -> Color.Transparent
                                            },
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                ) {
                                    Text(
                                        text = dayOfMonth.toString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = when {
                                            isSelected -> MaterialTheme.colorScheme.onPrimary
                                            isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                            else -> LocalContentColor.current
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Events for selected date
            Box(modifier = Modifier.fillMaxSize()) {
                if (events.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No events for this day",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to add an event",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    // Events list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(events) { event ->
                            EventCard(
                                event = event,
                                onEditClick = {
                                    selectedEvent = event
                                    showEventDialog = true
                                },
                                onDeleteClick = {
                                    // Use ViewModel to delete event
                                    viewModel.deleteEvent(event)
                                }
                            )
                        }
                    }
                }

                // Event dialog
                if (showEventDialog) {
                    EventDialog(
                        event = selectedEvent,
                        initialDate = selectedDate,
                        onDismiss = { showEventDialog = false },
                        onSave = { title, date, startTime, endTime, location, description, type ->
                            if (selectedEvent == null) {
                                // Add new event via ViewModel
                                viewModel.addEvent(
                                    title, date, startTime, endTime,
                                    location, description, type
                                )
                            } else {
                                // Update existing event via ViewModel
                                viewModel.updateEvent(
                                    selectedEvent!!, title, date, startTime, endTime,
                                    location, description, type
                                )
                            }
                            showEventDialog = false
                        }
                    )
                }
            }
        }
    }
}



@Composable
fun EventCard(
    event: ScheduleEvent,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = event.type.color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = event.type.color.copy(alpha = 0.2f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    event.type.icon,
                    contentDescription = null,
                    tint = event.type.color
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.startTime} - ${event.endTime}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (event.location.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.location,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (event.isRecurring && event.recurringDays.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Repeat,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Recurring: " + event.recurringDays.joinToString(", ") {
                                it.name.substring(0, 3)
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Column {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit")
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDialog(
    event: ScheduleEvent?,
    initialDate: Date,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        date: Date,
        startTime: String,
        endTime: String,
        location: String,
        description: String,
        type: EventType
    ) -> Unit
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var date by remember { mutableStateOf(event?.date ?: initialDate) }
    var startTime by remember { mutableStateOf(event?.startTime ?: "09:00") }
    var endTime by remember { mutableStateOf(event?.endTime ?: "10:00") }
    var location by remember { mutableStateOf(event?.location ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var type by remember { mutableStateOf(event?.type ?: EventType.CLASS) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (event == null) "Add Event" else "Edit Event") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date selection (simplified for this demo)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Date: ")
                    Spacer(modifier = Modifier.width(8.dp))
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    Text(dateFormat.format(date))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Time selection
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Location field
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Event type selection
                Text("Event Type:", style = MaterialTheme.typography.titleSmall)

                Spacer(modifier = Modifier.height(8.dp))

                // First row of event types
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilterChip(
                        selected = type == EventType.CLASS,
                        onClick = { type = EventType.CLASS },
                        label = { Text(EventType.CLASS.label) },
                        leadingIcon = {
                            Icon(
                                EventType.CLASS.icon,
                                contentDescription = null,
                                tint = if (type == EventType.CLASS)
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                else EventType.CLASS.color
                            )
                        }
                    )

                    FilterChip(
                        selected = type == EventType.ASSIGNMENT,
                        onClick = { type = EventType.ASSIGNMENT },
                        label = { Text(EventType.ASSIGNMENT.label) },
                        leadingIcon = {
                            Icon(
                                EventType.ASSIGNMENT.icon,
                                contentDescription = null,
                                tint = if (type == EventType.ASSIGNMENT)
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                else EventType.ASSIGNMENT.color
                            )
                        }
                    )

                    FilterChip(
                        selected = type == EventType.EXAM,
                        onClick = { type = EventType.EXAM },
                        label = { Text(EventType.EXAM.label) },
                        leadingIcon = {
                            Icon(
                                EventType.EXAM.icon,
                                contentDescription = null,
                                tint = if (type == EventType.EXAM)
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                else EventType.EXAM.color
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Second row of event types
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilterChip(
                        selected = type == EventType.MEETING,
                        onClick = { type = EventType.MEETING },
                        label = { Text(EventType.MEETING.label) },
                        leadingIcon = {
                            Icon(
                                EventType.MEETING.icon,
                                contentDescription = null,
                                tint = if (type == EventType.MEETING)
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                else EventType.MEETING.color
                            )
                        }
                    )

                    FilterChip(
                        selected = type == EventType.PERSONAL,
                        onClick = { type = EventType.PERSONAL },
                        label = { Text(EventType.PERSONAL.label) },
                        leadingIcon = {
                            Icon(
                                EventType.PERSONAL.icon,
                                contentDescription = null,
                                tint = if (type == EventType.PERSONAL)
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                else EventType.PERSONAL.color
                            )
                        }
                    )

                    FilterChip(
                        selected = type == EventType.OTHER,
                        onClick = { type = EventType.OTHER },
                        label = { Text(EventType.OTHER.label) },
                        leadingIcon = {
                            Icon(
                                EventType.OTHER.icon,
                                contentDescription = null,
                                tint = if (type == EventType.OTHER)
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                else EventType.OTHER.color
                            )
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(title, date, startTime, endTime, location, description, type)
                },
                enabled = title.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank()
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

// Sample events for demonstration
private val sampleEvents = listOf(
    ScheduleEvent(
        title = "Math Lecture",
        date = Calendar.getInstance().time,
        startTime = "09:00",
        endTime = "10:30",
        location = "Room 101, Building A",
        type = EventType.CLASS,
        isRecurring = true,
        recurringDays = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
    ),
    ScheduleEvent(
        title = "Physics Lab",
        date = Calendar.getInstance().time,
        startTime = "13:00",
        endTime = "15:00",
        location = "Science Building, Lab 3",
        type = EventType.CLASS
    ),
    ScheduleEvent(
        title = "Study Group",
        date = Calendar.getInstance().time,
        startTime = "16:00",
        endTime = "18:00",
        location = "Library, Second Floor",
        description = "Review for midterm exam",
        type = EventType.MEETING
    ),
    ScheduleEvent(
        title = "Programming Assignment Due",
        date = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 2)
        }.time,
        startTime = "23:59",
        endTime = "23:59",
        description = "Submit on the course website",
        type = EventType.ASSIGNMENT
    ),
    ScheduleEvent(
        title = "Midterm Exam",
        date = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 5)
        }.time,
        startTime = "14:00",
        endTime = "16:00",
        location = "Main Hall",
        description = "Chapters 1-5",
        type = EventType.EXAM
    )
)