package com.allubie.nana.data.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DateTimeConverters {
    
    @TypeConverter
    fun fromInstant(instant: Instant?): String? {
        return instant?.toString()
    }
    
    @TypeConverter
    fun toInstant(instantString: String?): Instant? {
        return instantString?.let { Instant.parse(it) }
    }
    
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }
    
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }
    
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }
    
    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it) }
    }
}
