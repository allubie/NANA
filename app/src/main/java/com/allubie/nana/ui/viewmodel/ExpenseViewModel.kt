package com.allubie.nana.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.allubie.nana.data.entity.Expense
import com.allubie.nana.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {
    
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()
    
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()
    
    private val _currentExpense = MutableStateFlow<Expense?>(null)
    val currentExpense: StateFlow<Expense?> = _currentExpense.asStateFlow()
    
    private val _categoryTotals = MutableStateFlow<Map<String, Double>>(emptyMap())
    val categoryTotals: StateFlow<Map<String, Double>> = _categoryTotals.asStateFlow()
    
    init {
        loadAllExpenses()
        loadCategories()
    }
    
    fun loadAllExpenses() {
        viewModelScope.launch {
            repository.getAllExpenses().collect {
                _expenses.value = it
                calculateCategoryTotals()
            }
        }
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collect {
                _categories.value = it
            }
        }
    }
    
    private fun calculateCategoryTotals() {
        val currentExpenses = _expenses.value
        val totals = mutableMapOf<String, Double>()
        currentExpenses.groupBy { it.category }.forEach { (category, expenses) ->
            totals[category] = expenses.sumOf { it.amount }
        }
        _categoryTotals.value = totals
    }
    
    fun loadExpensesByCategory(category: String) {
        viewModelScope.launch {
            repository.getExpensesByCategory(category).collect {
                _expenses.value = it
            }
        }
    }
    
    fun loadExpensesByDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            repository.getExpensesByDateRange(startDate, endDate).collect {
                _expenses.value = it
            }
        }
    }
    
    fun loadExpenseById(id: Long) {
        viewModelScope.launch {
            _currentExpense.value = repository.getExpenseById(id)
        }
    }
    
    fun saveExpense(expense: Expense) {
        viewModelScope.launch {
            if (expense.id == 0L) {
                repository.insertExpense(expense)
            } else {
                repository.updateExpense(expense)
            }
        }
    }
    
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
    
    suspend fun getTotalExpenseInDateRange(startDate: String, endDate: String): Double {
        return repository.getTotalExpenseInDateRange(startDate, endDate)
    }
    
    fun refreshCategoryTotals() {
        calculateCategoryTotals()
    }
    
    fun deleteAllExpenses() {
        viewModelScope.launch {
            repository.deleteAllExpenses()
        }
    }
}

class ExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
