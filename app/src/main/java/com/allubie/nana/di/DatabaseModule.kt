package com.allubie.nana.di

import android.content.Context
import com.allubie.nana.data.AppDatabase
import com.allubie.nana.data.dao.NoteDao
import com.allubie.nana.data.dao.RoutineDao
import com.allubie.nana.data.dao.ScheduleEventDao
import com.allubie.nana.data.dao.TransactionDao
import com.allubie.nana.data.repository.NoteRepository
import com.allubie.nana.data.repository.RoutineRepository
import com.allubie.nana.data.repository.ScheduleRepository
import com.allubie.nana.data.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    fun provideRoutineDao(database: AppDatabase): RoutineDao {
        return database.routineDao()
    }

    @Provides
    fun provideScheduleEventDao(database: AppDatabase): ScheduleEventDao {
        return database.scheduleEventDao()
    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository {
        return NoteRepository(noteDao)
    }

    @Provides
    @Singleton
    fun provideRoutineRepository(routineDao: RoutineDao): RoutineRepository {
        return RoutineRepository(routineDao)
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(scheduleEventDao: ScheduleEventDao): ScheduleRepository {
        return ScheduleRepository(scheduleEventDao)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(transactionDao: TransactionDao): TransactionRepository {
        return TransactionRepository(transactionDao)
    }
}