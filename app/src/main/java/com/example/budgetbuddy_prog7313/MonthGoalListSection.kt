package com.example.budgetbuddy_prog7313

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy_prog7313.data.AppDatabase
import com.example.budgetbuddy_prog7313.data.MonthlyGoal
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthGoalListSection(title: String, isFuture: Boolean) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val goalDao = db.monthlyGoalDao()
    val expenseDao = db.expenseDao()
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(true) }
    var goalList by remember { mutableStateOf<List<MonthlyGoal>>(emptyList()) }
    val currentMonth = YearMonth.now()

    // Listen for all goals and filter them by whether they are future or past
    LaunchedEffect(Unit) {
        goalDao.getAllGoals().collectLatest { allGoals ->
            goalList = allGoals.filter {
                val goalMonth = YearMonth.parse(it.monthId)
                if (isFuture) goalMonth > currentMonth else goalMonth < currentMonth
            }.sortedByDescending { it.monthId }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {

        // Section header that can expand or collapse
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Icon(
                imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                contentDescription = null
            )
        }

        if (expanded) {
            goalList.forEach { goal ->

                var totalSpent by remember(goal.monthId) { mutableStateOf<Float?>(null) }

                // For each goal, calculate how much was spent in that month
                LaunchedEffect(goal.monthId) {
                    val start = "${goal.monthId}-01"
                    val end = "${goal.monthId}-31" // assumes 31-day month
                    expenseDao.getTotalSpentForMonth(start, end).collectLatest {
                        totalSpent = it
                    }
                }

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    val displayMonth = YearMonth.parse(goal.monthId).month
                        .getDisplayName(TextStyle.FULL, Locale.getDefault())

                    Text("• $displayMonth Goal")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Min: R${"%.2f".format(goal.minAmount)} | Max: R${"%.2f".format(goal.maxAmount)}")

                        when {
                            totalSpent == null -> {
                                Text("Calculating...", color = Color.Gray)
                            }
                            totalSpent!! in goal.minAmount..goal.maxAmount -> {
                                Text("Goal Achieved", color = Color.Green)
                            }
                            else -> {
                                val over = totalSpent!! - goal.maxAmount
                                Text(
                                    "Over by R${"%.2f".format(over)}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

