package com.allubie.nana.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val richContent: String = "", // JSON string for rich content
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val category: String = "",
    val color: String = "",
    val createdAt: String, // Store as String for Room compatibility
    val updatedAt: String,
    val hasImages: Boolean = false,
    val hasCheckboxes: Boolean = false,
    val hasLinks: Boolean = false,
    val hasFormatting: Boolean = false
)
