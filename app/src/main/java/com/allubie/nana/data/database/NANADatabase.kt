package com.allubie.nana.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.allubie.nana.data.converter.Converters
import com.allubie.nana.data.dao.*
import com.allubie.nana.data.entity.*

@Database(
    entities = [
        Note::class,
        Routine::class,
        Schedule::class,
        Expense::class,
        ExpenseCategory::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NANADatabase : RoomDatabase() {
    
    abstract fun noteDao(): NoteDao
    abstract fun routineDao(): RoutineDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun expenseDao(): ExpenseDao
    
    companion object {
        @Volatile
        private var INSTANCE: NANADatabase? = null
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to notes table for rich content support
                database.execSQL("ALTER TABLE notes ADD COLUMN richContent TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE notes ADD COLUMN hasLinks INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE notes ADD COLUMN hasFormatting INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        fun getDatabase(context: Context): NANADatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NANADatabase::class.java,
                    "nana_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
