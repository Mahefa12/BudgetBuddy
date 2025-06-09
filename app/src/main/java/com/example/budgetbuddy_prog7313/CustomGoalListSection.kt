package com.example.budgetbuddy_prog7313

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy_prog7313.data.AppDatabase
import com.example.budgetbuddy_prog7313.data.CustomGoal
import com.example.budgetbuddy_prog7313.LevelManager
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomGoalListSection() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.customGoalDao()
    val levelManager = remember { LevelManager(context) }
    val scope = rememberCoroutineScope()

    var activeGoals by remember { mutableStateOf<List<CustomGoal>>(emptyList()) }
    var completedGoals by remember { mutableStateOf<List<CustomGoal>>(emptyList()) }

    var showActive by remember { mutableStateOf(true) }
    var showCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        dao.getActiveGoals().collect { activeGoals = it }
        dao.getCompletedGoals().collect { completedGoals = it }
    }

    Column(modifier = Modifier.fillMaxWidth()) {

        // Header for active goals
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showActive = !showActive }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Custom Goals", style = MaterialTheme.typography.titleMedium)
            Icon(
                imageVector = if (showActive) Icons.Outlined.ExpandMore else Icons.Outlined.ExpandLess,
                contentDescription = null
            )
        }

        if (showActive) {
            if (activeGoals.isEmpty()) {
                Text("No active custom goals.", style = MaterialTheme.typography.bodySmall)
            } else {
                activeGoals.forEach { goal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(goal.name, style = MaterialTheme.typography.titleMedium)
                            Text(goal.description, style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(onClick = {
                                val updated = goal.copy(isCompleted = true)
                                scope.launch {
                                    dao.update(updated)
                                    levelManager.addProgress(0.7f)
                                }
                            }) {
                                Text("Complete")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Header for completed goals
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showCompleted = !showCompleted }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Completed Goals", style = MaterialTheme.typography.titleMedium)
            Icon(
                imageVector = if (showCompleted) Icons.Outlined.ExpandMore else Icons.Outlined.ExpandLess,
                contentDescription = null
            )
        }

        if (showCompleted) {
            if (completedGoals.isEmpty()) {
                Text("No completed goals yet.", style = MaterialTheme.typography.bodySmall)
            } else {
                completedGoals.forEach { goal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(goal.name, style = MaterialTheme.typography.titleMedium)
                            Text(goal.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

