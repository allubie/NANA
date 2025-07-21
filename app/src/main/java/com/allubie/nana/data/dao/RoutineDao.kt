package com.allubie.nana.data.dao

import androidx.room.*
import com.allubie.nana.data.entity.Routine
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    
    @Query("SELECT * FROM routines WHERE isActive = 1 ORDER BY isPinned DESC, time ASC")
    fun getAllActiveRoutines(): Flow<List<Routine>>
    
    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getRoutineById(id: Long): Routine?
    
    @Query("SELECT * FROM routines WHERE category = :category AND isActive = 1")
    fun getRoutinesByCategory(category: String): Flow<List<Routine>>
    
    @Query("SELECT * FROM routines WHERE isCompleted = 0 AND isActive = 1")
    fun getIncompleteRoutines(): Flow<List<Routine>>
    
    @Insert
    suspend fun insertRoutine(routine: Routine): Long
    
    @Update
    suspend fun updateRoutine(routine: Routine)
    
    @Delete
    suspend fun deleteRoutine(routine: Routine)
    
    @Query("UPDATE routines SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePinStatus(id: Long, isPinned: Boolean)
    
    @Query("UPDATE routines SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean, completedAt: String?)
    
    @Query("UPDATE routines SET streak = :streak WHERE id = :id")
    suspend fun updateStreak(id: Long, streak: Int)
    
    @Query("SELECT DISTINCT category FROM routines WHERE category != '' AND isActive = 1")
    fun getAllCategories(): Flow<List<String>>
    
    @Query("SELECT * FROM routines ORDER BY isPinned DESC, time ASC")
    suspend fun getAllRoutines(): List<Routine>
    
    @Query("DELETE FROM routines")
    suspend fun deleteAllRoutines()
    
    @Query("SELECT * FROM routines WHERE time IS NOT NULL AND reminderEnabled = 1 AND isActive = 1")
    suspend fun getRoutinesWithReminders(): List<Routine>
}
