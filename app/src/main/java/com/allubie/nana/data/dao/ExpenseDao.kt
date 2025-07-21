package com.allubie.nana.data.dao

import androidx.room.*
import com.allubie.nana.data.entity.Expense
import com.allubie.nana.data.entity.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    suspend fun getAllExpensesSync(): List<Expense>
    
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): Expense?
    
    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesInDateRange(startDate: String, endDate: String): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE strftime('%Y-%m', date) = :yearMonth ORDER BY date DESC")
    fun getExpensesForMonth(yearMonth: String): Flow<List<Expense>>
    
    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE strftime('%Y-%m', date) = :yearMonth GROUP BY category")
    fun getCategoryTotalsForMonth(yearMonth: String): Flow<List<CategoryTotal>>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE strftime('%Y-%m', date) = :yearMonth")
    suspend fun getTotalExpensesForMonth(yearMonth: String): Double?
    
    @Insert
    suspend fun insertExpense(expense: Expense): Long
    
    @Update
    suspend fun updateExpense(expense: Expense)
    
    @Delete
    suspend fun deleteExpense(expense: Expense)
    
    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
    
    // Category operations
    @Query("SELECT * FROM expense_categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<ExpenseCategory>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: ExpenseCategory)
    
    @Update
    suspend fun updateCategory(category: ExpenseCategory)
    
    @Delete
    suspend fun deleteCategory(category: ExpenseCategory)
    
    @Query("SELECT * FROM expense_categories WHERE name = :name")
    suspend fun getCategoryByName(name: String): ExpenseCategory?
}

data class CategoryTotal(
    val category: String,
    val total: Double
)
