package com.allubie.nana.data.repository

import com.allubie.nana.data.dao.RoutineDao
import com.allubie.nana.data.entity.RoutineEntity
import com.allubie.nana.data.entity.RoutineTaskEntity
import com.allubie.nana.ui.routines.Routine
import com.allubie.nana.ui.routines.RoutineIcon
import com.allubie.nana.ui.routines.RoutineTask
import com.allubie.nana.utils.DayOfWeek
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineRepository(private val routineDao: RoutineDao) {
    
    val allRoutines: Flow<List<Routine>> = routineDao.getAllRoutines().map { routineEntities ->
        routineEntities.map { entity ->
            val tasks = routineDao.getTasksForRoutine(entity.id).first()
            entity.toRoutine(tasks)
        }
    }
    
    suspend fun insertRoutine(routine: Routine) {
        val routineEntity = routine.toRoutineEntity()
        val taskEntities = routine.tasks.mapIndexed { index, task ->
            task.toTaskEntity(routine.id, index)
        }
        routineDao.insertRoutineWithTasks(routineEntity, taskEntities)
    }
    
    suspend fun updateRoutine(routine: Routine) {
        val routineEntity = routine.toRoutineEntity()
        val taskEntities = routine.tasks.mapIndexed { index, task ->
            task.toTaskEntity(routine.id, index)
        }
        routineDao.updateRoutineWithTasks(routineEntity, taskEntities)
    }
    
    suspend fun deleteRoutine(routine: Routine) {
        routineDao.deleteRoutine(routine.toRoutineEntity())
    }
    
    suspend fun toggleTaskCompletion(routineId: String, taskId: String, isCompleted: Boolean) {
        val routine = routineDao.getRoutineById(routineId) ?: return
        val tasks = routineDao.getTasksForRoutine(routineId).first()
        val updatedTasks = tasks.map { task ->
            if (task.id == taskId) {
                task.copy(isCompleted = isCompleted)
            } else {
                task
            }
        }
        routineDao.updateRoutineWithTasks(routine, updatedTasks)
    }
    
    // Conversion methods
    private fun Routine.toRoutineEntity(): RoutineEntity {
        return RoutineEntity(
            id = id,
            name = name,
            iconName = icon.name,
            daysActive = daysActive.joinToString(",") { it.name }
        )
    }
    
    private fun RoutineTask.toTaskEntity(routineId: String, position: Int): RoutineTaskEntity {
        return RoutineTaskEntity(
            id = id,
            routineId = routineId,
            name = name,
            time = time,
            isCompleted = isCompleted,
            position = position
        )
    }
    
    private fun RoutineEntity.toRoutine(tasks: List<RoutineTaskEntity>): Routine {
        val icon = try { RoutineIcon.valueOf(iconName) } catch (e: Exception) { RoutineIcon.DEFAULT }
        val days = daysActive.split(",").mapNotNull { dayName ->
            try { DayOfWeek.valueOf(dayName) } catch (e: Exception) { null }
        }
        
        return Routine(
            id = id,
            name = name,
            tasks = tasks.map { it.toRoutineTask() },
            icon = icon,
            daysActive = days
        )
    }
    
    private fun RoutineTaskEntity.toRoutineTask(): RoutineTask {
        return RoutineTask(
            id = id,
            name = name,
            time = time,
            isCompleted = isCompleted
        )
    }
}