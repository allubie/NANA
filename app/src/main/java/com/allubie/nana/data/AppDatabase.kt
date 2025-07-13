package com.allubie.nana.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.allubie.nana.data.dao.NoteDao
import com.allubie.nana.data.dao.RoutineDao
import com.allubie.nana.data.dao.ScheduleEventDao
import com.allubie.nana.data.dao.TransactionDao
import com.allubie.nana.data.entity.*

@Database(
    entities = [
        NoteEntity::class,
        RoutineEntity::class,
        RoutineTaskEntity::class,
        ScheduleEventEntity::class,
        TransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    NoteConverters::class,
    RoutineConverters::class,
    ScheduleConverters::class,
    TransactionConverters::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun routineDao(): RoutineDao
    abstract fun scheduleEventDao(): ScheduleEventDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nana_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}