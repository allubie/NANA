package com.allubie.nana.data.repository

import android.content.Context
import android.util.Log
import com.allubie.nana.data.dao.ScheduleDao
import com.allubie.nana.data.entity.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ScheduleRepository(
    private val scheduleDao: ScheduleDao,
    private val context: Context? = null
) {
    
    fun getAllSchedules(): Flow<List<Schedule>> = scheduleDao.getAllSchedules()
    
    suspend fun getScheduleById(id: Long): Schedule? = scheduleDao.getScheduleById(id)
    
    fun getSchedulesByDate(date: String): Flow<List<Schedule>> = scheduleDao.getSchedulesForDate(date)
    
    suspend fun insertSchedule(schedule: Schedule): Long {
        val now = Clock.System.now().toString() // Use ISO instant format
        val scheduleId = scheduleDao.insertSchedule(schedule.copy(createdAt = now, updatedAt = now))
        
        // Schedule notification if reminder is enabled and context is available
        if (schedule.reminderEnabled && context != null) {
            try {
                Log.d("ScheduleRepository", "Scheduling notification for schedule: ${schedule.title}")
                Log.d("ScheduleRepository", "Schedule details - startDateTime: ${schedule.startDateTime}, reminderMinutes: ${schedule.reminderMinutesBefore}")
                
                val schedulerClass = Class.forName("com.allubie.nana.utils.NotificationScheduler")
                val constructor = schedulerClass.getConstructor(Context::class.java)
                val notificationScheduler = constructor.newInstance(context)
                
                val scheduleWithId = schedule.copy(id = scheduleId, createdAt = now, updatedAt = now)
                
                val method = schedulerClass.getMethod("scheduleNotificationForSchedule", Schedule::class.java)
                method.invoke(notificationScheduler, scheduleWithId)
                Log.d("ScheduleRepository", "Notification scheduling completed")
            } catch (e: Exception) {
                Log.e("ScheduleRepository", "Error scheduling notification", e)
            }
        } else {
            Log.d("ScheduleRepository", "Notification not scheduled - reminderEnabled: ${schedule.reminderEnabled}, context available: ${context != null}")
        }
        
        return scheduleId
    }
    
    suspend fun updateSchedule(schedule: Schedule) {
        val now = Clock.System.now().toString() // Use ISO instant format
        val updatedSchedule = schedule.copy(updatedAt = now)
        scheduleDao.updateSchedule(updatedSchedule)
        
        // Handle notifications if context is available
        if (context != null) {
            try {
                Log.d("ScheduleRepository", "Updating notification for schedule: ${schedule.title}")
                val schedulerClass = Class.forName("com.allubie.nana.utils.NotificationScheduler")
                val constructor = schedulerClass.getConstructor(Context::class.java)
                val notificationScheduler = constructor.newInstance(context)
                
                // Cancel old notification first
                val cancelMethod = schedulerClass.getMethod("cancelScheduleNotification", Long::class.javaPrimitiveType)
                cancelMethod.invoke(notificationScheduler, schedule.id)
                
                // Schedule new notification if reminder is enabled and not completed
                if (updatedSchedule.reminderEnabled && !updatedSchedule.isCompleted) {
                    val scheduleMethod = schedulerClass.getMethod("scheduleNotificationForSchedule", Schedule::class.java)
                    scheduleMethod.invoke(notificationScheduler, updatedSchedule)
                    Log.d("ScheduleRepository", "Notification rescheduled")
                } else {
                    Log.d("ScheduleRepository", "Notification not rescheduled - reminderEnabled: ${updatedSchedule.reminderEnabled}, completed: ${updatedSchedule.isCompleted}")
                }
            } catch (e: Exception) {
                Log.e("ScheduleRepository", "Error updating notification", e)
            }
        }
    }
    
    suspend fun deleteSchedule(schedule: Schedule) {
        scheduleDao.deleteSchedule(schedule)
        
        // Cancel notification if context is available
        if (context != null) {
            try {
                Log.d("ScheduleRepository", "Cancelling notification for schedule: ${schedule.title}")
                val schedulerClass = Class.forName("com.allubie.nana.utils.NotificationScheduler")
                val constructor = schedulerClass.getConstructor(Context::class.java)
                val notificationScheduler = constructor.newInstance(context)
                
                val cancelMethod = schedulerClass.getMethod("cancelScheduleNotification", Long::class.javaPrimitiveType)
                cancelMethod.invoke(notificationScheduler, schedule.id)
                Log.d("ScheduleRepository", "Notification cancelled")
            } catch (e: Exception) {
                Log.e("ScheduleRepository", "Error cancelling notification", e)
            }
        }
    }
    
    suspend fun toggleCompletionStatus(id: Long, isCompleted: Boolean) = scheduleDao.updateCompletionStatus(id, isCompleted)
    
    suspend fun togglePinStatus(id: Long, isPinned: Boolean) = scheduleDao.updatePinStatus(id, isPinned)

    suspend fun getAllSchedulesForExport(): List<Schedule> = scheduleDao.getAllSchedulesSync()
    
    suspend fun deleteAllSchedules() = scheduleDao.deleteAllSchedules()
    
    suspend fun getSchedulesWithReminders(): List<Schedule> = scheduleDao.getSchedulesWithReminders()
}
