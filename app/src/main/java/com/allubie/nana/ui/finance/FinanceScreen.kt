package com.allubie.nana.ui.finance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel = hiltViewModel()
) {
    // Collect state from ViewModel
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpenses by viewModel.totalExpenses.collectAsStateWithLifecycle()
    val balance by viewModel.balance.collectAsStateWithLifecycle(initialValue = 0.0)

    // Dialog state
    var showTransactionDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }

    // Formatting
    val currencyFormat = remember { NumberFormat.getCurrencyInstance() }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Finance", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Income FAB
                FloatingActionButton(
                    onClick = {
                        selectedTransaction = null
                        transactionType = TransactionType.INCOME
                        showTransactionDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Outlined.ArrowUpward, contentDescription = "Add Income")
                }

                // Expense FAB
                FloatingActionButton(
                    onClick = {
                        selectedTransaction = null
                        transactionType = TransactionType.EXPENSE
                        showTransactionDialog = true
                    }
                ) {
                    Icon(Icons.Outlined.ArrowDownward, contentDescription = "Add Expense")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Summary cards
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Current Balance",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currencyFormat.format(balance),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (balance >= 0) Color(0xFF388E3C) else Color(0xFFD32F2F)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Income",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = currencyFormat.format(totalIncome),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF388E3C)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Expenses",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = currencyFormat.format(totalExpenses),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }
                }
            }

            // Recent transactions header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            // Transactions list
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions yet.\nTap + to add a transaction.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionCard(
                            transaction = transaction,
                            currencyFormat = currencyFormat,
                            onEditClick = {
                                selectedTransaction = transaction
                                transactionType = transaction.type
                                showTransactionDialog = true
                            },
                            onDeleteClick = {
                                // Use ViewModel to delete transaction
                                viewModel.deleteTransaction(transaction)
                            }
                        )
                    }
                }
            }

            // Transaction dialog
            if (showTransactionDialog) {
                TransactionDialog(
                    transaction = selectedTransaction,
                    transactionType = transactionType,
                    onDismiss = { showTransactionDialog = false },
                    onSave = { amount, title, description, category, type ->
                        if (selectedTransaction == null) {
                            // Add new transaction via ViewModel
                            viewModel.addTransaction(amount, title, description, category, type)
                        } else {
                            // Update existing transaction via ViewModel
                            viewModel.updateTransaction(
                                selectedTransaction!!, amount, title, description, category, type
                            )
                        }
                        showTransactionDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: Transaction,
    currencyFormat: NumberFormat,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            Icon(
                transaction.category.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Transaction details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = dateFormatter.format(transaction.date),
                    style = MaterialTheme.typography.bodySmall
                )

                if (transaction.description.isNotBlank()) {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Amount
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (transaction.type == TransactionType.INCOME)
                        "+${currencyFormat.format(transaction.amount)}"
                    else
                        "-${currencyFormat.format(transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (transaction.type == TransactionType.INCOME)
                        Color(0xFF388E3C)
                    else
                        Color(0xFFD32F2F)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    // Edit button
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete button
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDialog(
    transaction: Transaction?,
    transactionType: TransactionType,
    onDismiss: () -> Unit,
    onSave: (amount: Double, title: String, description: String, category: TransactionCategory, type: TransactionType) -> Unit
) {
    var amount by remember { mutableStateOf((transaction?.amount ?: 0.0).toString()) }
    var title by remember { mutableStateOf(transaction?.title ?: "") }
    var description by remember { mutableStateOf(transaction?.description ?: "") }
    var category by remember { mutableStateOf(transaction?.category ?:
    if (transactionType == TransactionType.INCOME) TransactionCategory.SALARY
    else TransactionCategory.OTHER)
    }
    var type by remember { mutableStateOf(transaction?.type ?: transactionType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (transaction == null) {
                    if (transactionType == TransactionType.INCOME) "Add Income" else "Add Expense"
                } else {
                    if (transaction.type == TransactionType.INCOME) "Edit Income" else "Edit Expense"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Amount field
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        // Only allow numeric input
                        val newValue = it.replace(Regex("[^0-9.]"), "")
                        amount = newValue
                    },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("$") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category selection
                Text("Category:", style = MaterialTheme.typography.titleSmall)

                val categories = if (transactionType == TransactionType.INCOME) {
                    listOf(
                        TransactionCategory.SALARY,
                        TransactionCategory.ALLOWANCE,
                        TransactionCategory.SAVINGS,
                        TransactionCategory.OTHER
                    )
                } else {
                    listOf(
                        TransactionCategory.FOOD,
                        TransactionCategory.TRANSPORTATION,
                        TransactionCategory.ENTERTAINMENT,
                        TransactionCategory.EDUCATION,
                        TransactionCategory.SHOPPING,
                        TransactionCategory.HOUSING,
                        TransactionCategory.HEALTH,
                        TransactionCategory.OTHER
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Display categories in rows of 3
                categories.chunked(3).forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowCategories.forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat.label) },
                                leadingIcon = {
                                    Icon(cat.icon, contentDescription = null)
                                },
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        // Add empty spaces if needed to maintain layout
                        repeat(3 - rowCategories.size) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    if (amountValue > 0 && title.isNotBlank()) {
                        onSave(amountValue, title, description, category, type)
                    }
                },
                enabled = amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0 && title.isNotBlank()
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

// Sample transactions for demonstration
private fun getSampleTransactions(): List<Transaction> {
    return listOf(
        Transaction(
            amount = 750.0,
            title = "Monthly Allowance",
            description = "From parents",
            category = TransactionCategory.ALLOWANCE,
            type = TransactionType.INCOME,
            date = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -3) }.time
        ),
        Transaction(
            amount = 300.0,
            title = "Tutoring",
            description = "Math tutoring - 6 hours",
            category = TransactionCategory.SALARY,
            type = TransactionType.INCOME,
            date = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -7) }.time
        ),
        Transaction(
            amount = 45.99,
            title = "Textbooks",
            description = "Computer Science textbook",
            category = TransactionCategory.EDUCATION,
            type = TransactionType.EXPENSE,
            date = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }.time
        ),
        Transaction(
            amount = 12.50,
            title = "Lunch",
            description = "Campus cafeteria",
            category = TransactionCategory.FOOD,
            type = TransactionType.EXPENSE,
            date = Calendar.getInstance().time
        ),
        Transaction(
            amount = 25.0,
            title = "Bus Pass",
            category = TransactionCategory.TRANSPORTATION,
            type = TransactionType.EXPENSE,
            date = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -5) }.time
        )
    )
}