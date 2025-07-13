package com.allubie.nana.data.repository

import com.allubie.nana.data.dao.NoteDao
import com.allubie.nana.data.entity.NoteEntity
import com.allubie.nana.ui.notes.Note
import com.allubie.nana.ui.notes.NoteColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository(private val noteDao: NoteDao) {
    
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes().map { noteEntities ->
        noteEntities.map { entity -> entity.toNote() }
    }
    
    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note.toEntity())
    }
    
    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toEntity())
    }
    
    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toEntity())
    }
    
    // Extension functions to convert between domain and data layer
    private fun Note.toEntity(): NoteEntity {
        return NoteEntity(
            id = id,
            title = title,
            content = content,
            timestamp = timestamp,
            colorName = color.name
        )
    }
    
    private fun NoteEntity.toNote(): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            timestamp = timestamp,
            color = try { NoteColor.valueOf(colorName) } catch (e: Exception) { NoteColor.Default }
        )
    }
}