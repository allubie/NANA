package com.allubie.nana.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val date: LocalDate,
    val location: String? = null,
    val isPinned: Boolean = false,
    val isCompleted: Boolean = false,
    val category: String,
    val isRecurring: Boolean = false,
    val recurringPattern: String? = null, // "daily", "weekly", "monthly"
    val reminderMinutes: Int = 15, // Minutes before event to show reminder
    val createdAt: Instant = Clock.System.now()
)
