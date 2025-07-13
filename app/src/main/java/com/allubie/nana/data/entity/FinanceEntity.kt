package com.allubie.nana.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.allubie.nana.ui.finance.TransactionCategory
import com.allubie.nana.ui.finance.TransactionType
import java.util.Date
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val title: String,
    val description: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val categoryName: String = "OTHER",
    val typeName: String = "EXPENSE"
)

class TransactionConverters {
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(name: String): TransactionType {
        return try {
            TransactionType.valueOf(name)
        } catch (e: Exception) {
            TransactionType.EXPENSE
        }
    }

    @TypeConverter
    fun fromTransactionCategory(category: TransactionCategory): String {
        return category.name
    }

    @TypeConverter
    fun toTransactionCategory(name: String): TransactionCategory {
        return try {
            TransactionCategory.valueOf(name)
        } catch (e: Exception) {
            TransactionCategory.OTHER
        }
    }

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(timeMillis: Long): Date {
        return Date(timeMillis)
    }
}