package com.allubie.nana.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(tableName = "routine_completions")
data class RoutineCompletionEntity(
    @PrimaryKey
    val id: String,
    val routineId: String,
    val completionDate: LocalDate,
    val completedAt: Instant = Clock.System.now()
)
