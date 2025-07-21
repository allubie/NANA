package com.allubie.nana.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isPinned: Boolean = false,
    val isCompleted: Boolean = false,
    val isActive: Boolean = true,
    val frequency: RoutineFrequency,
    val daysOfWeek: List<DayOfWeek> = emptyList(),
    val time: String? = null, // HH:mm format
    val reminderEnabled: Boolean = false,
    val reminderMinutesBefore: Int = 0,
    val createdAt: String,
    val completedAt: String? = null,
    val streak: Int = 0,
    val category: String = ""
)

@Serializable
enum class RoutineFrequency {
    DAILY, WEEKLY, CUSTOM
}
