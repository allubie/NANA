package com.allubie.nana.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.allubie.nana.data.entity.Schedule
import com.allubie.nana.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel(private val repository: ScheduleRepository) : ViewModel() {
    
    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()
    
    private val _currentSchedule = MutableStateFlow<Schedule?>(null)
    val currentSchedule: StateFlow<Schedule?> = _currentSchedule.asStateFlow()
    
    init {
        loadAllSchedules()
    }
    
    private fun loadAllSchedules() {
        viewModelScope.launch {
            repository.getAllSchedules().collect {
                _schedules.value = it
            }
        }
    }
    
    fun loadSchedulesByDate(date: String) {
        viewModelScope.launch {
            repository.getSchedulesByDate(date).collect {
                _schedules.value = it
            }
        }
    }
    
    fun loadScheduleById(id: Long) {
        viewModelScope.launch {
            _currentSchedule.value = repository.getScheduleById(id)
        }
    }
    
    fun saveSchedule(schedule: Schedule) {
        viewModelScope.launch {
            if (schedule.id == 0L) {
                repository.insertSchedule(schedule)
            } else {
                repository.updateSchedule(schedule)
            }
        }
    }
    
    fun toggleCompletionStatus(scheduleId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleCompletionStatus(scheduleId, isCompleted)
        }
    }
    
    fun togglePinStatus(scheduleId: Long, isPinned: Boolean) {
        viewModelScope.launch {
            repository.togglePinStatus(scheduleId, isPinned)
        }
    }
    
    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
        }
    }
}

class ScheduleViewModelFactory(private val repository: ScheduleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
