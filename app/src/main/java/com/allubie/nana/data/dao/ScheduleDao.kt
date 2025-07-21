package com.allubie.nana.data.dao

import androidx.room.*
import com.allubie.nana.data.entity.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    
    @Query("SELECT * FROM schedules ORDER BY isPinned DESC, startDateTime ASC")
    fun getAllSchedules(): Flow<List<Schedule>>
    
    @Query("SELECT * FROM schedules ORDER BY isPinned DESC, startDateTime ASC")
    suspend fun getAllSchedulesSync(): List<Schedule>
    
    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getScheduleById(id: Long): Schedule?
    
    @Query("SELECT * FROM schedules WHERE DATE(startDateTime) = :date ORDER BY startDateTime ASC")
    fun getSchedulesForDate(date: String): Flow<List<Schedule>>
    
    @Query("SELECT * FROM schedules WHERE startDateTime >= :start AND startDateTime <= :end ORDER BY startDateTime ASC")
    fun getSchedulesInDateRange(start: String, end: String): Flow<List<Schedule>>
    
    @Query("SELECT * FROM schedules WHERE category = :category")
    fun getSchedulesByCategory(category: String): Flow<List<Schedule>>
    
    @Query("SELECT * FROM schedules WHERE isCompleted = 0 AND startDateTime >= datetime('now')")
    fun getUpcomingSchedules(): Flow<List<Schedule>>
    
    @Insert
    suspend fun insertSchedule(schedule: Schedule): Long
    
    @Update
    suspend fun updateSchedule(schedule: Schedule)
    
    @Delete
    suspend fun deleteSchedule(schedule: Schedule)
    
    @Query("UPDATE schedules SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePinStatus(id: Long, isPinned: Boolean)
    
    @Query("UPDATE schedules SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean)
    
    @Query("SELECT DISTINCT category FROM schedules WHERE category != ''")
    fun getAllCategories(): Flow<List<String>>
    
    @Query("DELETE FROM schedules")
    suspend fun deleteAllSchedules()
    
    @Query("SELECT * FROM schedules WHERE reminderEnabled = 1 AND isCompleted = 0 AND startDateTime > datetime('now')")
    suspend fun getSchedulesWithReminders(): List<Schedule>
}
