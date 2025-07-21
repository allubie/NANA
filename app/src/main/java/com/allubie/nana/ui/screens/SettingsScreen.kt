package com.allubie.nana.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.allubie.nana.ui.theme.NANATheme
import com.allubie.nana.ui.theme.ThemeMode
import com.allubie.nana.ui.viewmodel.SettingsViewModel
import com.allubie.nana.ui.viewmodel.SettingsViewModelFactory
import com.allubie.nana.utils.NotificationHelper
import com.allubie.nana.utils.ImportExportHelper
import com.allubie.nana.utils.CurrencyFormatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.Manifest
import android.os.Build
import kotlinx.coroutines.launch
import com.allubie.nana.data.database.NANADatabase
import com.allubie.nana.data.repository.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val selectedTheme by settingsViewModel.currentTheme.collectAsState()
    val notificationsEnabled by settingsViewModel.notificationsEnabled.collectAsState()
    val autoBackupEnabled by settingsViewModel.autoBackupEnabled.collectAsState()
    val selectedCurrency by settingsViewModel.currencyFormat.collectAsState()
    val is24HourFormat by settingsViewModel.is24HourFormat.collectAsState()
    
    val notificationHelper = remember { NotificationHelper(context) }
    val importExportHelper = remember { ImportExportHelper(context) }
    
    // Database and repositories for import/export
    val database = NANADatabase.getDatabase(context)
    val noteRepository = NoteRepository(database.noteDao())
    val scheduleRepository = ScheduleRepository(database.scheduleDao(), context)
    val routineRepository = RoutineRepository(database.routineDao(), context)
    val expenseRepository = ExpenseRepository(database.expenseDao())
    
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // File picker launchers
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                try {
                    val notes = noteRepository.getAllNotesForExport()
                    val schedules = scheduleRepository.getAllSchedulesForExport()
                    val routines = routineRepository.getAllRoutinesForExport()
                    val expenses = expenseRepository.getAllExpensesForExport()
                    
                    importExportHelper.exportData(notes, schedules, routines, expenses)
                        .onSuccess {
                            // Success - could show a toast or snackbar
                        }
                        .onFailure {
                            // Error - could show error message
                        }
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }
    
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                try {
                    importExportHelper.importData(it)
                        .onSuccess { backupData ->
                            // Import data to database
                            noteRepository.deleteAllNotes()
                            scheduleRepository.deleteAllSchedules()
                            routineRepository.deleteAllRoutines()
                            expenseRepository.deleteAllExpenses()
                            
                            backupData.notes.forEach { note ->
                                noteRepository.insertNote(note)
                            }
                            backupData.schedules.forEach { schedule ->
                                scheduleRepository.insertSchedule(schedule)
                            }
                            backupData.routines.forEach { routine ->
                                routineRepository.insertRoutine(routine)
                            }
                            backupData.expenses.forEach { expense ->
                                expenseRepository.insertExpense(expense)
                            }
                        }
                        .onFailure {
                            // Error - could show error message
                        }
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }
    
    // Permission launcher for notifications
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        settingsViewModel.setNotificationsEnabled(isGranted)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme section
            item {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Theme",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        ThemeMode.values().forEach { theme ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedTheme == theme,
                                    onClick = { settingsViewModel.updateTheme(theme) }
                                )
                                Text(
                                    text = when (theme) {
                                        ThemeMode.LIGHT -> "Light Theme"
                                        ThemeMode.DARK -> "Dark Theme"
                                        ThemeMode.AMOLED -> "AMOLED Theme"
                                        ThemeMode.MATERIAL_YOU -> "Material You"
                                    },
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Notifications section
            item {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Enable Notifications",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Receive reminders and alerts",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled && !com.allubie.nana.utils.NotificationPermissionHelper(context).areNotificationsEnabled()) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    } else {
                                        settingsViewModel.setNotificationsEnabled(enabled)
                                    }
                                }
                            )
                        }
                        
                        if (notificationsEnabled) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Column {
                                OutlinedButton(
                                    onClick = {
                                        com.allubie.nana.utils.NotificationHelper(context).showNotification(
                                            "NANA Test",
                                            "This is a test notification from NANA app",
                                            "test",
                                            999L
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Test Notification")
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                OutlinedButton(
                                    onClick = {
                                        com.allubie.nana.utils.NotificationDebugger(context).testScheduleNotification()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Test Schedule Notification (2 min)")
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                OutlinedButton(
                                    onClick = {
                                        com.allubie.nana.utils.NotificationDebugger(context).debugNotificationSystem()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Debug Notification System")
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                OutlinedButton(
                                    onClick = {
                                        com.allubie.nana.utils.NotificationPermissionHelper(context).requestExactAlarmPermission()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Request Exact Alarm Permission")
                                }
                            }
                        }
                    }
                }
            }
            
            // Data & Backup section
            item {
                Text(
                    text = "Data & Backup",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Auto Backup",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Automatically backup data daily",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = autoBackupEnabled,
                                onCheckedChange = { settingsViewModel.setAutoBackupEnabled(it) }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { showExportDialog = true }) {
                                Text("Export Data")
                            }
                            TextButton(onClick = { showImportDialog = true }) {
                                Text("Import Data")
                            }
                        }
                    }
                }
            }
            
            // Format section
            item {
                Text(
                    text = "Format",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Currency Setting
                        Text(
                            text = "Currency",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val currencies = CurrencyFormatter.getAllCurrencies()
                        var showCurrencyDropdown by remember { mutableStateOf(false) }
                        
                        ExposedDropdownMenuBox(
                            expanded = showCurrencyDropdown,
                            onExpandedChange = { showCurrencyDropdown = !showCurrencyDropdown }
                        ) {
                            OutlinedTextField(
                                value = selectedCurrency,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCurrencyDropdown) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = showCurrencyDropdown,
                                onDismissRequest = { showCurrencyDropdown = false }
                            ) {
                                currencies.forEach { currency ->
                                    DropdownMenuItem(
                                        text = { Text(currency) },
                                        onClick = {
                                            settingsViewModel.setCurrencyFormat(currency)
                                            showCurrencyDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Time Format Setting
                        Text(
                            text = "Time Format",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !is24HourFormat,
                                onClick = { settingsViewModel.setTimeFormat24H(false) }
                            )
                            Text(
                                text = "12-hour (AM/PM)",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = is24HourFormat,
                                onClick = { settingsViewModel.setTimeFormat24H(true) }
                            )
                            Text(
                                text = "24-hour",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            
            // About section
            item {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                Card(
                    onClick = onNavigateToAbout
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "About NANA",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Navigate to about"
                        )
                    }
                }
            }
        }
    }
    
    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Data") },
            text = { Text("Export all your data to a backup file. This includes notes, schedules, routines, expenses, and settings.") },
            confirmButton = {
                TextButton(onClick = {
                    exportLauncher.launch("nana_backup_${System.currentTimeMillis()}.json")
                    showExportDialog = false
                }) {
                    Text("Export")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Data") },
            text = { Text("Import data from a backup file. This will replace existing data. Make sure to backup current data first.") },
            confirmButton = {
                TextButton(onClick = {
                    importLauncher.launch("application/json")
                    showImportDialog = false
                }) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    NANATheme {
        SettingsScreen(
            onNavigateBack = {},
            onNavigateToAbout = {}
        )
    }
}
