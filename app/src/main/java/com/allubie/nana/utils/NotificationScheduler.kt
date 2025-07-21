package com.allubie.nana.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.allubie.nana.R
import com.allubie.nana.data.entity.Schedule
import com.allubie.nana.data.entity.Routine
import com.allubie.nana.receivers.NotificationReceiver
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Handles notification scheduling for schedules and routines using AlarmManager
 */
class NotificationScheduler(private val context: Context) {
    
    companion object {
        private const val TAG = "NANA_NOTIFICATION"
        private const val CHANNEL_ID = "NANA_NOTIFICATIONS"
        private const val CHANNEL_NAME = "NANA Schedule & Routine Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for scheduled events and routines"
    }
    
    private val alarmManager: AlarmManager = 
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    private val notificationManager: NotificationManager = 
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
        Log.d(TAG, "NotificationScheduler initialized")
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created: $CHANNEL_ID")
        }
    }
    
    /**
     * Schedule notification for a schedule event
     */
    fun scheduleNotificationForSchedule(schedule: Schedule) {
        try {
            Log.d(TAG, "=== Scheduling Notification for Schedule ===")
            Log.d(TAG, "Schedule ID: ${schedule.id}, Title: '${schedule.title}'")
            Log.d(TAG, "Start DateTime: '${schedule.startDateTime}'")
            Log.d(TAG, "Reminder Enabled: ${schedule.reminderEnabled}")
            Log.d(TAG, "Reminder Minutes Before: ${schedule.reminderMinutesBefore}")
            
            // Parse the start datetime
            val startInstant = Instant.parse(schedule.startDateTime)
            val startTime = ZonedDateTime.ofInstant(
                startInstant.toJavaInstant(), 
                ZoneId.systemDefault()
            )
            
            val currentTime = ZonedDateTime.now()
            
            Log.d(TAG, "Start Time: $startTime")
            Log.d(TAG, "Current Time: $currentTime")
            
            // Schedule reminder notification if enabled
            if (schedule.reminderEnabled && schedule.reminderMinutesBefore > 0) {
                val reminderTime = startTime.minusMinutes(schedule.reminderMinutesBefore.toLong())
                
                Log.d(TAG, "Reminder Time: $reminderTime")
                Log.d(TAG, "Minutes before event: ${schedule.reminderMinutesBefore}")
                
                if (reminderTime.isAfter(currentTime)) {
                    scheduleReminderNotification(schedule, reminderTime, true)
                } else {
                    Log.d(TAG, "WARNING: Reminder time is in the past, not scheduling reminder")
                }
            }
            
            // Also schedule notification for the actual event start time
            if (startTime.isAfter(currentTime)) {
                scheduleReminderNotification(schedule, startTime, false)
            } else {
                Log.d(TAG, "WARNING: Schedule time is in the past, not scheduling notification")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Error scheduling notification for schedule ${schedule.id}: ${e.message}", e)
        }
    }
    
    /**
     * Helper method to schedule reminder or event notifications
     */
    private fun scheduleReminderNotification(
        schedule: Schedule, 
        triggerTime: ZonedDateTime, 
        isReminder: Boolean
    ) {
        try {
            val triggerTimeMillis = triggerTime.toInstant().toEpochMilli()
            
            Log.d(TAG, "Scheduling ${if (isReminder) "reminder" else "event"} notification")
            Log.d(TAG, "Trigger Time (millis): $triggerTimeMillis")
            Log.d(TAG, "Time until notification: ${(triggerTimeMillis - System.currentTimeMillis()) / 1000} seconds")
            
            val title = if (isReminder) {
                "Reminder: ${schedule.title}"
            } else {
                schedule.title
            }
            
            val description = if (isReminder) {
                "Starting in ${schedule.reminderMinutesBefore} minutes: ${schedule.description}"
            } else {
                schedule.description
            }
            
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra(NotificationReceiver.EXTRA_TYPE, NotificationReceiver.TYPE_SCHEDULE)
                putExtra(NotificationReceiver.EXTRA_ITEM_ID, schedule.id)
                putExtra(NotificationReceiver.EXTRA_TITLE, title)
                putExtra(NotificationReceiver.EXTRA_DESCRIPTION, description)
            }
            
            // Use different request codes for reminder vs event notifications
            val requestCode = if (isReminder) {
                schedule.id.toInt() + 50000 // Offset for reminders
            } else {
                schedule.id.toInt()
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Schedule the alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            }
            
            Log.d(TAG, "SUCCESS: ${if (isReminder) "Reminder" else "Event"} notification scheduled for schedule ID: ${schedule.id}")
            
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to schedule ${if (isReminder) "reminder" else "event"} notification: ${e.message}", e)
        }
    }
    
    /**
     * Schedule notification for a routine
     */
    fun scheduleNotificationForRoutine(routine: Routine) {
        try {
            Log.d(TAG, "=== Scheduling Notification for Routine ===")
            Log.d(TAG, "Routine ID: ${routine.id}, Title: '${routine.title}'")
            Log.d(TAG, "Time: '${routine.time}'")
            Log.d(TAG, "Reminder Enabled: ${routine.reminderEnabled}")
            Log.d(TAG, "Reminder Minutes Before: ${routine.reminderMinutesBefore}")
            
            // Skip if routine doesn't have a time set
            if (routine.time == null) {
                Log.d(TAG, "WARNING: Routine has no time set, skipping notification")
                return
            }
            
            // Parse the routine time (HH:mm format)
            val timeParts = routine.time.split(":")
            if (timeParts.size != 2) {
                Log.e(TAG, "ERROR: Invalid time format: ${routine.time}")
                return
            }
            
            val hour = timeParts[0].toIntOrNull()
            val minute = timeParts[1].toIntOrNull()
            
            if (hour == null || minute == null) {
                Log.e(TAG, "ERROR: Could not parse time: ${routine.time}")
                return
            }
            
            // Create a ZonedDateTime for today at the routine time
            val currentTime = ZonedDateTime.now()
            var routineTime = currentTime
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0)
            
            // If the time has already passed today, schedule for tomorrow
            if (routineTime.isBefore(currentTime) || routineTime.isEqual(currentTime)) {
                routineTime = routineTime.plusDays(1)
                Log.d(TAG, "Time has passed today, scheduling for tomorrow")
            }
            
            Log.d(TAG, "Routine Time: $routineTime")
            Log.d(TAG, "Current Time: $currentTime")
            
            // Schedule reminder notification if enabled
            if (routine.reminderEnabled && routine.reminderMinutesBefore > 0) {
                val reminderTime = routineTime.minusMinutes(routine.reminderMinutesBefore.toLong())
                
                Log.d(TAG, "Reminder Time: $reminderTime")
                Log.d(TAG, "Minutes before routine: ${routine.reminderMinutesBefore}")
                
                if (reminderTime.isAfter(currentTime)) {
                    scheduleRoutineReminderNotification(routine, reminderTime, true)
                } else {
                    Log.d(TAG, "WARNING: Reminder time is in the past, not scheduling reminder")
                }
            }
            
            // Also schedule notification for the actual routine time
            scheduleRoutineReminderNotification(routine, routineTime, false)
            
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Error scheduling notification for routine ${routine.id}: ${e.message}", e)
        }
    }
    
    /**
     * Helper method to schedule reminder or routine notifications
     */
    private fun scheduleRoutineReminderNotification(
        routine: Routine, 
        triggerTime: ZonedDateTime, 
        isReminder: Boolean
    ) {
        try {
            val triggerTimeMillis = triggerTime.toInstant().toEpochMilli()
            
            Log.d(TAG, "Scheduling ${if (isReminder) "reminder" else "routine"} notification")
            Log.d(TAG, "Trigger Time (millis): $triggerTimeMillis")
            Log.d(TAG, "Time until notification: ${(triggerTimeMillis - System.currentTimeMillis()) / 1000} seconds")
            
            val title = if (isReminder) {
                "Reminder: ${routine.title}"
            } else {
                routine.title
            }
            
            val description = if (isReminder) {
                "Starting in ${routine.reminderMinutesBefore} minutes: ${routine.description}"
            } else {
                routine.description
            }
            
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra(NotificationReceiver.EXTRA_TYPE, NotificationReceiver.TYPE_ROUTINE)
                putExtra(NotificationReceiver.EXTRA_ITEM_ID, routine.id)
                putExtra(NotificationReceiver.EXTRA_TITLE, title)
                putExtra(NotificationReceiver.EXTRA_DESCRIPTION, description)
            }
            
            // Use different request codes for reminder vs routine notifications
            val requestCode = if (isReminder) {
                routine.id.toInt() + 60000 // Offset for routine reminders
            } else {
                routine.id.toInt() + 10000 // Offset for routines
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Schedule the alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            }
            
            Log.d(TAG, "SUCCESS: ${if (isReminder) "Reminder" else "Routine"} notification scheduled for routine ID: ${routine.id}")
            
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to schedule ${if (isReminder) "reminder" else "routine"} notification: ${e.message}", e)
        }
    }
    
    /**
     * Snooze a routine notification for a specified duration
     */
    fun snoozeRoutineNotification(routineId: Long, snoozeMinutes: Int = 10) {
        try {
            Log.d(TAG, "=== Snoozing Routine Notification ===")
            Log.d(TAG, "Routine ID: $routineId, Snooze Minutes: $snoozeMinutes")
            
            // Cancel current notification
            cancelRoutineNotification(routineId)
            
            // Schedule new notification after snooze duration
            val snoozeTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000)
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("type", "routine_snooze")
                putExtra("routine_id", routineId)
                putExtra("title", "Routine Reminder (Snoozed)")
                putExtra("message", "Your routine is ready to be completed")
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                (routineId.toInt() + 70000), // Different request code for snooze
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                snoozeTime,
                pendingIntent
            )
            
            Log.d(TAG, "SUCCESS: Routine notification snoozed for $snoozeMinutes minutes")
            
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Error snoozing routine notification $routineId: ${e.message}", e)
        }
    }
    
    /**
     * Snooze a schedule notification for a specified duration
     */
    fun snoozeScheduleNotification(scheduleId: Long, snoozeMinutes: Int = 10) {
        try {
            Log.d(TAG, "=== Snoozing Schedule Notification ===")
            Log.d(TAG, "Schedule ID: $scheduleId, Snooze Minutes: $snoozeMinutes")
            
            // Cancel current notification
            cancelScheduleNotification(scheduleId)
            
            // Schedule new notification after snooze duration
            val snoozeTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000)
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("type", "schedule_snooze")
                putExtra("schedule_id", scheduleId)
                putExtra("title", "Schedule Reminder (Snoozed)")
                putExtra("message", "Your scheduled event is ready")
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                (scheduleId.toInt() + 80000), // Different request code for snooze
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                snoozeTime,
                pendingIntent
            )
            
            Log.d(TAG, "SUCCESS: Schedule notification snoozed for $snoozeMinutes minutes")
            
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Error snoozing schedule notification $scheduleId: ${e.message}", e)
        }
    }
    
    /**
     * Send completion feedback notification for routine
     */
    fun sendRoutineCompletionNotification(routineTitle: String, streakCount: Int = 0) {
        try {
            Log.d(TAG, "=== Sending Routine Completion Notification ===")
            
            val congratsMessages = listOf(
                "Great job! Keep up the routine!",
                "Well done! Consistency is key!",
                "Awesome! You're building great habits!",
                "Excellent work! Stay on track!",
                "Perfect! You're doing amazing!"
            )
            
            val message = if (streakCount > 1) {
                "${congratsMessages.random()} ${streakCount} day streak!"
            } else {
                congratsMessages.random()
            }
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Routine Completed!")
                .setContentText("$routineTitle - $message")
                .setStyle(NotificationCompat.BigTextStyle().bigText("$routineTitle - $message"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()
            
            notificationManager.notify(
                (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
                notification
            )
            
            Log.d(TAG, "SUCCESS: Routine completion notification sent")
            
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Error sending routine completion notification: ${e.message}", e)
        }
    }

    /**
     * Cancel notification for a schedule
     */
    fun cancelScheduleNotification(scheduleId: Long) {
        try {
            Log.d(TAG, "=== Canceling Schedule Notification ===")
            Log.d(TAG, "Schedule ID: $scheduleId")
            
            // Cancel event notification
            cancelNotificationByRequestCode(scheduleId.toInt(), "schedule event")
            
            // Cancel reminder notification
            cancelNotificationByRequestCode(scheduleId.toInt() + 50000, "schedule reminder")
            
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Error canceling schedule notification $scheduleId: ${e.message}", e)
        }
    }
    
    /**
     * Cancel notification for a routine
     */
    fun cancelRoutineNotification(routineId: Long) {
        try {
            Log.d(TAG, "=== Canceling Routine Notification ===")
            Log.d(TAG, "Routine ID: $routineId")
            
            // Cancel routine notification
            cancelNotificationByRequestCode(routineId.toInt() + 10000, "routine event")
            
            // Cancel reminder notification
            cancelNotificationByRequestCode(routineId.toInt() + 60000, "routine reminder")
            
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Error canceling routine notification $routineId: ${e.message}", e)
        }
    }
    
    /**
     * Helper method to cancel notification by request code
     */
    private fun cancelNotificationByRequestCode(requestCode: Int, type: String) {
        try {
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
                Log.d(TAG, "SUCCESS: $type notification canceled (request code: $requestCode)")
            } else {
                Log.d(TAG, "WARNING: No pending $type notification found (request code: $requestCode)")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Error canceling $type notification (request code: $requestCode): ${e.message}", e)
        }
    }
}
