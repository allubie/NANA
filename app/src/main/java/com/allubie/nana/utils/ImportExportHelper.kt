package com.allubie.nana.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import com.allubie.nana.data.entity.Note
import com.allubie.nana.data.entity.Schedule
import com.allubie.nana.data.entity.Routine
import com.allubie.nana.data.entity.Expense
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Serializable
data class BackupData(
    val notes: List<Note>,
    val schedules: List<Schedule>,
    val routines: List<Routine>,
    val expenses: List<Expense>,
    val version: String = "1.0",
    val exportDate: String
)

class ImportExportHelper(private val context: Context) {
    
    companion object {
        private const val BACKUP_FILE_NAME = "nana_backup.json"
        private const val MIME_TYPE = "application/json"
    }
    
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
    
    suspend fun exportData(
        notes: List<Note>,
        schedules: List<Schedule>,
        routines: List<Routine>,
        expenses: List<Expense>
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val backupData = BackupData(
                notes = notes,
                schedules = schedules,
                routines = routines,
                expenses = expenses,
                exportDate = System.currentTimeMillis().toString()
            )
            
            val jsonString = json.encodeToString(backupData)
            val file = File(context.getExternalFilesDir(null), BACKUP_FILE_NAME)
            
            FileOutputStream(file).use { outputStream ->
                outputStream.write(jsonString.toByteArray())
            }
            
            Result.success(Uri.fromFile(file))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun importData(uri: Uri): Result<BackupData> = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val jsonString = inputStream?.bufferedReader()?.use { it.readText() }
                ?: return@withContext Result.failure(Exception("Failed to read file"))
            
            val backupData = json.decodeFromString<BackupData>(jsonString)
            Result.success(backupData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun createShareIntent(uri: Uri): android.content.Intent {
        return android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            type = MIME_TYPE
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
    }
}
