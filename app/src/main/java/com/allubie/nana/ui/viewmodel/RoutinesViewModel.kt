package com.allubie.nana.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.allubie.nana.data.entity.RoutineEntity
import com.allubie.nana.data.repository.RoutineRepository
import com.allubie.nana.notification.NotificationWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class RoutinesViewModel(
    private val repository: RoutineRepository,
    private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RoutinesUiState())
    val uiState: StateFlow<RoutinesUiState> = _uiState.asStateFlow()
    
    val routines = repository.getActiveRoutines()
    
    init {
        loadTodaysProgress()
    }
    
    private fun loadTodaysProgress() {
        viewModelScope.launch {
            try {
                routines.combine(_uiState) { routinesList, state ->
                    val routinesWithProgress = routinesList.map { routine ->
                        val isCompleted = repository.isCompletedToday(routine.id)
                        val streak = repository.getStreakCount(routine.id)
                        val progress = repository.getCompletionRate(routine.id, 30)
                        
                        RoutineWithProgress(
                            routine = routine,
                            isCompletedToday = isCompleted,
                            streak = streak,
                            progress = progress
                        )
                    }
                    
                    val completedToday = routinesWithProgress.count { it.isCompletedToday }
                    val totalRoutines = routinesWithProgress.size
                    
                    state.copy(
                        routinesWithProgress = routinesWithProgress,
                        completedToday = completedToday,
                        totalRoutines = totalRoutines,
                        overallProgress = if (totalRoutines > 0) completedToday.toFloat() / totalRoutines else 0f,
                        isLoading = false
                    )
                }.collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    
    fun createRoutine(title: String, description: String, frequency: String, reminderTime: String? = null) {
        viewModelScope.launch {
            val routine = repository.createRoutine(title, description, frequency, reminderTime)
            
            // Schedule notification if reminder time is provided
            reminderTime?.let { timeString ->
                try {
                    val time = LocalTime.parse(timeString)
                    NotificationWorker.scheduleRoutineReminder(
                        context = context,
                        routineId = routine.id,
                        title = title,
                        description = description,
                        reminderTime = time
                    )
                } catch (e: Exception) {
                    // Handle parsing error gracefully
                }
            }
        }
    }
    
    fun updateRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            repository.updateRoutine(routine)
            
            // Cancel existing notifications and reschedule if needed
            NotificationWorker.cancelNotifications(context, "routine_${routine.id}")
            
            routine.reminderTime?.let { timeString ->
                try {
                    val time = LocalTime.parse(timeString)
                    NotificationWorker.scheduleRoutineReminder(
                        context = context,
                        routineId = routine.id,
                        title = routine.title,
                        description = routine.description,
                        reminderTime = time
                    )
                } catch (e: Exception) {
                    // Handle parsing error gracefully
                }
            }
        }
    }
    
    fun getRoutineById(id: String, callback: (RoutineEntity?) -> Unit) {
        viewModelScope.launch {
            val routine = repository.getRoutineById(id)
            callback(routine)
        }
    }
    
    fun deleteRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            // Cancel scheduled notifications
            NotificationWorker.cancelNotifications(context, "routine_${routine.id}")
            repository.deleteRoutine(routine)
        }
    }
    
    fun toggleCompletion(routineId: String) {
        viewModelScope.launch {
            repository.toggleCompletion(routineId)
            loadTodaysProgress() // Refresh the progress
        }
    }
    
    fun togglePin(routineId: String, isPinned: Boolean) {
        viewModelScope.launch {
            if (isPinned) {
                repository.unpinRoutine(routineId)
            } else {
                repository.pinRoutine(routineId)
            }
        }
    }
}

data class RoutineWithProgress(
    val routine: RoutineEntity,
    val isCompletedToday: Boolean,
    val streak: Int,
    val progress: Float
)

data class RoutinesUiState(
    val routinesWithProgress: List<RoutineWithProgress> = emptyList(),
    val completedToday: Int = 0,
    val totalRoutines: Int = 0,
    val overallProgress: Float = 0f,
    val isLoading: Boolean = false
)

class RoutinesViewModelFactory(
    private val repository: RoutineRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutinesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoutinesViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
