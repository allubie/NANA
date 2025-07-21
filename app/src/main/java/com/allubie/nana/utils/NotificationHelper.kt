package com.allubie.nana.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.allubie.nana.MainActivity
import com.allubie.nana.R
import com.allubie.nana.receivers.SnoozeActionReceiver

class NotificationHelper(private val context: Context) {
    
    companion object {
        private const val TAG = "NotificationHelper"
        private const val CHANNEL_ID_SCHEDULES = "schedule_notifications"
        private const val CHANNEL_ID_ROUTINES = "routine_notifications"
        private const val CHANNEL_NAME_SCHEDULES = "Schedule Reminders"
        private const val CHANNEL_NAME_ROUTINES = "Routine Reminders"
        private const val CHANNEL_DESCRIPTION_SCHEDULES = "Notifications for scheduled events"
        private const val CHANNEL_DESCRIPTION_ROUTINES = "Notifications for routine reminders"
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Schedule notifications channel
            val scheduleChannel = NotificationChannel(
                CHANNEL_ID_SCHEDULES,
                CHANNEL_NAME_SCHEDULES,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION_SCHEDULES
                enableLights(true)
                enableVibration(true)
            }
            
            // Routine notifications channel
            val routineChannel = NotificationChannel(
                CHANNEL_ID_ROUTINES,
                CHANNEL_NAME_ROUTINES,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION_ROUTINES
                enableLights(true)
                enableVibration(true)
            }
            
            notificationManager.createNotificationChannel(scheduleChannel)
            notificationManager.createNotificationChannel(routineChannel)
            
            Log.d(TAG, "Notification channels created")
        }
    }
    
    fun showNotification(
        title: String,
        description: String,
        type: String,
        itemId: Long,
        notificationId: Int = itemId.toInt()
    ) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("notification_type", type)
                putExtra("item_id", itemId)
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val channelId = when (type) {
                "routine" -> CHANNEL_ID_ROUTINES
                else -> CHANNEL_ID_SCHEDULES
            }
            
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
            
            val notificationManager = NotificationManagerCompat.from(context)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(notificationId, notification)
                Log.d(TAG, "Notification shown: $title")
            } else {
                Log.w(TAG, "Notifications are disabled")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
        }
    }
    
    fun showRoutineNotificationWithSnooze(
        title: String,
        description: String,
        routineId: Long,
        notificationId: Int = routineId.toInt()
    ) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("notification_type", "routine")
                putExtra("item_id", routineId)
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Snooze action intent
            val snoozeIntent = Intent(context, SnoozeActionReceiver::class.java)
            snoozeIntent.putExtra("routine_id", routineId)
            snoozeIntent.putExtra("action", "snooze_routine")
            
            val snoozePendingIntent = PendingIntent.getBroadcast(
                context,
                (routineId.toInt() + 90000),
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Complete action intent
            val completeIntent = Intent(context, SnoozeActionReceiver::class.java)
            completeIntent.putExtra("routine_id", routineId)
            completeIntent.putExtra("action", "complete_routine")
            
            val completePendingIntent = PendingIntent.getBroadcast(
                context,
                (routineId.toInt() + 95000),
                completeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID_ROUTINES)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_notification, "Snooze 10min", snoozePendingIntent)
                .addAction(R.drawable.ic_notification, "Mark Complete", completePendingIntent)
                .build()
            
            val notificationManager = NotificationManagerCompat.from(context)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(notificationId, notification)
                Log.d(TAG, "Routine notification with snooze shown: $title")
            } else {
                Log.w(TAG, "Notifications are disabled")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error showing routine notification with snooze", e)
        }
    }
    
    fun showScheduleNotificationWithSnooze(
        title: String,
        description: String,
        scheduleId: Long,
        notificationId: Int = scheduleId.toInt()
    ) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("notification_type", "schedule")
                putExtra("item_id", scheduleId)
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Snooze action intent
            val snoozeIntent = Intent(context, SnoozeActionReceiver::class.java)
            snoozeIntent.putExtra("schedule_id", scheduleId)
            snoozeIntent.putExtra("action", "snooze_schedule")
            
            val snoozePendingIntent = PendingIntent.getBroadcast(
                context,
                (scheduleId.toInt() + 85000),
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID_SCHEDULES)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_notification, "Snooze 10min", snoozePendingIntent)
                .build()
            
            val notificationManager = NotificationManagerCompat.from(context)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(notificationId, notification)
                Log.d(TAG, "Schedule notification with snooze shown: $title")
            } else {
                Log.w(TAG, "Notifications are disabled")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error showing schedule notification with snooze", e)
        }
    }

    fun cancelNotification(notificationId: Int) {
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(notificationId)
            Log.d(TAG, "Notification cancelled: $notificationId")
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling notification", e)
        }
    }
    
    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}
