package com.allubie.nana.data.database

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DateTimeConverters {
    
    @TypeConverter
    fun fromInstant(instant: Instant?): String? = instant?.toString()
    
    @TypeConverter
    fun toInstant(value: String?): Instant? = value?.let { Instant.parse(it) }
    
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()
    
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }
    
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? = time?.toString()
    
    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }
}
