package com.allubie.nana.data.dao

import androidx.room.*
import com.allubie.nana.data.entity.RoutineEntity
import com.allubie.nana.data.entity.RoutineCompletionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface RoutineDao {
    
    @Query("SELECT * FROM routines WHERE isActive = 1 ORDER BY isPinned DESC, createdAt DESC")
    fun getActiveRoutinesFlow(): Flow<List<RoutineEntity>>
    
    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getRoutineById(id: String): RoutineEntity?
    
    @Query("SELECT * FROM routine_completions WHERE routineId = :routineId AND completionDate = :date")
    suspend fun getCompletionForDate(routineId: String, date: LocalDate): RoutineCompletionEntity?
    
    @Query("SELECT * FROM routine_completions WHERE routineId = :routineId ORDER BY completionDate DESC")
    suspend fun getCompletionsForRoutine(routineId: String): List<RoutineCompletionEntity>
    
    @Query("SELECT * FROM routine_completions WHERE completionDate = :date")
    suspend fun getCompletionsForDate(date: LocalDate): List<RoutineCompletionEntity>
    
    @Query("SELECT COUNT(*) FROM routine_completions WHERE routineId = :routineId AND completionDate >= :startDate ORDER BY completionDate DESC")
    suspend fun getStreakCount(routineId: String, startDate: LocalDate): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: RoutineCompletionEntity)
    
    @Update
    suspend fun updateRoutine(routine: RoutineEntity)
    
    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)
    
    @Delete
    suspend fun deleteCompletion(completion: RoutineCompletionEntity)
    
    @Query("UPDATE routines SET isPinned = :pinned WHERE id = :id")
    suspend fun setPinned(id: String, pinned: Boolean)
    
    @Query("DELETE FROM routine_completions WHERE routineId = :routineId AND completionDate = :date")
    suspend fun removeCompletionForDate(routineId: String, date: LocalDate)
}
