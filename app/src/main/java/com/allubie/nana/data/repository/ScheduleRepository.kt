package com.allubie.nana.data.repository

import com.allubie.nana.data.dao.ScheduleEventDao
import com.allubie.nana.data.entity.ScheduleEventEntity
import com.allubie.nana.ui.schedule.EventType
import com.allubie.nana.ui.schedule.ScheduleEvent
import com.allubie.nana.utils.DayOfWeek
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository(private val scheduleEventDao: ScheduleEventDao) {
    
    val allEvents: Flow<List<ScheduleEvent>> = scheduleEventDao.getAllEvents().map { entities ->
        entities.map { it.toScheduleEvent() }
    }
    
    fun getEventsForDate(date: Date): Flow<List<ScheduleEvent>> {
        val calendar = Calendar.getInstance().apply { time = date }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startMillis = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endMillis = calendar.timeInMillis
        
        return scheduleEventDao.getEventsInDateRange(startMillis, endMillis).map { entities ->
            entities.map { it.toScheduleEvent() }
        }
    }
    
    suspend fun insertEvent(event: ScheduleEvent) {
        scheduleEventDao.insertEvent(event.toEntity())
    }
    
    suspend fun updateEvent(event: ScheduleEvent) {
        scheduleEventDao.updateEvent(event.toEntity())
    }
    
    suspend fun deleteEvent(event: ScheduleEvent) {
        scheduleEventDao.deleteEvent(event.toEntity())
    }
    
    // Conversion methods
    private fun ScheduleEvent.toEntity(): ScheduleEventEntity {
        return ScheduleEventEntity(
            id = id,
            title = title,
            dateMillis = date.time,
            startTime = startTime,
            endTime = endTime,
            location = location,
            description = description,
            typeName = type.name,
            isRecurring = isRecurring,
            recurringDays = recurringDays.joinToString(",") { it.name }
        )
    }
    
    private fun ScheduleEventEntity.toScheduleEvent(): ScheduleEvent {
        val eventType = try { EventType.valueOf(typeName) } catch (e: Exception) { EventType.CLASS }
        val days = if (recurringDays.isNotEmpty()) {
            recurringDays.split(",").mapNotNull { dayName ->
                try { DayOfWeek.valueOf(dayName) } catch (e: Exception) { null }
            }
        } else {
            emptyList()
        }
        
        return ScheduleEvent(
            id = id,
            title = title,
            date = Date(dateMillis),
            startTime = startTime,
            endTime = endTime,
            location = location,
            description = description,
            type = eventType,
            isRecurring = isRecurring,
            recurringDays = days
        )
    }
}