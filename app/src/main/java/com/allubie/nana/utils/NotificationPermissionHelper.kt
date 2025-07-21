package com.allubie.nana.utils

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class NotificationPermissionHelper(private val context: Context) {
    
    companion object {
        private const val TAG = "NotificationPermissionHelper"
        const val REQUEST_CODE_NOTIFICATION_PERMISSION = 1001
        const val REQUEST_CODE_EXACT_ALARM_PERMISSION = 1002
    }
    
    /**
     * Check if notifications are enabled for the app
     */
    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
    
    /**
     * Check if exact alarms can be scheduled
     */
    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
    
    /**
     * Request notification permission
     */
    fun requestNotificationPermission(activity: Activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13+, request notification permission
                activity.requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATION_PERMISSION
                )
            } else {
                // For older versions, direct to notification settings
                openNotificationSettings()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting notification permission", e)
        }
    }
    
    /**
     * Request exact alarm permission
     */
    fun requestExactAlarmPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting exact alarm permission", e)
        }
    }
    
    /**
     * Open notification settings for the app
     */
    fun openNotificationSettings() {
        try {
            val intent = Intent().apply {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    else -> {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.parse("package:${context.packageName}")
                    }
                }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening notification settings", e)
        }
    }
    
    /**
     * Check all required permissions for notifications
     */
    fun checkAllNotificationPermissions(): NotificationPermissionStatus {
        return NotificationPermissionStatus(
            notificationsEnabled = areNotificationsEnabled(),
            exactAlarmsEnabled = canScheduleExactAlarms()
        )
    }
    
    /**
     * Get a user-friendly message about missing permissions
     */
    fun getMissingPermissionsMessage(): String? {
        val status = checkAllNotificationPermissions()
        
        return when {
            !status.notificationsEnabled && !status.exactAlarmsEnabled -> {
                "Notifications and exact alarms are disabled. Please enable them in settings for timely reminders."
            }
            !status.notificationsEnabled -> {
                "Notifications are disabled. Please enable them in settings to receive reminders."
            }
            !status.exactAlarmsEnabled -> {
                "Exact alarms are disabled. Please enable them in settings for precise timing."
            }
            else -> null
        }
    }
}

data class NotificationPermissionStatus(
    val notificationsEnabled: Boolean,
    val exactAlarmsEnabled: Boolean
) {
    val allEnabled: Boolean = notificationsEnabled && exactAlarmsEnabled
}
