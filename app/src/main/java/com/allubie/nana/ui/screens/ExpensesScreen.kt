package com.allubie.nana.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.allubie.nana.data.entity.Expense
import com.allubie.nana.data.database.NANADatabase
import com.allubie.nana.data.repository.ExpenseRepository
import com.allubie.nana.data.repository.SettingsRepository
import com.allubie.nana.ui.viewmodel.ExpenseViewModel
import com.allubie.nana.ui.viewmodel.ExpenseViewModelFactory
import com.allubie.nana.ui.viewmodel.SettingsViewModel
import com.allubie.nana.ui.viewmodel.SettingsViewModelFactory
import com.allubie.nana.ui.theme.NANATheme
import com.allubie.nana.utils.CurrencyFormatter
import kotlin.math.min

// Helper function to get category icon
@Composable
fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "food" -> Icons.Default.Restaurant
        "transport" -> Icons.Default.DirectionsCar
        "shopping" -> Icons.Default.ShoppingCart
        "entertainment" -> Icons.Default.Movie
        "health" -> Icons.Default.LocalHospital
        "education" -> Icons.Default.School
        "utilities" -> Icons.Default.Home
        "travel" -> Icons.Default.Flight
        "clothing" -> Icons.Default.Checkroom
        "technology" -> Icons.Default.Computer
        else -> Icons.AutoMirrored.Filled.List
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    onNavigateToExpense: (Long) -> Unit
) {
    val context = LocalContext.current
    val database = NANADatabase.getDatabase(context)
    val repository = ExpenseRepository(database.expenseDao())
    val viewModel: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory(repository))
    val coroutineScope = rememberCoroutineScope()
    
    // Settings for currency formatting
    val settingsRepository = SettingsRepository(context)
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val currencyFormat by settingsViewModel.currencyFormat.collectAsState()
    
    var selectedPeriod by remember { mutableStateOf("This Month") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showBudgetSettings by remember { mutableStateOf(false) }
    var showChartsView by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    
    // Budget state
    val monthlyBudget by settingsRepository.monthlyBudget.collectAsState(initial = 1000.0)
    var budgetEditText by remember { mutableStateOf("") }
    
    val expenses by viewModel.expenses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()
    
    val periods = listOf("Today", "This Week", "This Month", "This Year")
    val displayCategories = listOf("All") + categories
    
    // Initialize budget edit text and refresh data when budget changes
    LaunchedEffect(monthlyBudget) {
        budgetEditText = monthlyBudget.toString()
        // Refresh category totals when budget changes
        viewModel.refreshCategoryTotals()
    }
    
    // Budget calculations - always based on all expenses, not filtered expenses
    val currentSpending = categoryTotals.values.sum()
    val budgetProgress = if (monthlyBudget > 0) (currentSpending / monthlyBudget).toFloat() else 0f
    
    // Filter expenses based on category
    LaunchedEffect(selectedCategory) {
        if (selectedCategory == "All") {
            viewModel.loadAllExpenses() // Ensure we reload all expenses
        } else {
            viewModel.loadExpensesByCategory(selectedCategory)
        }
    }
    
    val totalExpenses = expenses.sumOf { it.amount }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expenses") },
                actions = {
                    IconButton(onClick = { showChartsView = !showChartsView }) {
                        Icon(
                            if (showChartsView) Icons.AutoMirrored.Filled.List else Icons.Default.Info,
                            contentDescription = if (showChartsView) "List view" else "Charts view"
                        )
                    }
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
                                    // Add navigation to settings when available
                                    showOverflowMenu = false 
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Budget Settings") },
                                onClick = { 
                                    showBudgetSettings = !showBudgetSettings
                                    showOverflowMenu = false 
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Categories") },
                                onClick = { 
                                    showCategoryDialog = true
                                    showOverflowMenu = false 
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToExpense(0) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Budget Overview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Monthly Budget",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Budget progress bar
                    LinearProgressIndicator(
                        progress = { budgetProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = when {
                            budgetProgress >= 1.0f -> MaterialTheme.colorScheme.error
                            budgetProgress > 0.8f -> Color(0xFFFF9800) // Orange warning
                            else -> MaterialTheme.colorScheme.primary
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Spent",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyFormatter.formatAmount(currentSpending, currencyFormat),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    budgetProgress >= 1.0f -> MaterialTheme.colorScheme.error
                                    budgetProgress > 0.8f -> Color(0xFFFF9800)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Budget",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyFormatter.formatAmount(monthlyBudget, currencyFormat),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Remaining budget
                    val remaining = monthlyBudget - currentSpending
                    if (remaining > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Remaining: ${CurrencyFormatter.formatAmount(remaining, currencyFormat)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else if (remaining < 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Over budget by: ${CurrencyFormatter.formatAmount(-remaining, currencyFormat)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Period and Category Filters
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(periods) { period ->
                    FilterChip(
                        onClick = { selectedPeriod = period },
                        label = { Text(period) },
                        selected = selectedPeriod == period
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displayCategories) { category ->
                    FilterChip(
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        selected = selectedCategory == category,
                        leadingIcon = if (category != "All") {
                            {
                                Icon(
                                    getCategoryIcon(category),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )
                }
            }

            // Budget Settings Section (Collapsible)
            if (showBudgetSettings) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                                text = "Budget Settings",
                                style = MaterialTheme.typography.titleMedium
                            )
                            IconButton(onClick = { showBudgetSettings = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = budgetEditText,
                            onValueChange = { budgetEditText = it },
                            label = { Text("Monthly Budget") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            prefix = { Text(CurrencyFormatter.getCurrencySymbol(currencyFormat)) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { 
                                    showBudgetSettings = false
                                    budgetEditText = monthlyBudget.toString()
                                }
                            ) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val newBudget = budgetEditText.toDoubleOrNull()
                                    if (newBudget != null && newBudget > 0) {
                                        coroutineScope.launch {
                                            settingsRepository.setMonthlyBudget(newBudget)
                                        }
                                        showBudgetSettings = false
                                    }
                                }
                            ) {
                                Text("Save")
                            }
                        }
                    }
                }
            }

            // Charts or List View
            if (showChartsView) {
                // Category breakdown
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Category Breakdown",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        categoryTotals.forEach { (category, amount) ->
                            val percentage = if (totalExpenses > 0) (amount / totalExpenses) else 0.0
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        getCategoryIcon(category),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = category,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        LinearProgressIndicator(
                                            progress = { percentage.toFloat() },
                                            modifier = Modifier.width(100.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = CurrencyFormatter.formatAmount(amount, currencyFormat),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // Expense List
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(expenses) { expense ->
                        ExpenseItem(
                            expense = expense,
                            currencyFormat = currencyFormat,
                            onClick = { onNavigateToExpense(expense.id) },
                            onDeleteClick = { viewModel.deleteExpense(expense) }
                        )
                    }
                }
            }
        }
    }
    
    // Category Management Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { 
                Text(
                    "Category Overview",
                    style = MaterialTheme.typography.headlineSmall
                ) 
            },
            text = {
                Column {
                    if (categories.isEmpty()) {
                        Text(
                            "No expense categories yet. Add some expenses to see categories here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 300.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categories) { category ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                getCategoryIcon(category),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    category,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    "${expenses.count { it.category == category }} expenses",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Text(
                                            text = categoryTotals[category]?.let { 
                                                CurrencyFormatter.formatAmount(it, currencyFormat) 
                                            } ?: CurrencyFormatter.formatAmount(0.0, currencyFormat),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    currencyFormat: String,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        getCategoryIcon(expense.category),
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = expense.description,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = expense.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = expense.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "-${CurrencyFormatter.formatAmount(expense.amount, currencyFormat)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpensesScreenPreview() {
    NANATheme {
        ExpensesScreen(
            onNavigateToExpense = {}
        )
    }
}