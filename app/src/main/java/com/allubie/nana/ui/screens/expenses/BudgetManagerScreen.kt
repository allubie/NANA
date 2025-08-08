package com.allubie.nana.ui.screens.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.allubie.nana.data.entity.ExpenseCategoryEntity
import com.allubie.nana.ui.viewmodel.ExpensesViewModel
import com.allubie.nana.ui.viewmodel.CategoryWithSpending
import com.allubie.nana.data.preferences.AppPreferences
import com.allubie.nana.utils.getCurrencySymbol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetManagerScreen(
    viewModel: ExpensesViewModel,
    appPreferences: AppPreferences,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState(initial = emptyList())
    val currencySymbol = getCurrencySymbol(appPreferences.currency)
    
    var editingCategoryBudgets by remember { mutableStateOf(false) }
    var budgetEdits by remember { mutableStateOf(mutableMapOf<String, String>()) }
    
    // Initialize budget edits when categories load
    LaunchedEffect(allCategories) {
        if (budgetEdits.isEmpty()) {
            budgetEdits = allCategories.associate { 
                it.name to it.monthlyBudget.toString() 
            }.toMutableMap()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Management") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (editingCategoryBudgets) {
                        IconButton(
                            onClick = {
                                // Save all budget changes
                                budgetEdits.forEach { (categoryName, budgetText) ->
                                    val budget = budgetText.toDoubleOrNull() ?: 0.0
                                    val category = allCategories.find { it.name == categoryName }
                                    if (category != null && category.monthlyBudget != budget) {
                                        viewModel.updateCategory(
                                            category.copy(monthlyBudget = budget)
                                        )
                                    }
                                }
                                editingCategoryBudgets = false
                            }
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                    } else {
                        IconButton(onClick = { editingCategoryBudgets = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Budgets")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Budget Overview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Monthly Budget Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Budget",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "$currencySymbol${String.format("%.2f", uiState.totalBudget)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Column {
                            Text(
                                text = "Total Spent",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "$currencySymbol${String.format("%.2f", uiState.totalSpent)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.totalSpent > uiState.totalBudget) 
                                    MaterialTheme.colorScheme.error 
                                else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Overall progress bar
                    LinearProgressIndicator(
                        progress = uiState.budgetProgress.coerceAtMost(1f),
                        modifier = Modifier.fillMaxWidth(),
                        color = if (uiState.budgetProgress > 1f) 
                            MaterialTheme.colorScheme.error 
                        else MaterialTheme.colorScheme.primary,
                    )
                    
                    Text(
                        text = "${(uiState.budgetProgress * 100).toInt()}% of budget used",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Category Budgets List
            Text(
                text = "Category Budgets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.categoriesWithSpending) { categoryWithSpending ->
                    CategoryBudgetCard(
                        categoryWithSpending = categoryWithSpending,
                        currencySymbol = currencySymbol,
                        isEditing = editingCategoryBudgets,
                        budgetText = budgetEdits[categoryWithSpending.category.name] ?: "0.0",
                        onBudgetChange = { newBudget ->
                            budgetEdits[categoryWithSpending.category.name] = newBudget
                        }
                    )
                }
                
                // Show categories without spending data
                val categoriesWithoutSpending = allCategories.filter { category ->
                    uiState.categoriesWithSpending.none { it.category.name == category.name }
                }
                
                items(categoriesWithoutSpending) { category ->
                    CategoryBudgetCard(
                        categoryWithSpending = CategoryWithSpending(
                            category = category,
                            spent = 0.0,
                            progress = 0.0
                        ),
                        currencySymbol = currencySymbol,
                        isEditing = editingCategoryBudgets,
                        budgetText = budgetEdits[category.name] ?: "0.0",
                        onBudgetChange = { newBudget ->
                            budgetEdits[category.name] = newBudget
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryBudgetCard(
    categoryWithSpending: CategoryWithSpending,
    currencySymbol: String,
    isEditing: Boolean,
    budgetText: String,
    onBudgetChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = categoryWithSpending.category.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "Spent: $currencySymbol${String.format("%.2f", categoryWithSpending.spent)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Budget",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (isEditing) {
                        OutlinedTextField(
                            value = budgetText,
                            onValueChange = onBudgetChange,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.width(120.dp),
                            prefix = { Text(currencySymbol) },
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = "$currencySymbol${String.format("%.2f", categoryWithSpending.category.monthlyBudget)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            if (!isEditing && categoryWithSpending.category.monthlyBudget > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = categoryWithSpending.progress.toFloat().coerceAtMost(1f),
                    modifier = Modifier.fillMaxWidth(),
                    color = when {
                        categoryWithSpending.progress > 1.0 -> MaterialTheme.colorScheme.error
                        categoryWithSpending.progress > 0.8 -> Color(0xFFFF9800) // Orange
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
                
                Text(
                    text = "${(categoryWithSpending.progress * 100).toInt()}% of budget used",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
