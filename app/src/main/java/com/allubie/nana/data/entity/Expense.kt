package com.allubie.nana.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val category: String,
    val categoryIcon: String = "",
    val date: String, // Date in ISO format
    val createdAt: String,
    val updatedAt: String,
    val hasReceipt: Boolean = false,
    val receiptImagePath: String = "",
    val isRecurring: Boolean = false,
    val tags: List<String> = emptyList()
)

@Entity(tableName = "expense_categories")
data class ExpenseCategory(
    @PrimaryKey
    val name: String,
    val icon: String,
    val color: String,
    val budgetLimit: Double = 0.0,
    val isDefault: Boolean = false
)
