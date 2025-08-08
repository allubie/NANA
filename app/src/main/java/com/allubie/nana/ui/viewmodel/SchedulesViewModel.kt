package com.allubie.nana.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.allubie.nana.data.entity.ScheduleEntity
import com.allubie.nana.data.repository.ScheduleRepository
import com.allubie.nana.notification.NotificationWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.todayIn

class SchedulesViewModel(
    private val repository: ScheduleRepository,
    private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SchedulesUiState())
    val uiState: StateFlow<SchedulesUiState> = _uiState.asStateFlow()
    
    val allSchedules = repository.getAllSchedules()
    
    init {
        loadTodaysSchedules()
    }
    
    private fun loadTodaysSchedules() {
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val todaysSchedules = repository.getSchedulesForDateSync(today)
            val nextSchedule = repository.getNextIncompleteSchedule(today)
            
            _uiState.value = _uiState.value.copy(
                todaysSchedules = todaysSchedules,
                nextSchedule = nextSchedule,
                selectedDate = today
            )
        }
    }
    
    fun loadSchedulesForDate(date: LocalDate) {
        viewModelScope.launch {
            repository.getSchedulesForDate(date).collect { schedules ->
                _uiState.value = _uiState.value.copy(
                    selectedDateSchedules = schedules,
                    selectedDate = date
                )
            }
        }
    }
    
    fun searchSchedules(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchQuery = "", searchResults = emptyList())
        } else {
            _uiState.value = _uiState.value.copy(searchQuery = query)
            viewModelScope.launch {
                repository.searchSchedules(query).collect { results ->
                    _uiState.value = _uiState.value.copy(searchResults = results)
                }
            }
        }
    }
    
    fun clearSearch() {
        _uiState.value = _uiState.value.copy(searchQuery = "", searchResults = emptyList())
    }
    
    fun createSchedule(
        title: String,
        description: String,
        startTime: LocalTime,
        endTime: LocalTime,
        date: LocalDate,
        location: String? = null,
        category: String,
        isRecurring: Boolean = false,
        recurringPattern: String? = null,
        reminderMinutes: Int = 15
    ) {
        viewModelScope.launch {
            val schedule = repository.createSchedule(
                title, description, startTime, endTime, date, 
                location, category, isRecurring, recurringPattern, reminderMinutes
            )
            
            // Schedule notification reminder (X minutes before start)
            val scheduledDateTime = date.atTime(startTime)
            NotificationWorker.scheduleScheduleReminder(
                context = context,
                scheduleId = schedule.id,
                title = title,
                scheduledTime = scheduledDateTime,
                minutesBefore = reminderMinutes
            )
            
            // Schedule start notification alert (exactly when schedule starts)
            NotificationWorker.scheduleScheduleStartAlert(
                context = context,
                scheduleId = schedule.id,
                title = title,
                scheduledTime = scheduledDateTime
            )
            
            if (date == Clock.System.todayIn(TimeZone.currentSystemDefault())) {
                loadTodaysSchedules()
            }
        }
    }
    
    fun updateSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            repository.updateSchedule(schedule)
            
            // Cancel existing notifications and reschedule both reminder and start alerts
            NotificationWorker.cancelNotifications(context, "schedule_${schedule.id}")
            NotificationWorker.cancelNotifications(context, "schedule_start_${schedule.id}")
            
            val scheduledDateTime = schedule.date.atTime(schedule.startTime)
            NotificationWorker.scheduleScheduleReminder(
                context = context,
                scheduleId = schedule.id,
                title = schedule.title,
                scheduledTime = scheduledDateTime,
                minutesBefore = schedule.reminderMinutes
            )
            
            // Schedule start notification alert (exactly when schedule starts)
            NotificationWorker.scheduleScheduleStartAlert(
                context = context,
                scheduleId = schedule.id,
                title = schedule.title,
                scheduledTime = scheduledDateTime
            )
            
            loadTodaysSchedules()
        }
    }
    
    fun getScheduleById(id: String, callback: (ScheduleEntity?) -> Unit) {
        viewModelScope.launch {
            val schedule = repository.getScheduleById(id)
            callback(schedule)
        }
    }
    
    fun deleteSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            // Cancel scheduled notifications
            NotificationWorker.cancelNotifications(context, "schedule_${schedule.id}")
            NotificationWorker.cancelNotifications(context, "schedule_start_${schedule.id}")
            repository.deleteSchedule(schedule)
            loadTodaysSchedules()
        }
    }
    
    fun toggleCompletion(scheduleId: String) {
        viewModelScope.launch {
            repository.toggleCompletion(scheduleId)
            loadTodaysSchedules()
        }
    }
    
    fun togglePin(scheduleId: String, isPinned: Boolean) {
        viewModelScope.launch {
            if (isPinned) {
                repository.unpinSchedule(scheduleId)
            } else {
                repository.pinSchedule(scheduleId)
            }
        }
    }
    
    fun setViewMode(viewMode: ScheduleViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = viewMode)
    }
}

data class SchedulesUiState(
    val todaysSchedules: List<ScheduleEntity> = emptyList(),
    val selectedDateSchedules: List<ScheduleEntity> = emptyList(),
    val nextSchedule: ScheduleEntity? = null,
    val selectedDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val searchQuery: String = "",
    val searchResults: List<ScheduleEntity> = emptyList(),
    val viewMode: ScheduleViewMode = ScheduleViewMode.LIST,
    val isLoading: Boolean = false
)

enum class ScheduleViewMode {
    LIST, CALENDAR, TIMELINE
}

class SchedulesViewModelFactory(
    private val repository: ScheduleRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SchedulesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SchedulesViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
