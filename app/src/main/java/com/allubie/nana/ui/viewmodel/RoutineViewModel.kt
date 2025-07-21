package com.allubie.nana.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.allubie.nana.data.entity.Routine
import com.allubie.nana.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoutineViewModel(private val repository: RoutineRepository) : ViewModel() {
    
    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines.asStateFlow()
    
    private val _currentRoutine = MutableStateFlow<Routine?>(null)
    val currentRoutine: StateFlow<Routine?> = _currentRoutine.asStateFlow()
    
    init {
        loadAllRoutines()
    }
    
    private fun loadAllRoutines() {
        viewModelScope.launch {
            repository.getAllRoutines().collect {
                _routines.value = it
            }
        }
    }
    
    fun loadRoutineById(id: Long) {
        viewModelScope.launch {
            _currentRoutine.value = repository.getRoutineById(id)
        }
    }
    
    fun saveRoutine(routine: Routine) {
        viewModelScope.launch {
            if (routine.id == 0L) {
                repository.insertRoutine(routine)
            } else {
                repository.updateRoutine(routine)
            }
        }
    }
    
    fun toggleCompletionStatus(routineId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleCompletionStatus(routineId, isCompleted)
            
            // Send completion feedback notification if marking as completed
            if (isCompleted) {
                val routine = repository.getRoutineById(routineId)
                if (routine != null) {
                    repository.sendCompletionFeedback(routine.title)
                }
            }
        }
    }
    
    fun togglePin(routineId: Long, isPinned: Boolean) {
        viewModelScope.launch {
            repository.togglePin(routineId, isPinned)
        }
    }
    
    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            repository.deleteRoutine(routine)
        }
    }
}

class RoutineViewModelFactory(private val repository: RoutineRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoutineViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
