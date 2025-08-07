package com.allubie.nana.data.dao

import androidx.room.*
import com.allubie.nana.data.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface ScheduleDao {
    
    @Query("SELECT * FROM schedules ORDER BY date DESC, startTime ASC")
    fun getAllSchedulesFlow(): Flow<List<ScheduleEntity>>
    
    @Query("SELECT * FROM schedules WHERE date = :date ORDER BY startTime ASC")
    suspend fun getSchedulesForDate(date: LocalDate): List<ScheduleEntity>
    
    @Query("SELECT * FROM schedules WHERE date = :date ORDER BY startTime ASC")
    fun getSchedulesForDateFlow(date: LocalDate): Flow<List<ScheduleEntity>>
    
    @Query("SELECT * FROM schedules WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, startTime ASC")
    fun getSchedulesInRangeFlow(startDate: LocalDate, endDate: LocalDate): Flow<List<ScheduleEntity>>
    
    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getScheduleById(id: String): ScheduleEntity?
    
    @Query("SELECT * FROM schedules WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%' ORDER BY date DESC, startTime ASC")
    fun searchSchedulesFlow(searchQuery: String): Flow<List<ScheduleEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity)
    
    @Update
    suspend fun updateSchedule(schedule: ScheduleEntity)
    
    @Delete
    suspend fun deleteSchedule(schedule: ScheduleEntity)
    
    @Query("UPDATE schedules SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: String, completed: Boolean)
    
    @Query("UPDATE schedules SET isPinned = :pinned WHERE id = :id")
    suspend fun setPinned(id: String, pinned: Boolean)
    
    @Query("SELECT * FROM schedules WHERE date = :date AND isCompleted = 0 ORDER BY startTime ASC LIMIT 1")
    suspend fun getNextIncompleteScheduleForDate(date: LocalDate): ScheduleEntity?
}
