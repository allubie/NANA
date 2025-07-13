package com.allubie.nana.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allubie.nana.data.repository.ScheduleRepository
import com.allubie.nana.utils.DayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    
    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate
    
    val eventsForSelectedDate: StateFlow<List<ScheduleEvent>> = _selectedDate
        .flatMapLatest { date -> scheduleRepository.getEventsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val allEvents: StateFlow<List<ScheduleEvent>> = scheduleRepository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun setSelectedDate(date: Date) {
        _selectedDate.value = date
    }
    
    fun addEvent(
        title: String,
        date: Date,
        startTime: String,
        endTime: String,
        location: String,
        description: String,
        type: EventType,
        isRecurring: Boolean = false,
        recurringDays: List<DayOfWeek> = emptyList()
    ) {
        if (title.isBlank()) return
        
        viewModelScope.launch {
            val event = ScheduleEvent(
                title = title,
                date = date,
                startTime = startTime,
                endTime = endTime,
                location = location,
                description = description,
                type = type,
                isRecurring = isRecurring,
                recurringDays = recurringDays
            )
            scheduleRepository.insertEvent(event)
        }
    }
    
    fun updateEvent(
        event: ScheduleEvent,
        title: String,
        date: Date,
        startTime: String,
        endTime: String,
        location: String,
        description: String,
        type: EventType,
        isRecurring: Boolean = false,
        recurringDays: List<DayOfWeek> = emptyList()
    ) {
        if (title.isBlank()) return
        
        viewModelScope.launch {
            val updatedEvent = event.copy(
                title = title,
                date = date,
                startTime = startTime,
                endTime = endTime,
                location = location,
                description = description,
                type = type,
                isRecurring = isRecurring,
                recurringDays = recurringDays
            )
            scheduleRepository.updateEvent(updatedEvent)
        }
    }
    
    fun deleteEvent(event: ScheduleEvent) {
        viewModelScope.launch {
            scheduleRepository.deleteEvent(event)
        }
    }
}