package com.allubie.nana.ui.screens.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.allubie.nana.ui.viewmodel.NotesViewModel
import com.allubie.nana.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteViewerScreen(
    noteId: String,
    notesViewModel: NotesViewModel,
    onEdit: () -> Unit,
    onBack: () -> Unit
) {
    var noteTitle by remember { mutableStateOf("Loading...") }
    var noteContent by remember { mutableStateOf("") }
    
    // Get the note data when the screen loads
    LaunchedEffect(noteId) {
        if (noteId.isNotEmpty()) {
            notesViewModel.getNoteByStringId(noteId) { note ->
                if (note != null) {
                    noteTitle = note.title
                    noteContent = note.content
                } else {
                    noteTitle = "Note not found"
                    noteContent = "This note may have been deleted."
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Note",
                        fontWeight = FontWeight.Medium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Spacing.screenPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.screenPadding)
        ) {
            Text(
                text = noteTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (noteContent.isNotBlank()) {
                Text(
                    text = noteContent,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
                )
            } else {
                Text(
                    text = "No content",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
