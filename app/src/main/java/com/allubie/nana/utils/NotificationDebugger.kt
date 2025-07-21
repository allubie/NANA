package com.allubie.nana.utils

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.allubie.nana.data.entity.Routine
import com.allubie.nana.data.entity.Schedule
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.hours

class NotificationDebugger(private val context: Context) {
    
    companion object {
        private const val TAG = "NotificationDebugger"
    }
    
    fun debugNotificationSystem() {
        Log.d(TAG, "=== Notification System Debug ===")
        
        // Check notification permissions
        val notificationEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        Log.d(TAG, "Notifications enabled: $notificationEnabled")
        
        // Check exact alarm permissions
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val exactAlarmsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
        Log.d(TAG, "Exact alarms enabled: $exactAlarmsEnabled")
        
        // Check Android version
        Log.d(TAG, "Android SDK: ${Build.VERSION.SDK_INT}")
        Log.d(TAG, "Android Release: ${Build.VERSION.RELEASE}")
        
        // Check app package name
        Log.d(TAG, "Package name: ${context.packageName}")
        
        // Check notification channels
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = notificationManager.notificationChannels
            Log.d(TAG, "Notification channels count: ${channels.size}")
            channels.forEach { channel ->
                Log.d(TAG, "Channel: ${channel.id}, Importance: ${channel.importance}")
            }
        }
        
        Log.d(TAG, "=== End Debug ===")
    }
    
    fun debugScheduleNotification(schedule: Schedule) {
        Log.d(TAG, "=== Schedule Debug ===")
        Log.d(TAG, "Schedule ID: ${schedule.id}")
        Log.d(TAG, "Schedule Title: ${schedule.title}")
        Log.d(TAG, "Schedule Description: ${schedule.description}")
        Log.d(TAG, "Schedule DateTime: ${schedule.startDateTime}")
        Log.d(TAG, "Schedule End DateTime: ${schedule.endDateTime}")
        Log.d(TAG, "Schedule isCompleted: ${schedule.isCompleted}")
        Log.d(TAG, "Schedule reminderEnabled: ${schedule.reminderEnabled}")
        Log.d(TAG, "Schedule reminderMinutesBefore: ${schedule.reminderMinutesBefore}")
        Log.d(TAG, "Current Time: ${System.currentTimeMillis()}")
        Log.d(TAG, "=== End Schedule Debug ===")
    }
    
    fun debugRoutineNotification(routine: Routine) {
        Log.d(TAG, "=== Routine Debug ===")
        Log.d(TAG, "Routine ID: ${routine.id}")
        Log.d(TAG, "Routine Title: ${routine.title}")
        Log.d(TAG, "Routine Description: ${routine.description}")
        Log.d(TAG, "Routine Time: ${routine.time}")
        Log.d(TAG, "Routine Frequency: ${routine.frequency}")
        Log.d(TAG, "Routine isActive: ${routine.isActive}")
        Log.d(TAG, "Routine reminderEnabled: ${routine.reminderEnabled}")
        Log.d(TAG, "Routine reminderMinutesBefore: ${routine.reminderMinutesBefore}")
        Log.d(TAG, "Current Time: ${System.currentTimeMillis()}")
        Log.d(TAG, "=== End Routine Debug ===")
    }
    
    fun testNotification() {
        Log.d(TAG, "Testing immediate notification...")
        
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(
            title = "Test Notification",
            description = "This is a test notification from NANA app",
            type = "test",
            itemId = 999L
        )
        
        Log.d(TAG, "Test notification sent")
    }
    
    fun testScheduleNotification() {
        try {
            Log.d(TAG, "=== Testing Schedule Notification (2 minutes) ===")
            
            // Create a test schedule 2 minutes in the future
            val currentTime = kotlinx.datetime.Clock.System.now()
            val testTime = currentTime.plus(2.minutes)
            
            Log.d(TAG, "Current Time: $currentTime")
            Log.d(TAG, "Test Schedule Time: $testTime")
            
            val testSchedule = com.allubie.nana.data.entity.Schedule(
                id = 9999L,
                title = "Test Schedule Notification",
                description = "This is a test schedule notification scheduled for 2 minutes from now",
                startDateTime = testTime.toString(),
                endDateTime = testTime.plus(1.hours).toString(),
                isPinned = false,
                isCompleted = false,
                isRecurring = false,
                recurrencePattern = null,
                reminderEnabled = true,
                reminderMinutesBefore = 1, // Set reminder for 1 minute before the event
                location = "Test Location",
                category = "Test",
                color = "#FF5722",
                createdAt = currentTime.toString(),
                updatedAt = currentTime.toString()
            )
            
            Log.d(TAG, "Created test schedule: ${testSchedule.title}")
            
            // Use NotificationScheduler to schedule the notification
            val notificationScheduler = NotificationScheduler(context)
            notificationScheduler.scheduleNotificationForSchedule(testSchedule)
            
            Log.d(TAG, "Test schedule notification scheduled successfully")
            
            // Also show immediate confirmation
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showNotification(
                title = "Test Scheduled",
                description = "A test notification has been scheduled for 2 minutes from now",
                type = "test",
                itemId = 9998L
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to schedule test notification: ${e.message}", e)
        }
    }
}
