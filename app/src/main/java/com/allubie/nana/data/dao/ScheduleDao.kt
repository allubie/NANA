package com.allubie.nana.data.dao

import androidx.room.*
import com.allubie.nana.data.entity.ScheduleEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleEventDao {
    @Query("SELECT * FROM schedule_events ORDER BY dateMillis")
    fun getAllEvents(): Flow<List<ScheduleEventEntity>>

    @Query("SELECT * FROM schedule_events WHERE dateMillis BETWEEN :startMillis AND :endMillis ORDER BY startTime")
    fun getEventsInDateRange(startMillis: Long, endMillis: Long): Flow<List<ScheduleEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ScheduleEventEntity)

    @Update
    suspend fun updateEvent(event: ScheduleEventEntity)

    @Delete
    suspend fun deleteEvent(event: ScheduleEventEntity)
}