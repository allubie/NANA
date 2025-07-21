package com.allubie.nana.data.repository

import android.content.Context
import android.util.Log
import com.allubie.nana.data.dao.RoutineDao
import com.allubie.nana.data.entity.Routine
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class RoutineRepository(
    private val routineDao: RoutineDao,
    private val context: Context? = null
) {
    
    fun getAllRoutines(): Flow<List<Routine>> = routineDao.getAllActiveRoutines()
    
    suspend fun getRoutineById(id: Long): Routine? = routineDao.getRoutineById(id)
    
    suspend fun insertRoutine(routine: Routine): Long {
        val now = Clock.System.now().toString() // Use ISO instant format
        val routineId = routineDao.insertRoutine(routine.copy(createdAt = now))
        
        // Schedule notification if reminder is enabled and context is available
        if (routine.reminderEnabled && routine.isActive && context != null) {
            try {
                // Create the scheduler class directly to avoid import issues
                val schedulerClass = Class.forName("com.allubie.nana.utils.NotificationScheduler")
                val constructor = schedulerClass.getConstructor(Context::class.java)
                val notificationScheduler = constructor.newInstance(context)
                
                val routineWithId = routine.copy(id = routineId, createdAt = now)
                val method = schedulerClass.getMethod("scheduleNotificationForRoutine", Routine::class.java)
                method.invoke(notificationScheduler, routineWithId)
            } catch (e: Exception) {
                Log.e("RoutineRepository", "Error scheduling notification", e)
            }
        }
        
        return routineId
    }
    
    suspend fun updateRoutine(routine: Routine) {
        routineDao.updateRoutine(routine)
        
        // Handle notifications if context is available
        if (context != null) {
            try {
                // Create the scheduler class directly to avoid import issues
                val schedulerClass = Class.forName("com.allubie.nana.utils.NotificationScheduler")
                val constructor = schedulerClass.getConstructor(Context::class.java)
                val notificationScheduler = constructor.newInstance(context)
                
                // Cancel old notification first
                val cancelMethod = schedulerClass.getMethod("cancelRoutineNotification", Long::class.javaPrimitiveType)
                cancelMethod.invoke(notificationScheduler, routine.id)
                
                // Schedule new notification if reminder is enabled and routine is active
                if (routine.reminderEnabled && routine.isActive) {
                    val scheduleMethod = schedulerClass.getMethod("scheduleNotificationForRoutine", Routine::class.java)
                    scheduleMethod.invoke(notificationScheduler, routine)
                }
            } catch (e: Exception) {
                Log.e("RoutineRepository", "Error updating notification", e)
            }
        }
    }
    
    suspend fun deleteRoutine(routine: Routine) {
        routineDao.deleteRoutine(routine)
        
        // Cancel notification if context is available
        if (context != null) {
            try {
                // Create the scheduler class directly to avoid import issues
                val schedulerClass = Class.forName("com.allubie.nana.utils.NotificationScheduler")
                val constructor = schedulerClass.getConstructor(Context::class.java)
                val notificationScheduler = constructor.newInstance(context)
                
                val cancelMethod = schedulerClass.getMethod("cancelRoutineNotification", Long::class.javaPrimitiveType)
                cancelMethod.invoke(notificationScheduler, routine.id)
            } catch (e: Exception) {
                Log.e("RoutineRepository", "Error cancelling notification", e)
            }
        }
    }
    
    suspend fun toggleCompletionStatus(id: Long, isCompleted: Boolean) {
        val completedAt = if (isCompleted) {
            Clock.System.now().toString() // Use ISO instant format
        } else null
        routineDao.updateCompletionStatus(id, isCompleted, completedAt)
    }
    
    suspend fun togglePin(id: Long, isPinned: Boolean) {
        routineDao.updatePinStatus(id, isPinned)
    }
    
    suspend fun sendCompletionFeedback(routineTitle: String) {
        if (context != null) {
            try {
                // Create the scheduler class directly to avoid import issues
                val schedulerClass = Class.forName("com.allubie.nana.utils.NotificationScheduler")
                val constructor = schedulerClass.getConstructor(Context::class.java)
                val notificationScheduler = constructor.newInstance(context)
                
                // TODO: Calculate streak count from database
                val streakCount = 1 // For now, just use 1
                
                val method = schedulerClass.getMethod("sendRoutineCompletionNotification", String::class.java, Int::class.javaPrimitiveType)
                method.invoke(notificationScheduler, routineTitle, streakCount)
            } catch (e: Exception) {
                Log.e("RoutineRepository", "Error sending completion feedback", e)
            }
        }
    }

    suspend fun getAllRoutinesForExport(): List<Routine> = routineDao.getAllRoutines()
    
    suspend fun deleteAllRoutines() = routineDao.deleteAllRoutines()
    
    suspend fun getRoutinesWithReminders(): List<Routine> = routineDao.getRoutinesWithReminders()
}
