package com.allubie.nana.data.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.allubie.nana.data.entity.ExpenseCategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseCallback : RoomDatabase.Callback() {
    
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        
        // Pre-populate expense categories
        INSTANCE?.let { database ->
            CoroutineScope(Dispatchers.IO).launch {
                populateDefaultCategories(database)
            }
        }
    }
    
    private suspend fun populateDefaultCategories(database: AppDatabase) {
        val expenseDao = database.expenseDao()
        
        val defaultCategories = listOf(
            ExpenseCategoryEntity("Food", "Fastfood", "#FF6B6B", 600.0),
            ExpenseCategoryEntity("Education", "School", "#4ECDC4", 300.0),
            ExpenseCategoryEntity("Transport", "LocalGasStation", "#45B7D1", 200.0),
            ExpenseCategoryEntity("Shopping", "ShoppingCart", "#96CEB4", 250.0),
            ExpenseCategoryEntity("Entertainment", "Movie", "#FFA726", 150.0),
            ExpenseCategoryEntity("Health", "LocalHospital", "#AB47BC", 200.0)
        )
        
        defaultCategories.forEach { category ->
            expenseDao.insertCategory(category)
        }
    }
    
    companion object {
        private var INSTANCE: AppDatabase? = null
        
        fun setInstance(instance: AppDatabase) {
            INSTANCE = instance
        }
    }
}
