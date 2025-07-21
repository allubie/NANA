package com.allubie.nana.data.converter

import androidx.room.TypeConverter
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class Converters {
    
    @TypeConverter
    fun fromDayOfWeekList(value: List<DayOfWeek>): String {
        return Json.encodeToString(value.map { it.name })
    }
    
    @TypeConverter
    fun toDayOfWeekList(value: String): List<DayOfWeek> {
        return try {
            Json.decodeFromString<List<String>>(value).map { DayOfWeek.valueOf(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
