package com.allubie.nana.data.dao

import androidx.room.*
import com.allubie.nana.data.entity.RoutineEntity
import com.allubie.nana.data.entity.RoutineTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines ORDER BY name")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routine_tasks WHERE routineId = :routineId ORDER BY position")
    fun getTasksForRoutine(routineId: String): Flow<List<RoutineTaskEntity>>

    @Query("SELECT * FROM routines WHERE id = :routineId")
    suspend fun getRoutineById(routineId: String): RoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: RoutineTaskEntity)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Update
    suspend fun updateTask(task: RoutineTaskEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Delete
    suspend fun deleteTask(task: RoutineTaskEntity)

    @Query("DELETE FROM routine_tasks WHERE routineId = :routineId")
    suspend fun deleteTasksForRoutine(routineId: String)

    @Transaction
    suspend fun insertRoutineWithTasks(routine: RoutineEntity, tasks: List<RoutineTaskEntity>) {
        insertRoutine(routine)
        tasks.forEach { insertTask(it) }
    }

    @Transaction
    suspend fun updateRoutineWithTasks(routine: RoutineEntity, tasks: List<RoutineTaskEntity>) {
        updateRoutine(routine)
        deleteTasksForRoutine(routine.id)
        tasks.forEach { insertTask(it) }
    }
}