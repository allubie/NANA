package com.allubie.nana.data.repository

import com.allubie.nana.data.dao.TransactionDao
import com.allubie.nana.data.entity.TransactionEntity
import com.allubie.nana.ui.finance.Transaction
import com.allubie.nana.ui.finance.TransactionCategory
import com.allubie.nana.ui.finance.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository(private val transactionDao: TransactionDao) {
    
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
        .map { entities -> entities.map { it.toTransaction() } }
    
    val allIncome: Flow<List<Transaction>> = transactionDao.getTransactionsByType("INCOME")
        .map { entities -> entities.map { it.toTransaction() } }
    
    val allExpenses: Flow<List<Transaction>> = transactionDao.getTransactionsByType("EXPENSE")
        .map { entities -> entities.map { it.toTransaction() } }
    
    val totalIncome: Flow<Double> = transactionDao.getTotalIncome().map { it ?: 0.0 }
    
    val totalExpenses: Flow<Double> = transactionDao.getTotalExpenses().map { it ?: 0.0 }
    
    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }
    
    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }
    
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }
    
    // Conversion methods
    private fun Transaction.toEntity(): TransactionEntity {
        return TransactionEntity(
            id = id,
            amount = amount,
            title = title,
            description = description,
            dateMillis = date.time,
            categoryName = category.name,
            typeName = type.name
        )
    }
    
    private fun TransactionEntity.toTransaction(): Transaction {
        val transactionType = try { TransactionType.valueOf(typeName) } catch (e: Exception) { TransactionType.EXPENSE }
        val category = try { TransactionCategory.valueOf(categoryName) } catch (e: Exception) { TransactionCategory.OTHER }
        
        return Transaction(
            id = id,
            amount = amount,
            title = title,
            description = description,
            date = Date(dateMillis),
            category = category,
            type = transactionType
        )
    }
}