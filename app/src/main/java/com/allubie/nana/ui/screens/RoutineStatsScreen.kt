package com.allubie.nana.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.allubie.nana.data.database.NANADatabase
import com.allubie.nana.data.entity.Routine
import com.allubie.nana.data.entity.RoutineFrequency
import com.allubie.nana.data.repository.RoutineRepository
import com.allubie.nana.ui.viewmodel.RoutineViewModel
import com.allubie.nana.ui.viewmodel.RoutineViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineStatsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val database = NANADatabase.getDatabase(context)
    val repository = RoutineRepository(database.routineDao(), context)
    val viewModel: RoutineViewModel = viewModel(factory = RoutineViewModelFactory(repository))
    
    val routines by viewModel.routines.collectAsState()
    
    // Calculate statistics
    val today = LocalDate.now()
    val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
    val startOfMonth = today.withDayOfMonth(1)
    
    val dailyRoutines = routines.filter { it.frequency == RoutineFrequency.DAILY }
    val weeklyRoutines = routines.filter { it.frequency == RoutineFrequency.WEEKLY }
    
    // Calculate completion rates
    val todayCompletions = dailyRoutines.count { it.isCompleted }
    val dailyCompletionRate = if (dailyRoutines.isNotEmpty()) {
        (todayCompletions.toFloat() / dailyRoutines.size * 100).roundToInt()
    } else 0
    
    val weeklyCompletions = weeklyRoutines.count { it.isCompleted }
    val weeklyCompletionRate = if (weeklyRoutines.isNotEmpty()) {
        (weeklyCompletions.toFloat() / weeklyRoutines.size * 100).roundToInt()
    } else 0
    
    // Calculate streaks (simplified - in real implementation, you'd track completion history)
    val activeRoutines = routines.filter { it.isActive }
    val completedRoutines = activeRoutines.filter { it.isCompleted }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Routine Statistics") },
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
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overview Cards
            item {
                Text(
                    text = "Today's Progress",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Daily Progress Card
                    Card(
                        modifier = Modifier.weight(1f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Daily Routines",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$todayCompletions/${dailyRoutines.size}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "$dailyCompletionRate% Complete",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = dailyCompletionRate / 100f,
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    // Weekly Progress Card
                    Card(
                        modifier = Modifier.weight(1f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Weekly Routines",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$weeklyCompletions/${weeklyRoutines.size}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "$weeklyCompletionRate% Complete",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = weeklyCompletionRate / 100f,
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
            
            // Habit Tracking Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Habit Tracking",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Active Habits",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (activeRoutines.isEmpty()) {
                            Text(
                                text = "No active routines yet. Create some routines to start tracking your habits!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            activeRoutines.forEach { routine ->
                                HabitTrackingItem(
                                    routine = routine,
                                    onToggleComplete = { 
                                        viewModel.toggleCompletionStatus(routine.id, !routine.isCompleted)
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
            
            // Weekly Calendar View (Simplified)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This Week's Overview",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Week of ${startOfWeek.format(DateTimeFormatter.ofPattern("MMM dd"))}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Simple week view
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            repeat(7) { dayIndex ->
                                val dayDate = startOfWeek.plusDays(dayIndex.toLong())
                                val isToday = dayDate == today
                                val dayName = dayDate.format(DateTimeFormatter.ofPattern("E"))
                                
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = dayName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isToday) MaterialTheme.colorScheme.primary 
                                               else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = dayDate.dayOfMonth.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isToday) MaterialTheme.colorScheme.primary 
                                               else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    // Simplified completion indicator
                                    Surface(
                                        modifier = Modifier.size(8.dp),
                                        shape = MaterialTheme.shapes.small,
                                        color = if (isToday && completedRoutines.isNotEmpty()) 
                                                   MaterialTheme.colorScheme.primary
                                               else MaterialTheme.colorScheme.surfaceVariant
                                    ) {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitTrackingItem(
    routine: Routine,
    onToggleComplete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = routine.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${routine.frequency.name.lowercase().replaceFirstChar { it.uppercase() }} routine",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Simple streak indicator (TODO: implement real streak calculation)
            if (routine.isCompleted) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "🔥 1",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Checkbox(
                checked = routine.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )
        }
    }
}
