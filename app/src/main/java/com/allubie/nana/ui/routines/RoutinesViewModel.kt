package com.allubie.nana.ui.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allubie.nana.data.repository.RoutineRepository
import com.allubie.nana.utils.DayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {
    
    val routines: StateFlow<List<Routine>> = routineRepository.allRoutines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun addRoutine(
        name: String,
        tasks: List<RoutineTask>,
        icon: RoutineIcon,
        daysActive: List<DayOfWeek>
    ) {
        if (name.isBlank()) return
        
        viewModelScope.launch {
            val routine = Routine(
                name = name,
                tasks = tasks,
                icon = icon,
                daysActive = daysActive
            )
            routineRepository.insertRoutine(routine)
        }
    }
    
    fun updateRoutine(
        routine: Routine,
        name: String,
        tasks: List<RoutineTask>,
        icon: RoutineIcon,
        daysActive: List<DayOfWeek>
    ) {
        if (name.isBlank()) return
        
        viewModelScope.launch {
            val updatedRoutine = routine.copy(
                name = name,
                tasks = tasks,
                icon = icon,
                daysActive = daysActive
            )
            routineRepository.updateRoutine(updatedRoutine)
        }
    }
    
    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            routineRepository.deleteRoutine(routine)
        }
    }
    
    fun toggleTaskCompletion(routineId: String, taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            routineRepository.toggleTaskCompletion(routineId, taskId, isCompleted)
        }
    }
}