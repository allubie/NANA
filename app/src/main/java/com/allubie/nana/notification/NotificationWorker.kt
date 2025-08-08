package com.allubie.nana.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.datetime.*
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        const val KEY_NOTIFICATION_TYPE = "notification_type"
        const val KEY_TITLE = "title"
        const val KEY_DESCRIPTION = "description"
        const val KEY_TIME = "time"
        const val KEY_MINUTES_BEFORE = "minutes_before"
        
        const val TYPE_ROUTINE = "routine"
        const val TYPE_SCHEDULE = "schedule"
        const val TYPE_SCHEDULE_START = "schedule_start"
        const val TYPE_EXPENSE = "expense"
        
        fun scheduleRoutineReminder(
            context: Context,
            routineId: String,
            title: String,
            description: String,
            reminderTime: LocalTime
        ) {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val today = now.date
            val reminderDateTime = today.atTime(reminderTime)
            val reminderInstant = reminderDateTime.toInstant(TimeZone.currentSystemDefault())
            
            // If the time has passed today, schedule for tomorrow
            val targetInstant = if (reminderInstant <= Clock.System.now()) {
                today.plus(1, DateTimeUnit.DAY).atTime(reminderTime).toInstant(TimeZone.currentSystemDefault())
            } else {
                reminderInstant
            }
            
            val delay = targetInstant.epochSeconds - Clock.System.now().epochSeconds
            
            val data = Data.Builder()
                .putString(KEY_NOTIFICATION_TYPE, TYPE_ROUTINE)
                .putString(KEY_TITLE, title)
                .putString(KEY_DESCRIPTION, description)
                .build()
            
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .addTag("routine_$routineId")
                .build()
            
            WorkManager.getInstance(context).enqueue(workRequest)
        }
        
        fun scheduleScheduleReminder(
            context: Context,
            scheduleId: String,
            title: String,
            scheduledTime: LocalDateTime,
            minutesBefore: Int = 15
        ) {
            val scheduledInstant = scheduledTime.toInstant(TimeZone.currentSystemDefault())
            val reminderInstant = scheduledInstant.minus(minutesBefore.toLong(), DateTimeUnit.MINUTE, TimeZone.currentSystemDefault())
            val now = Clock.System.now()
            
            // Only schedule if the reminder time is in the future
            if (reminderInstant > now) {
                val delay = reminderInstant.epochSeconds - now.epochSeconds
                
                val data = Data.Builder()
                    .putString(KEY_NOTIFICATION_TYPE, TYPE_SCHEDULE)
                    .putString(KEY_TITLE, title)
                    .putString(KEY_TIME, scheduledTime.time.toString())
                    .putInt(KEY_MINUTES_BEFORE, minutesBefore)
                    .build()
                
                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInputData(data)
                    .setInitialDelay(delay, TimeUnit.SECONDS)
                    .addTag("schedule_$scheduleId")
                    .build()
                
                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
        
        fun scheduleScheduleStartAlert(
            context: Context,
            scheduleId: String,
            title: String,
            scheduledTime: LocalDateTime
        ) {
            val scheduledInstant = scheduledTime.toInstant(TimeZone.currentSystemDefault())
            val now = Clock.System.now()
            
            // Only schedule if the start time is in the future
            if (scheduledInstant > now) {
                val delay = scheduledInstant.epochSeconds - now.epochSeconds
                
                val data = Data.Builder()
                    .putString(KEY_NOTIFICATION_TYPE, TYPE_SCHEDULE_START)
                    .putString(KEY_TITLE, title)
                    .putString(KEY_TIME, scheduledTime.time.toString())
                    .build()
                
                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInputData(data)
                    .setInitialDelay(delay, TimeUnit.SECONDS)
                    .addTag("schedule_start_$scheduleId")
                    .build()
                
                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
        
        fun cancelNotifications(context: Context, tag: String) {
            WorkManager.getInstance(context).cancelAllWorkByTag(tag)
        }
    }
    
    override suspend fun doWork(): Result {
        return try {
            val notificationService = NotificationService(applicationContext)
            
            when (inputData.getString(KEY_NOTIFICATION_TYPE)) {
                TYPE_ROUTINE -> {
                    val title = inputData.getString(KEY_TITLE) ?: "Routine Reminder"
                    val description = inputData.getString(KEY_DESCRIPTION) ?: ""
                    notificationService.sendRoutineReminder(title, description)
                }
                
                TYPE_SCHEDULE -> {
                    val title = inputData.getString(KEY_TITLE) ?: "Schedule Reminder"
                    val time = inputData.getString(KEY_TIME) ?: ""
                    val minutesBefore = inputData.getInt(KEY_MINUTES_BEFORE, 15)
                    notificationService.sendScheduleReminder(title, time, minutesBefore)
                }
                
                TYPE_SCHEDULE_START -> {
                    val title = inputData.getString(KEY_TITLE) ?: "Schedule Started"
                    val time = inputData.getString(KEY_TIME) ?: ""
                    notificationService.sendScheduleStartAlert(title, time)
                }
                
                TYPE_EXPENSE -> {
                    val message = inputData.getString(KEY_DESCRIPTION) ?: "Budget alert"
                    notificationService.sendExpenseAlert(message)
                }
            }
            
            Result.success()
        } catch (exception: Exception) {
            Result.failure()
        }
    }
}
