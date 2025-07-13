package com.allubie.nana.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allubie.nana.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    
    val allTransactions: StateFlow<List<Transaction>> = transactionRepository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val incomeTransactions: StateFlow<List<Transaction>> = transactionRepository.allIncome
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val expenseTransactions: StateFlow<List<Transaction>> = transactionRepository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val totalIncome: StateFlow<Double> = transactionRepository.totalIncome
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    val totalExpenses: StateFlow<Double> = transactionRepository.totalExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    val balance: Flow<Double> = combine(totalIncome, totalExpenses) { income, expenses ->
        income - expenses
    }
    
    fun addTransaction(
        amount: Double,
        title: String,
        description: String,
        category: TransactionCategory,
        type: TransactionType
    ) {
        if (amount <= 0 || title.isBlank()) return
        
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                title = title,
                description = description,
                category = category,
                type = type
            )
            transactionRepository.insertTransaction(transaction)
        }
    }
    
    fun updateTransaction(
        transaction: Transaction,
        amount: Double,
        title: String,
        description: String,
        category: TransactionCategory,
        type: TransactionType
    ) {
        if (amount <= 0 || title.isBlank()) return
        
        viewModelScope.launch {
            val updatedTransaction = transaction.copy(
                amount = amount,
                title = title,
                description = description,
                category = category,
                type = type
            )
            transactionRepository.updateTransaction(updatedTransaction)
        }
    }
    
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }
}