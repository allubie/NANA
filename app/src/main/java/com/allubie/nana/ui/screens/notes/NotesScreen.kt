package com.allubie.nana.ui.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.ExperimentalComposeUiApi
import com.allubie.nana.ui.viewmodel.NotesViewModel
import com.allubie.nana.ui.components.SwipeableItemCard
import com.allubie.nana.ui.theme.Spacing
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val category: String? = null,
    val createdAt: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    onNoteClick: (String) -> Unit = {},
    onArchivedNotesClick: () -> Unit = {},
    onRecycleBinClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val allNotes by viewModel.notes.collectAsState(initial = emptyList())
    
    var isSearchVisible by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val displayNotes = allNotes.map { note: com.allubie.nana.data.entity.NoteEntity ->
        Note(
            id = note.id.toString(),
            title = note.title,
            content = note.content,
            isPinned = note.isPinned,
            category = note.category,
            createdAt = formatDate(note.createdAt)
        )
    }

    val filteredNotes = remember(displayNotes, searchQuery) {
        if (searchQuery.isBlank()) {
            displayNotes
        } else {
            displayNotes.filter { note ->
                note.title.contains(searchQuery, ignoreCase = true) ||
                note.content.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(isSearchVisible) {
        if (isSearchVisible) {
            focusRequester.requestFocus()
        } else {
            searchQuery = ""
        }
    }
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                actions = {
                    IconButton(onClick = { 
                        isSearchVisible = !isSearchVisible
                        if (!isSearchVisible) {
                            keyboardController?.hide()
                        }
                    }) {
                        Icon(
                            if (isSearchVisible) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = if (isSearchVisible) "Close search" else "Search"
                        )
                    }
                    IconButton(onClick = { 
                        showOverflowMenu = true
                    }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                        
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Archived Notes") },
                                onClick = { 
                                    showOverflowMenu = false
                                    onArchivedNotesClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Recycle Bin") },
                                onClick = { 
                                    showOverflowMenu = false
                                    onRecycleBinClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = { 
                                    showOverflowMenu = false
                                    onSettingsClick()
                                }
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNoteClick("-1") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isSearchVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search notes...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenPadding)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                        }
                    )
                )
                Spacer(modifier = Modifier.height(Spacing.itemSpacing))
            }

            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (searchQuery.isEmpty()) {
                        // Empty state when no notes exist
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notes,
                                contentDescription = "No notes",
                                modifier = Modifier.size(Spacing.iconMassive),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(Spacing.screenPadding))
                            Text(
                                text = "No notes yet",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(Spacing.itemSpacing))
                            Text(
                                text = "Tap the + button to create your first note",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Empty state when search has no results
                        Text(
                            text = "No notes found for \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(Spacing.screenPadding),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredNotes) { note ->
                        SwipeableItemCard(
                            isPinned = note.isPinned,
                            onPin = { viewModel.togglePin(note.id, note.isPinned) },
                            onArchive = { viewModel.archiveNote(note.id) },
                            onDelete = { viewModel.moveToTrash(note.id) }
                        ) {
                            NoteListItem(
                                note = note,
                                onClick = { 
                                    onNoteClick(note.id)
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(Spacing.itemSpacing))
                    }
                }
            }
        }
    }
}

@Composable
fun NoteListItem(
    note: Note,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(Spacing.cornerMedium)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(Spacing.cornerMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.cardPadding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (note.isPinned) {
                        Icon(
                            imageVector = Icons.Filled.PushPin,
                            contentDescription = "Pinned",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Spacing.iconSmall)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.extraSmall))
                
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.itemSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        note.category?.let { category ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = Spacing.itemSpacing, vertical = Spacing.extraSmall)
                            ) {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        Text(
                            text = note.createdAt,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(instant: kotlinx.datetime.Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    
    return when {
        localDateTime.date == now.date -> "Today"
        localDateTime.date.toEpochDays() == now.date.toEpochDays() - 1 -> "Yesterday"
        else -> {
            val daysDiff = now.date.toEpochDays() - localDateTime.date.toEpochDays()
            when {
                daysDiff < 7 -> "$daysDiff days ago"
                daysDiff < 30 -> "${daysDiff / 7} week${if (daysDiff / 7 > 1) "s" else ""} ago"
                else -> "${daysDiff / 30} month${if (daysDiff / 30 > 1) "s" else ""} ago"
            }
        }
    }
}
