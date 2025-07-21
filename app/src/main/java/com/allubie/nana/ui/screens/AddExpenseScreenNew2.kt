package com.allubie.nana.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.allubie.nana.data.entity.Expense
import com.allubie.nana.ui.viewmodel.ExpenseViewModel
import com.allubie.nana.ui.viewmodel.SettingsViewModel
import com.allubie.nana.utils.CurrencyFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreenNew2(
    expenseViewModel: ExpenseViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    expenseId: Long? = null,
    modifier: Modifier = Modifier
) {
    // Form state
    val currencyFormat by settingsViewModel.currencyFormat.collectAsState()
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    
    // Load existing expense if editing
    val currentExpense by expenseViewModel.currentExpense.collectAsState()
    
    LaunchedEffect(expenseId) {
        expenseId?.let { id ->
            if (id != 0L) {
                expenseViewModel.loadExpenseById(id)
            }
        }
    }
    
    // Update form fields when expense is loaded
    LaunchedEffect(currentExpense) {
        currentExpense?.let { expense ->
            description = expense.description
            amount = expense.amount.toString()
            category = expense.category
            date = expense.date
        }
    }
    
    // Categories for dropdown - use ViewModel categories
    val categories by expenseViewModel.categories.collectAsState()
    var isFormValid by remember { mutableStateOf(false) }
    
    // Validate form
    LaunchedEffect(description, amount, category) {
        isFormValid = description.isNotBlank() && 
                     amount.isNotBlank() && 
                     amount.toDoubleOrNull() != null &&
                     amount.toDoubleOrNull()!! > 0 &&
                     category.isNotBlank()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (expenseId != null) "Edit Expense" else "Add Expense",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (expenseId != null) {
                        IconButton(
                            onClick = { showDeleteDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Expense",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description*") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Amount
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount*") },
                    prefix = { Text(CurrencyFormatter.getCurrencySymbol(currencyFormat)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                
                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Category*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { categoryOption ->
                            DropdownMenuItem(
                                text = { Text(categoryOption) },
                                onClick = {
                                    category = categoryOption
                                    expandedCategory = false
                                }
                            )
                        }
                        // Add option to create new category
                        DropdownMenuItem(
                            text = { Text("+ Add New Category") },
                            onClick = {
                                // TODO: Implement add new category functionality
                                expandedCategory = false
                            }
                        )
                    }
                }
                
                // Date
                OutlinedTextField(
                    value = formatDateForDisplay(date),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Date") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Date"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            
            Button(
                onClick = {
                    val expenseAmount = amount.toDoubleOrNull() ?: 0.0
                    val now = kotlinx.datetime.Clock.System.now().toString() // Use ISO instant format
                    
                    val expense = if (expenseId != null) {
                        // Update existing expense
                        Expense(
                            id = expenseId,
                            amount = expenseAmount,
                            description = description,
                            category = category,
                            date = date,
                            createdAt = now,
                            updatedAt = now
                        )
                    } else {
                        // Create new expense
                        Expense(
                            amount = expenseAmount,
                            description = description,
                            category = category,
                            date = date,
                            createdAt = now,
                            updatedAt = now
                        )
                    }
                    
                    expenseViewModel.saveExpense(expense)
                    
                    onNavigateBack()
                },
                modifier = Modifier.weight(1f),
                enabled = isFormValid
            ) {
                Text(if (expenseId != null) "Update" else "Save")
            }
        }
    }
    
    // Date Picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            yearRange = 2025..2030  // Restrict years from 2025 to 2030
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            date = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                                .toString()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Delete Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Expense") },
            text = { Text("Are you sure you want to delete this expense? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        expenseId?.let { id ->
                            // For now, we'll skip the delete operation
                            // This would require implementing deleteExpenseById in the ViewModel
                        }
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
        }
    }
}

private fun formatDateForDisplay(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    } catch (e: Exception) {
        dateString
    }
}
