package com.allubie.nana.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allubie.nana.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {
    
    val notes: StateFlow<List<Note>> = noteRepository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun addNote(title: String, content: String, color: NoteColor) {
        if (title.isBlank()) return
        
        viewModelScope.launch {
            val note = Note(
                title = title,
                content = content,
                color = color
            )
            noteRepository.insertNote(note)
        }
    }
    
    fun updateNote(note: Note, title: String, content: String, color: NoteColor) {
        if (title.isBlank()) return
        
        viewModelScope.launch {
            val updatedNote = note.copy(
                title = title,
                content = content,
                color = color
            )
            noteRepository.updateNote(updatedNote)
        }
    }
    
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }
}