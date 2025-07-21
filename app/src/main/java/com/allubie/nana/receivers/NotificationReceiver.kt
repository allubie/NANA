package com.allubie.nana.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.allubie.nana.utils.NotificationHelper

class NotificationReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "NotificationReceiver"
        const val EXTRA_TITLE = "notification_title"
        const val EXTRA_DESCRIPTION = "notification_description"
        const val EXTRA_TYPE = "notification_type"
        const val EXTRA_ITEM_ID = "item_id"
        
        // Notification types
        const val TYPE_SCHEDULE = "schedule"
        const val TYPE_ROUTINE = "routine"
        const val TYPE_ROUTINE_SNOOZE = "routine_snooze"
        const val TYPE_SCHEDULE_SNOOZE = "schedule_snooze"
        const val TYPE_COMPLETION_FEEDBACK = "completion_feedback"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "NotificationReceiver triggered")
        
        try {
            val type = intent.getStringExtra("type") ?: intent.getStringExtra(EXTRA_TYPE) ?: TYPE_SCHEDULE
            
            when (type) {
                TYPE_ROUTINE_SNOOZE -> {
                    val routineId = intent.getLongExtra("routine_id", 0L)
                    val title = intent.getStringExtra("title") ?: "Routine Reminder (Snoozed)"
                    val message = intent.getStringExtra("message") ?: "Your routine is ready to be completed"
                    
                    Log.d(TAG, "Showing snoozed routine notification: $title")
                    
                    val notificationHelper = NotificationHelper(context)
                    notificationHelper.showRoutineNotificationWithSnooze(
                        title = title,
                        description = message,
                        routineId = routineId
                    )
                }
                
                TYPE_SCHEDULE_SNOOZE -> {
                    val scheduleId = intent.getLongExtra("schedule_id", 0L)
                    val title = intent.getStringExtra("title") ?: "Schedule Reminder (Snoozed)"
                    val message = intent.getStringExtra("message") ?: "Your scheduled event is ready"
                    
                    Log.d(TAG, "Showing snoozed schedule notification: $title")
                    
                    val notificationHelper = NotificationHelper(context)
                    notificationHelper.showScheduleNotificationWithSnooze(
                        title = title,
                        description = message,
                        scheduleId = scheduleId
                    )
                }
                
                else -> {
                    // Handle legacy notifications
                    val title = intent.getStringExtra(EXTRA_TITLE) ?: "NANA Reminder"
                    val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: "You have a scheduled item"
                    val itemId = intent.getLongExtra(EXTRA_ITEM_ID, 0L)
                    
                    Log.d(TAG, "Showing notification: $title - $description")
                    
                    val notificationHelper = NotificationHelper(context)
                    notificationHelper.showNotification(
                        title = title,
                        description = description,
                        type = type,
                        itemId = itemId
                    )
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in onReceive", e)
        }
    }
}
