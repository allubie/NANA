package com.allubie.nana.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.allubie.nana.ui.notes.NoteColor
import java.util.UUID

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val colorName: String = "Default"
)

class NoteConverters {
    @TypeConverter
    fun fromNoteColor(noteColor: NoteColor): String {
        return noteColor.name
    }

    @TypeConverter
    fun toNoteColor(colorName: String): NoteColor {
        return try {
            NoteColor.valueOf(colorName)
        } catch (e: Exception) {
            NoteColor.Default
        }
    }
}