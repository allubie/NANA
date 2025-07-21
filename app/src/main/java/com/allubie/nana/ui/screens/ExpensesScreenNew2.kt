package com.allubie.nana.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allubie.nana.data.entity.Expense
import com.allubie.nana.ui.viewmodel.ExpenseViewModel
import com.allubie.nana.ui.viewmodel.SettingsViewModel
import com.allubie.nana.utils.CurrencyFormatter
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreenNew2(
    expenseViewModel: ExpenseViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val expenses by expenseViewModel.expenses.collectAsState()
    val currencyFormat by settingsViewModel.currencyFormat.collectAsState()
    
    // Simple budget for now - this can be extended to use SettingsViewModel later
    val monthlyBudget = 1000.0
    
    // Calculate current month expenses
    val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    val currentMonthExpenses = expenses.filter { expense ->
        expense.date.startsWith(currentMonth)
    }
    val totalSpent = currentMonthExpenses.sumOf { it.amount }
    val remainingBudget = monthlyBudget - totalSpent
    val budgetProgress = if (monthlyBudget > 0) (totalSpent / monthlyBudget).toFloat() else 0f
    
    // Category breakdown
    val categoryTotals = currentMonthExpenses
        .groupBy { it.category }
        .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }
    
    // UI state
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf("Date (Newest)") }
    var selectedCategoryFilter by remember { mutableStateOf("All Categories") }
    
    // Get all categories for filtering
    val allCategories = expenses.map { it.category }.distinct().sorted()
    
    // Apply sorting and filtering
    val sortedAndFilteredExpenses = expenses
        .let { expenseList ->
            // Apply category filter
            if (selectedCategoryFilter == "All Categories") {
                expenseList
            } else {
                expenseList.filter { it.category == selectedCategoryFilter }
            }
        }
        .let { expenseList ->
            // Apply sorting
            when (selectedSortOption) {
                "Date (Newest)" -> expenseList.sortedByDescending { it.date }
                "Date (Oldest)" -> expenseList.sortedBy { it.date }
                "Amount (Highest)" -> expenseList.sortedByDescending { it.amount }
                "Amount (Lowest)" -> expenseList.sortedBy { it.amount }
                "Category" -> expenseList.sortedBy { it.category }
                else -> expenseList.sortedByDescending { it.date }
            }
        }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Expenses",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Overflow menu
                Box {
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = { 
                                onNavigateToSettings()
                                showOverflowMenu = false 
                            }
                        )
                        
                        // Sort options
                        Text(
                            text = "Sort by",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        
                        listOf(
                            "Date (Newest)", 
                            "Date (Oldest)", 
                            "Amount (Highest)", 
                            "Amount (Lowest)", 
                            "Category"
                        ).forEach { sortOption ->
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (selectedSortOption == sortOption) {
                                            Icon(
                                                Icons.Default.Check, 
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        } else {
                                            Spacer(modifier = Modifier.width(24.dp))
                                        }
                                        Text(sortOption)
                                    }
                                },
                                onClick = { 
                                    selectedSortOption = sortOption
                                    showOverflowMenu = false 
                                }
                            )
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        // Filter by category
                        Text(
                            text = "Filter by Category",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        
                        DropdownMenuItem(
                            text = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (selectedCategoryFilter == "All Categories") {
                                        Icon(
                                            Icons.Default.Check, 
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    } else {
                                        Spacer(modifier = Modifier.width(24.dp))
                                    }
                                    Text("All Categories")
                                }
                            },
                            onClick = { 
                                selectedCategoryFilter = "All Categories"
                                showOverflowMenu = false 
                            }
                        )
                        
                        allCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (selectedCategoryFilter == category) {
                                            Icon(
                                                Icons.Default.Check, 
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        } else {
                                            Spacer(modifier = Modifier.width(24.dp))
                                        }
                                        Text(category)
                                    }
                                },
                                onClick = { 
                                    selectedCategoryFilter = category
                                    showOverflowMenu = false 
                                }
                            )
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        DropdownMenuItem(
                            text = { Text("Delete All Expenses") },
                            onClick = { 
                                showDeleteAllDialog = true
                                showOverflowMenu = false 
                            }
                        )
                    }
                }
                
                FloatingActionButton(
                    onClick = onNavigateToAddExpense,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Expense"
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Budget Overview Card
        BudgetOverviewCard(
            monthlyBudget = monthlyBudget,
            totalSpent = totalSpent,
            remainingBudget = remainingBudget,
            budgetProgress = budgetProgress,
            currencyFormat = currencyFormat,
            onEditBudget = { showBudgetDialog = true }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Category Breakdown
        if (categoryTotals.isNotEmpty()) {
            CategoryBreakdownCard(
                categoryTotals = categoryTotals,
                totalSpent = totalSpent,
                currencyFormat = currencyFormat
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Recent Expenses
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Expenses",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Show current filter/sort status
                    Column(horizontalAlignment = Alignment.End) {
                        if (selectedCategoryFilter != "All Categories") {
                            Text(
                                text = "Category: $selectedCategoryFilter",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = selectedSortOption,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (expenses.isEmpty()) {
                    Text(
                        text = "No expenses yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else if (sortedAndFilteredExpenses.isEmpty()) {
                    Text(
                        text = "No expenses match the current filter",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn {
                        items(sortedAndFilteredExpenses.take(10)) { expense ->
                            ExpenseItemCard(
                                expense = expense,
                                currencyFormat = currencyFormat,
                                onDeleteExpense = { expenseViewModel.deleteExpense(it) }
                            )
                        }
                        
                        if (sortedAndFilteredExpenses.size > 10) {
                            item {
                                Text(
                                    text = "Showing ${10} of ${sortedAndFilteredExpenses.size} expenses",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Budget Edit Dialog
    if (showBudgetDialog) {
        BudgetEditDialog(
            currentBudget = monthlyBudget,
            currencyFormat = currencyFormat,
            onSaveBudget = { newBudget ->
                // TODO: Implement budget saving through SettingsViewModel
                showBudgetDialog = false
            },
            onDismiss = { showBudgetDialog = false }
        )
    }
    
    // Delete All Confirmation Dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("Delete All Expenses") },
            text = { 
                Text(
                    "Are you sure you want to delete all expenses? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        expenseViewModel.deleteAllExpenses()
                        showDeleteAllDialog = false
                    }
                ) {
                    Text("Delete All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BudgetOverviewCard(
    monthlyBudget: Double,
    totalSpent: Double,
    remainingBudget: Double,
    budgetProgress: Float,
    currencyFormat: String,
    onEditBudget: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monthly Budget",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onEditBudget) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Budget"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = CurrencyFormatter.formatAmount(monthlyBudget, currencyFormat),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = budgetProgress.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth(),
                color = if (budgetProgress > 1f) MaterialTheme.colorScheme.error 
                       else MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Spent",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormatter.formatAmount(totalSpent, currencyFormat),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormatter.formatAmount(remainingBudget, currencyFormat),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (remainingBudget < 0) MaterialTheme.colorScheme.error 
                               else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryBreakdownCard(
    categoryTotals: List<Pair<String, Double>>,
    totalSpent: Double,
    currencyFormat: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Category Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            categoryTotals.take(5).forEach { (category, amount) ->
                val percentage = if (totalSpent > 0) (amount / totalSpent * 100) else 0.0
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${percentage.toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Text(
                        text = CurrencyFormatter.formatAmount(amount, currencyFormat),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseItemCard(
    expense: Expense,
    currencyFormat: String,
    onDeleteExpense: (Expense) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${expense.category} • ${formatDate(expense.date)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = CurrencyFormatter.formatAmount(expense.amount, currencyFormat),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Expense") },
            text = { Text("Are you sure you want to delete this expense?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteExpense(expense)
                        showDeleteDialog = false
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

@Composable
private fun BudgetEditDialog(
    currentBudget: Double,
    currencyFormat: String,
    onSaveBudget: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var budgetText by remember { mutableStateOf(currentBudget.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Monthly Budget") },
        text = {
            OutlinedTextField(
                value = budgetText,
                onValueChange = { budgetText = it },
                label = { Text("Monthly Budget") },
                prefix = { Text(CurrencyFormatter.getCurrencySymbol(currencyFormat)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    budgetText.toDoubleOrNull()?.let { newBudget ->
                        if (newBudget >= 0) {
                            onSaveBudget(newBudget)
                        }
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

private fun formatDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
        date.format(DateTimeFormatter.ofPattern("MMM dd"))
    } catch (e: Exception) {
        dateString
    }
}
