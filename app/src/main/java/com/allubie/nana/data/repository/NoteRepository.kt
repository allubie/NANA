package com.allubie.nana.data.repository

import com.allubie.nana.data.dao.NoteDao
import com.allubie.nana.data.entity.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class NoteRepository(private val noteDao: NoteDao) {
    
    fun getAllActiveNotes(): Flow<List<Note>> = noteDao.getAllActiveNotes()
    
    fun getArchivedNotes(): Flow<List<Note>> = noteDao.getArchivedNotes()
    
    fun getDeletedNotes(): Flow<List<Note>> = noteDao.getDeletedNotes()
    
    suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)
    
    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)
    
    fun getNotesByCategory(category: String): Flow<List<Note>> = noteDao.getNotesByCategory(category)
    
    fun getAllCategories(): Flow<List<String>> = noteDao.getAllCategories()
    
    suspend fun insertNote(note: Note): Long {
        val now = Clock.System.now().toString() // Use ISO instant format
        return noteDao.insertNote(note.copy(createdAt = now, updatedAt = now))
    }
    
    suspend fun updateNote(note: Note) {
        val now = Clock.System.now().toString() // Use ISO instant format
        noteDao.updateNote(note.copy(updatedAt = now))
    }
    
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    
    suspend fun togglePinStatus(id: Long, isPinned: Boolean) = noteDao.updatePinStatus(id, isPinned)
    
    suspend fun toggleArchiveStatus(id: Long, isArchived: Boolean) = noteDao.updateArchiveStatus(id, isArchived)
    
    suspend fun moveToTrash(id: Long) = noteDao.updateDeleteStatus(id, true)
    
    suspend fun restoreFromTrash(id: Long) = noteDao.updateDeleteStatus(id, false)
    
    suspend fun getAllNotesForExport(): List<Note> = noteDao.getAllNotes()
    
    suspend fun deleteAllNotes() = noteDao.deleteAllNotes()
    
    suspend fun permanentlyDeleteAllDeletedNotes() = noteDao.permanentlyDeleteAllDeletedNotes()
    
    suspend fun permanentlyDeleteNote(id: Long) = noteDao.permanentlyDeleteNote(id)
}
