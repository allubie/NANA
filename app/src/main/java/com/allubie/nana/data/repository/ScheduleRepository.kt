package com.allubie.nana.data.repository

import com.allubie.nana.data.dao.ScheduleDao
import com.allubie.nana.data.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.UUID

class ScheduleRepository(private val scheduleDao: ScheduleDao) {
    
    fun getAllSchedules(): Flow<List<ScheduleEntity>> = scheduleDao.getAllSchedulesFlow()
    
    fun getSchedulesForDate(date: LocalDate): Flow<List<ScheduleEntity>> = scheduleDao.getSchedulesForDateFlow(date)
    
    fun getSchedulesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<ScheduleEntity>> = 
        scheduleDao.getSchedulesInRangeFlow(startDate, endDate)
    
    fun searchSchedules(query: String): Flow<List<ScheduleEntity>> = scheduleDao.searchSchedulesFlow(query)
    
    suspend fun getScheduleById(id: String): ScheduleEntity? = scheduleDao.getScheduleById(id)
    
    suspend fun getSchedulesForDateSync(date: LocalDate): List<ScheduleEntity> = scheduleDao.getSchedulesForDate(date)
    
    suspend fun insertSchedule(schedule: ScheduleEntity) = scheduleDao.insertSchedule(schedule)
    
    suspend fun updateSchedule(schedule: ScheduleEntity) = scheduleDao.updateSchedule(schedule)
    
    suspend fun deleteSchedule(schedule: ScheduleEntity) = scheduleDao.deleteSchedule(schedule)
    
    suspend fun toggleCompletion(id: String) {
        val schedule = getScheduleById(id)
        schedule?.let {
            scheduleDao.setCompleted(id, !it.isCompleted)
        }
    }
    
    suspend fun pinSchedule(id: String) = scheduleDao.setPinned(id, true)
    
    suspend fun unpinSchedule(id: String) = scheduleDao.setPinned(id, false)
    
    suspend fun getNextIncompleteSchedule(date: LocalDate): ScheduleEntity? = 
        scheduleDao.getNextIncompleteScheduleForDate(date)
    
    suspend fun getTodaysSchedules(): List<ScheduleEntity> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return getSchedulesForDateSync(today)
    }
    
    suspend fun createSchedule(
        title: String,
        description: String,
        startTime: LocalTime,
        endTime: LocalTime,
        date: LocalDate,
        location: String? = null,
        category: String,
        isRecurring: Boolean = false,
        recurringPattern: String? = null,
        reminderMinutes: Int = 15
    ): ScheduleEntity {
        val schedule = ScheduleEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            date = date,
            location = location,
            category = category,
            isRecurring = isRecurring,
            recurringPattern = recurringPattern,
            reminderMinutes = reminderMinutes
        )
        insertSchedule(schedule)
        return schedule
    }
}
