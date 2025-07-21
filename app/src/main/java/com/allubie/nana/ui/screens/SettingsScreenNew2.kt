package com.allubie.nana.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.allubie.nana.ui.theme.ThemeMode
import com.allubie.nana.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenNew2(
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val currentTheme by settingsViewModel.currentTheme.collectAsState()
    val notificationsEnabled by settingsViewModel.notificationsEnabled.collectAsState()
    val autoBackupEnabled by settingsViewModel.autoBackupEnabled.collectAsState()
    val currencyFormat by settingsViewModel.currencyFormat.collectAsState()
    val is24HourFormat by settingsViewModel.is24HourFormat.collectAsState()
    val monthlyBudget by settingsViewModel.monthlyBudget.collectAsState()
    
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Theme Settings
        SettingsCard(
            title = "Appearance",
            icon = Icons.Default.Palette
        ) {
            SettingsItem(
                title = "Theme",
                subtitle = when (currentTheme) {
                    ThemeMode.LIGHT -> "Light"
                    ThemeMode.DARK -> "Dark"
                    ThemeMode.AMOLED -> "AMOLED"
                    ThemeMode.MATERIAL_YOU -> "Material You"
                },
                onClick = { showThemeDialog = true }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Budget Settings
        SettingsCard(
            title = "Budget",
            icon = Icons.Default.AttachMoney
        ) {
            SettingsItem(
                title = "Monthly Budget",
                subtitle = "$${"%.2f".format(monthlyBudget)}",
                onClick = { showBudgetDialog = true }
            )
            
            SettingsItem(
                title = "Currency",
                subtitle = currencyFormat,
                onClick = { showCurrencyDialog = true }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Notification Settings
        SettingsCard(
            title = "Notifications",
            icon = Icons.Default.Notifications
        ) {
            SettingsItemWithSwitch(
                title = "Enable Notifications",
                subtitle = "Get reminders for schedules and routines",
                checked = notificationsEnabled,
                onCheckedChange = { settingsViewModel.setNotificationsEnabled(it) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Data Settings
        SettingsCard(
            title = "Data & Backup",
            icon = Icons.Default.CloudUpload
        ) {
            SettingsItemWithSwitch(
                title = "Auto Backup",
                subtitle = "Automatically backup your data",
                checked = autoBackupEnabled,
                onCheckedChange = { settingsViewModel.setAutoBackupEnabled(it) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Format Settings
        SettingsCard(
            title = "Formats",
            icon = Icons.Default.Schedule
        ) {
            SettingsItemWithSwitch(
                title = "24-Hour Format",
                subtitle = "Use 24-hour time format",
                checked = is24HourFormat,
                onCheckedChange = { settingsViewModel.setTimeFormat24H(it) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // About Section
        SettingsCard(
            title = "About",
            icon = Icons.Default.Info
        ) {
            SettingsItem(
                title = "Version",
                subtitle = "1.0.0",
                onClick = { }
            )
            
            SettingsItem(
                title = "Privacy Policy",
                subtitle = "View our privacy policy",
                onClick = { }
            )
            
            SettingsItem(
                title = "Terms of Service",
                subtitle = "View terms of service",
                onClick = { }
            )
        }
    }
    
    // Dialogs
    if (showBudgetDialog) {
        BudgetDialog(
            currentBudget = monthlyBudget,
            onSave = { newBudget ->
                settingsViewModel.setMonthlyBudget(newBudget)
                showBudgetDialog = false
            },
            onDismiss = { showBudgetDialog = false }
        )
    }
    
    if (showCurrencyDialog) {
        CurrencyDialog(
            currentCurrency = currencyFormat,
            onSave = { newCurrency ->
                settingsViewModel.setCurrencyFormat(newCurrency)
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }
    
    if (showThemeDialog) {
        ThemeDialog(
            currentTheme = currentTheme,
            onSave = { newTheme ->
                settingsViewModel.updateTheme(newTheme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsItemWithSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun BudgetDialog(
    currentBudget: Double,
    onSave: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var budgetText by remember { mutableStateOf(currentBudget.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Monthly Budget") },
        text = {
            Column {
                Text(
                    text = "Set your monthly budget to track expenses",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { budgetText = it },
                    label = { Text("Budget Amount") },
                    prefix = { Text("$") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    budgetText.toDoubleOrNull()?.let { budget ->
                        if (budget >= 0) onSave(budget)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CurrencyDialog(
    currentCurrency: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val currencies = listOf(
        "USD ($)",
        "EUR (€)",
        "GBP (£)",
        "JPY (¥)",
        "CAD (C$)",
        "AUD (A$)"
    )
    
    var selectedCurrency by remember { mutableStateOf(currentCurrency) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Currency Format") },
        text = {
            Column {
                currencies.forEach { currency ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCurrency == currency,
                            onClick = { selectedCurrency = currency }
                        )
                        Text(
                            text = currency,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(selectedCurrency) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ThemeDialog(
    currentTheme: ThemeMode,
    onSave: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val themes = listOf(
        ThemeMode.LIGHT to "Light",
        ThemeMode.DARK to "Dark",
        ThemeMode.AMOLED to "AMOLED",
        ThemeMode.MATERIAL_YOU to "Material You"
    )
    
    var selectedTheme by remember { mutableStateOf(currentTheme) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Theme") },
        text = {
            Column {
                themes.forEach { (theme, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTheme == theme,
                            onClick = { selectedTheme = theme }
                        )
                        Text(
                            text = name,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(selectedTheme) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
