package com.allubie.nana.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val startDateTime: String, // ISO format string
    val endDateTime: String,
    val isPinned: Boolean = false,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null, // JSON string for complex patterns
    val reminderEnabled: Boolean = false,
    val reminderMinutesBefore: Int = 15,
    val location: String = "",
    val category: String = "",
    val color: String = "",
    val createdAt: String,
    val updatedAt: String
)
