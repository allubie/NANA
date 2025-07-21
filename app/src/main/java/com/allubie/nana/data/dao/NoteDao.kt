package com.allubie.nana.data.dao

import androidx.room.*
import com.allubie.nana.data.entity.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    
    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllActiveNotes(): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedNotes(): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY updatedAt DESC")
    fun getDeletedNotes(): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?
    
    @Query("SELECT * FROM notes WHERE (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') AND isDeleted = 0")
    fun searchNotes(query: String): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE category = :category AND isDeleted = 0 AND isArchived = 0")
    fun getNotesByCategory(category: String): Flow<List<Note>>
    
    @Insert
    suspend fun insertNote(note: Note): Long
    
    @Update
    suspend fun updateNote(note: Note)
    
    @Delete
    suspend fun deleteNote(note: Note)
    
    @Query("UPDATE notes SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePinStatus(id: Long, isPinned: Boolean)
    
    @Query("UPDATE notes SET isArchived = :isArchived WHERE id = :id")
    suspend fun updateArchiveStatus(id: Long, isArchived: Boolean)
    
    @Query("UPDATE notes SET isDeleted = :isDeleted WHERE id = :id")
    suspend fun updateDeleteStatus(id: Long, isDeleted: Boolean)
    
    @Query("DELETE FROM notes WHERE isDeleted = 1")
    suspend fun permanentlyDeleteAllDeletedNotes()
    
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun permanentlyDeleteNote(id: Long)
    
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    suspend fun getAllNotes(): List<Note>
    
    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()
    
    @Query("SELECT DISTINCT category FROM notes WHERE category IS NOT NULL AND category != ''")
    fun getAllCategories(): Flow<List<String>>
}
