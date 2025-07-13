package com.allubie.nana.ui.notes

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

// Data class for note
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val color: NoteColor = NoteColor.Default
)

// Enum for note colors
enum class NoteColor(val value: androidx.compose.ui.graphics.Color) {
    Default(androidx.compose.ui.graphics.Color(0xFFFFFFFF)),
    Red(androidx.compose.ui.graphics.Color(0xFFFFCDD2)),
    Orange(androidx.compose.ui.graphics.Color(0xFFFFE0B2)),
    Yellow(androidx.compose.ui.graphics.Color(0xFFFFF9C4)),
    Green(androidx.compose.ui.graphics.Color(0xFFC8E6C9)),
    Blue(androidx.compose.ui.graphics.Color(0xFFBBDEFB)),
    Purple(androidx.compose.ui.graphics.Color(0xFFE1BEE7))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel()
) {
    // Sample notes for demonstration
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    var showNoteDialog by remember { mutableStateOf(false) }
    var currentNote by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Notes", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                currentNote = null  // Reset current note
                showNoteDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Note")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (notes.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "No notes yet",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tap + to create your first note",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // Notes list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notes) { note ->
                        NoteCard(
                            note = note,
                            onEditClick = {
                                currentNote = note
                                showNoteDialog = true
                            },
                            onDeleteClick = {
                                viewModel.deleteNote(note)
                            }
                        )
                    }
                }
            }

            // Note dialog
            if (showNoteDialog) {
                NoteDialog(
                    note = currentNote,
                    onDismiss = { showNoteDialog = false },
                    onSave = { title, content, color ->
                        if (currentNote == null) {
                            // Add new note via ViewModel
                            viewModel.addNote(title, content, color)
                        } else {
                            // Update existing note via ViewModel
                            viewModel.updateNote(currentNote!!, title, content, color)
                        }
                        showNoteDialog = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    note: Note,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = note.color.value.copy(alpha = 0.3f)
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
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Timestamp
                Text(
                    text = formatDate(note.timestamp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

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

@Composable
fun NoteDialog(
    note: Note?,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, color: NoteColor) -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var color by remember { mutableStateOf(note?.color ?: NoteColor.Default) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (note == null) "Add Note" else "Edit Note") },
        text = {
            Column {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Content field
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Color selection
                Text("Select color:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NoteColor.values().forEach { noteColor ->
                        ColorCircle(
                            color = noteColor.value,
                            selected = color == noteColor,
                            onClick = { color = noteColor }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(title, content, color) },
                enabled = title.isNotBlank()
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

@Composable
fun ColorCircle(
    color: androidx.compose.ui.graphics.Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .then(
                if (selected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                } else {
                    Modifier
                }
            )
    )
}

private fun formatDate(timestamp: Long): String {
    val format = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return format.format(Date(timestamp))
}

// Sample notes for demonstration
private val sampleNotes = listOf(
    Note(
        title = "Math Homework",
        content = "Complete calculus problems 1-10 for tomorrow's class",
        color = NoteColor.Yellow
    ),
    Note(
        title = "Project Ideas",
        content = "1. Mobile app for campus navigation\n2. Study group finder\n3. AI-based notes summarizer",
        color = NoteColor.Blue
    ),
    Note(
        title = "Reading List",
        content = "- The Alchemist\n- Atomic Habits\n- Deep Work\n- Design Patterns in Java",
        color = NoteColor.Green
    )
)