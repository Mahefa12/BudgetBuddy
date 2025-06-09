package com.example.budgetbuddy_prog7313

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.budgetbuddy_prog7313.LevelManager
import com.example.budgetbuddy_prog7313.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first

@Composable
fun HomeScreen(username: String) {
    val context = LocalContext.current
    val levelManager = remember { LevelManager(context) }
    val db = remember { AppDatabase.getDatabase(context) }
    val categoryDao = db.categoryDao()
    val expenseDao = db.expenseDao()

    var level by remember { mutableStateOf(levelManager.getLevel()) }
    var xp by remember { mutableStateOf(levelManager.getXP()) }

    // AI Suggestions state
    var aiSuggestions by remember { mutableStateOf(listOf<String>()) }

    // Load AI suggestions on composition
    LaunchedEffect(Unit) {
        aiSuggestions = withContext(Dispatchers.IO) {
            val includedCategories = categoryDao.getAllCategories().first().filter { cat -> !cat.excludeFromSuggestions }
            val totals = expenseDao.getAllCategoryTotals().first()
            val includedTotals = totals.filter { t -> includedCategories.any { cat -> cat.name == t.category } }
            val totalIncludedSpending = includedTotals.sumOf { it.total }
            val suggestions = mutableListOf<String>()
            if (totalIncludedSpending > 0) {
                for (cat in includedTotals) {
                    val percent = cat.total / totalIncludedSpending
                    if (percent > 0.3) {
                        suggestions.add("You've spent ${(percent * 100).toInt()}% of your included budget on ${cat.category}. Consider reducing your spending in this category.")
                    }
                }
            }
            suggestions
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome, $username",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // AI Suggestions Section
        if (aiSuggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Budget Buddy's Suggestions", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    for (msg in aiSuggestions) {
                        Text("• $msg", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "U",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Level $level", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(12.dp))

        // Progress bar
        LinearProgressIndicator(
            progress = xp,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Progress: ${(xp * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
    }
}
