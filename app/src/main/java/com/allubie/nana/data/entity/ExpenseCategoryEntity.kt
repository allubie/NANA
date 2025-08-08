package com.allubie.nana.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_categories")
data class ExpenseCategoryEntity(
    @PrimaryKey
    val name: String,
    val iconName: String, // Store icon name as string
    val colorHex: String, // Store color as hex string
    val monthlyBudget: Double = 0.0
)
