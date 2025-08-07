package com.allubie.nana.data.repository

import com.allubie.nana.data.dao.NoteDao
import com.allubie.nana.data.entity.NoteEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class NoteRepository(private val noteDao: NoteDao) {
    
    fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getActiveNotesFlow()
    
    fun getArchivedNotes(): Flow<List<NoteEntity>> = noteDao.getArchivedNotesFlow()
    
    fun getDeletedNotes(): Flow<List<NoteEntity>> = noteDao.getDeletedNotesFlow()
    
    fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotesFlow(query)
    
    suspend fun getNoteById(id: String): NoteEntity? = noteDao.getNoteById(id)
    
    suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)
    
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
    
    suspend fun moveToTrash(id: String) = noteDao.moveToTrash(id)
    
    suspend fun restoreFromTrash(id: String) = noteDao.restoreFromTrash(id)
    
    suspend fun archiveNote(id: String) = noteDao.setArchived(id, true)
    
    suspend fun unarchiveNote(id: String) = noteDao.setArchived(id, false)
    
    suspend fun pinNote(id: String) = noteDao.setPinned(id, true)
    
    suspend fun unpinNote(id: String) = noteDao.setPinned(id, false)
    
    suspend fun emptyTrash() = noteDao.emptyTrash()
    
    suspend fun createNote(title: String, content: String, category: String? = null): NoteEntity {
        val note = NoteEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content,
            category = category
        )
        insertNote(note)
        return note
    }
}
