package com.allubie.nana.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.allubie.nana.ui.routines.RoutineIcon
import com.allubie.nana.utils.DayOfWeek
import java.util.UUID

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconName: String = "DEFAULT",
    val daysActive: String = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY"
)

@Entity(tableName = "routine_tasks")
data class RoutineTaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val routineId: String,  // Foreign key to parent routine
    val name: String,
    val time: String = "",
    val isCompleted: Boolean = false,
    val position: Int = 0  // For ordering tasks
)

class RoutineConverters {
    @TypeConverter
    fun fromRoutineIcon(icon: RoutineIcon): String {
        return icon.name
    }

    @TypeConverter
    fun toRoutineIcon(name: String): RoutineIcon {
        return try {
            RoutineIcon.valueOf(name)
        } catch (e: Exception) {
            RoutineIcon.DEFAULT
        }
    }

    @TypeConverter
    fun fromDaysList(days: List<DayOfWeek>): String {
        return days.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDaysList(data: String): List<DayOfWeek> {
        if (data.isEmpty()) return emptyList()
        return data.split(",").map { dayName ->
            try {
                DayOfWeek.valueOf(dayName)
            } catch (e: Exception) {
                DayOfWeek.MONDAY // Default fallback
            }
        }
    }
}