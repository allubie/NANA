package com.allubie.nana.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.allubie.nana.MainActivity
import com.allubie.nana.R

class NotificationService(private val context: Context) {
    
    companion object {
        const val ROUTINE_CHANNEL_ID = "routine_reminders"
        const val SCHEDULE_CHANNEL_ID = "schedule_reminders"
        const val EXPENSE_CHANNEL_ID = "expense_alerts"
        
        const val ROUTINE_NOTIFICATION_ID = 1001
        const val SCHEDULE_NOTIFICATION_ID = 1002
        const val SCHEDULE_START_NOTIFICATION_ID = 1004
        const val EXPENSE_NOTIFICATION_ID = 1003
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val routineChannel = NotificationChannel(
                ROUTINE_CHANNEL_ID,
                "Routine Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for routine reminders"
                enableVibration(true)
                setShowBadge(true)
            }
            
            val scheduleChannel = NotificationChannel(
                SCHEDULE_CHANNEL_ID,
                "Schedule Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming schedules"
                enableVibration(true)
                setShowBadge(true)
            }
            
            val expenseChannel = NotificationChannel(
                EXPENSE_CHANNEL_ID,
                "Expense Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for expense tracking alerts"
                enableVibration(false)
                setShowBadge(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(listOf(routineChannel, scheduleChannel, expenseChannel))
        }
    }
    
    @SuppressLint("MissingPermission")
    fun sendRoutineReminder(routineTitle: String, routineDescription: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("screen", "routines")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, ROUTINE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Routine Reminder")
            .setContentText("Time for: $routineTitle")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$routineTitle\n$routineDescription"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            if (hasNotificationPermission()) {
                notify(ROUTINE_NOTIFICATION_ID, notification)
            }
        }
    }
    
    @SuppressLint("MissingPermission")
    fun sendScheduleReminder(scheduleTitle: String, scheduleTime: String, minutesBefore: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("screen", "schedules")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val reminderText = if (minutesBefore > 0) {
            "Starting in $minutesBefore minutes at $scheduleTime"
        } else {
            "Starting now at $scheduleTime"
        }
        
        val notification = NotificationCompat.Builder(context, SCHEDULE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Schedule Reminder")
            .setContentText("$scheduleTitle - $reminderText")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            if (hasNotificationPermission()) {
                notify(SCHEDULE_NOTIFICATION_ID, notification)
            }
        }
    }
    
    @SuppressLint("MissingPermission")
    fun sendScheduleStartAlert(scheduleTitle: String, scheduleTime: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("screen", "schedules")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 3, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, SCHEDULE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Schedule Started")
            .setContentText("$scheduleTitle started at $scheduleTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            if (hasNotificationPermission()) {
                notify(SCHEDULE_START_NOTIFICATION_ID, notification)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun sendExpenseAlert(message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("screen", "expenses")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 2, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, EXPENSE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Budget Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            if (hasNotificationPermission()) {
                notify(EXPENSE_NOTIFICATION_ID, notification)
            }
        }
    }
    
    private fun hasNotificationPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
    
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
    
    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}
