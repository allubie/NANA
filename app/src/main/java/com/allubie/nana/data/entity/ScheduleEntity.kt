package com.allubie.nana.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.allubie.nana.ui.schedule.EventType
import com.allubie.nana.utils.DayOfWeek
import java.util.Date
import java.util.UUID

@Entity(tableName = "schedule_events")
data class ScheduleEventEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dateMillis: Long, // Store Date as Long
    val startTime: String,
    val endTime: String,
    val location: String = "",
    val description: String = "",
    val typeName: String = "CLASS",
    val isRecurring: Boolean = false,
    val recurringDays: String = "" // Comma-separated list of days
)

class ScheduleConverters {
    @TypeConverter
    fun fromEventType(type: EventType): String {
        return type.name
    }

    @TypeConverter
    fun toEventType(name: String): EventType {
        return try {
            EventType.valueOf(name)
        } catch (e: Exception) {
            EventType.CLASS
        }
    }

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(timeMillis: Long): Date {
        return Date(timeMillis)
    }

    @TypeConverter
    fun fromRecurringDays(days: List<DayOfWeek>): String {
        return days.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toRecurringDays(data: String): List<DayOfWeek> {
        if (data.isEmpty()) return emptyList()
        return data.split(",").map { dayName ->
            try {
                DayOfWeek.valueOf(dayName)
            } catch (e: Exception) {
                DayOfWeek.MONDAY
            }
        }
    }
}