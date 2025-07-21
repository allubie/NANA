package com.allubie.nana.data.repository

import com.allubie.nana.data.dao.ExpenseDao
import com.allubie.nana.data.entity.Expense
import com.allubie.nana.data.entity.ExpenseCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    
    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()
    
    suspend fun getExpenseById(id: Long): Expense? = expenseDao.getExpenseById(id)
    
    fun getExpensesByCategory(category: String): Flow<List<Expense>> = expenseDao.getExpensesByCategory(category)
    
    fun getExpensesByDateRange(startDate: String, endDate: String): Flow<List<Expense>> = 
        expenseDao.getExpensesInDateRange(startDate, endDate)
    
    fun getAllCategories(): Flow<List<String>> = expenseDao.getAllCategories().map { categories ->
        categories.map { it.name }
    }
    
    suspend fun getTotalExpenseByCategory(category: String): Double {
        // Simplified implementation - can be enhanced with proper queries
        return 0.0
    }
    
    suspend fun getTotalExpenseInDateRange(startDate: String, endDate: String): Double {
        // Simplified implementation - can be enhanced with proper queries  
        return 0.0
    }
    
    suspend fun insertExpense(expense: Expense): Long {
        val now = Clock.System.now().toString() // Use ISO instant format
        return expenseDao.insertExpense(expense.copy(createdAt = now, updatedAt = now))
    }
    
    suspend fun updateExpense(expense: Expense) {
        val now = Clock.System.now().toString() // Use ISO instant format
        expenseDao.updateExpense(expense.copy(updatedAt = now))
    }
    
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
    
    suspend fun getAllExpensesForExport(): List<Expense> = expenseDao.getAllExpensesSync()
    
    suspend fun deleteAllExpenses() = expenseDao.deleteAllExpenses()
    
    fun getCategories(): Flow<List<ExpenseCategory>> = expenseDao.getAllCategories()
}
