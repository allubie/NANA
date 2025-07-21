package com.allubie.nana.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.allubie.nana.data.entity.Note
import com.allubie.nana.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()
    
    private val _archivedNotes = MutableStateFlow<List<Note>>(emptyList())
    val archivedNotes: StateFlow<List<Note>> = _archivedNotes.asStateFlow()
    
    private val _deletedNotes = MutableStateFlow<List<Note>>(emptyList())
    val deletedNotes: StateFlow<List<Note>> = _deletedNotes.asStateFlow()
    
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Note>>(emptyList())
    val searchResults: StateFlow<List<Note>> = _searchResults.asStateFlow()
    
    private val _currentNote = MutableStateFlow<Note?>(null)
    val currentNote: StateFlow<Note?> = _currentNote.asStateFlow()
    
    init {
        loadActiveNotes()
        loadCategories()
    }
    
    fun loadActiveNotes() {
        viewModelScope.launch {
            repository.getAllActiveNotes().collect {
                _notes.value = it
            }
        }
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collect {
                _categories.value = it
            }
        }
    }
    
    fun loadArchivedNotes() {
        viewModelScope.launch {
            repository.getArchivedNotes().collect {
                _archivedNotes.value = it
            }
        }
    }
    
    fun loadDeletedNotes() {
        viewModelScope.launch {
            repository.getDeletedNotes().collect {
                _deletedNotes.value = it
            }
        }
    }
    
    fun searchNotes(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            repository.searchNotes(query).collect {
                _searchResults.value = it
            }
        }
    }
    
    fun loadNotesByCategory(category: String) {
        viewModelScope.launch {
            repository.getNotesByCategory(category).collect {
                _notes.value = it
            }
        }
    }
    
    fun loadNoteById(id: Long) {
        viewModelScope.launch {
            _currentNote.value = repository.getNoteById(id)
        }
    }
    
    fun saveNote(note: Note) {
        viewModelScope.launch {
            if (note.id == 0L) {
                repository.insertNote(note)
            } else {
                repository.updateNote(note)
            }
        }
    }
    
    fun togglePinStatus(noteId: Long, isPinned: Boolean) {
        viewModelScope.launch {
            repository.togglePinStatus(noteId, isPinned)
        }
    }
    
    fun toggleArchiveStatus(noteId: Long, isArchived: Boolean) {
        viewModelScope.launch {
            repository.toggleArchiveStatus(noteId, isArchived)
        }
    }
    
    fun moveToTrash(noteId: Long) {
        viewModelScope.launch {
            repository.moveToTrash(noteId)
        }
    }
    
    fun restoreFromTrash(noteId: Long) {
        viewModelScope.launch {
            repository.restoreFromTrash(noteId)
        }
    }
    
    fun permanentlyDeleteAllDeletedNotes() {
        viewModelScope.launch {
            repository.permanentlyDeleteAllDeletedNotes()
        }
    }
    
    fun unarchiveNote(noteId: Long) {
        viewModelScope.launch {
            repository.toggleArchiveStatus(noteId, false)
        }
    }
    
    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            repository.moveToTrash(noteId)
        }
    }
    
    fun restoreNote(noteId: Long) {
        viewModelScope.launch {
            repository.restoreFromTrash(noteId)
        }
    }
    
    fun permanentlyDeleteNote(noteId: Long) {
        viewModelScope.launch {
            repository.permanentlyDeleteNote(noteId)
        }
    }
    
    fun emptyRecycleBin() {
        viewModelScope.launch {
            repository.permanentlyDeleteAllDeletedNotes()
        }
    }
}

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
