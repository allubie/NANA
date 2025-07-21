package com.allubie.nana.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.allubie.nana.data.database.NANADatabase
import com.allubie.nana.data.repository.RoutineRepository
import com.allubie.nana.utils.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SnoozeActionReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "SnoozeActionReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "SnoozeActionReceiver triggered")
        
        try {
            val action = intent.getStringExtra("action")
            val notificationScheduler = NotificationScheduler(context)
            val notificationManager = NotificationManagerCompat.from(context)
            
            when (action) {
                "snooze_routine" -> {
                    val routineId = intent.getLongExtra("routine_id", 0L)
                    Log.d(TAG, "Snoozing routine: $routineId")
                    
                    // Cancel current notification
                    notificationManager.cancel(routineId.toInt())
                    
                    // Snooze for 10 minutes
                    notificationScheduler.snoozeRoutineNotification(routineId, 10)
                }
                
                "snooze_schedule" -> {
                    val scheduleId = intent.getLongExtra("schedule_id", 0L)
                    Log.d(TAG, "Snoozing schedule: $scheduleId")
                    
                    // Cancel current notification
                    notificationManager.cancel(scheduleId.toInt())
                    
                    // Snooze for 10 minutes
                    notificationScheduler.snoozeScheduleNotification(scheduleId, 10)
                }
                
                "complete_routine" -> {
                    val routineId = intent.getLongExtra("routine_id", 0L)
                    Log.d(TAG, "Completing routine from notification: $routineId")
                    
                    // Cancel current notification
                    notificationManager.cancel(routineId.toInt())
                    
                    // Mark routine as completed
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val database = NANADatabase.getDatabase(context)
                            val repository = RoutineRepository(database.routineDao(), context)
                            
                            // Get routine details
                            val routine = repository.getRoutineById(routineId)
                            if (routine != null) {
                                // Mark as completed
                                repository.toggleCompletionStatus(routineId, true)
                                
                                // Send completion feedback notification
                                notificationScheduler.sendRoutineCompletionNotification(
                                    routine.title,
                                    streakCount = 1 // TODO: Calculate actual streak
                                )
                                
                                Log.d(TAG, "Routine marked as completed: ${routine.title}")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error completing routine from notification", e)
                        }
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in SnoozeActionReceiver", e)
        }
    }
}
