package com.example.budgetbuddy_prog7313

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetbuddy_prog7313.data.DailyStats
import com.example.budgetbuddy_prog7313.viewmodels.DailyStatsViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*
import com.example.budgetbuddy_prog7313.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: DailyStatsViewModel = viewModel()
) {
    val context = LocalContext.current
    val todayStats by viewModel.todayStats.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val monthlyStats by viewModel.monthlyStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val levelManager = remember { LevelManager(context) }
    val currentLevel = levelManager.getLevel()
    val currentXP = levelManager.getXP()

    // --- Daily Challenge Logic ---
    var dailyChallenge by remember { mutableStateOf("") }
    var challengeCompleted by remember { mutableStateOf(false) }
    var challengeProgress by remember { mutableStateOf(0f) }

    // For random category challenge
    var randomCategory by remember { mutableStateOf("") }
    val db = remember { AppDatabase.getDatabase(context) }
    val categoryDao = db.categoryDao()
    val expenseDao = db.expenseDao()

    // Generate and check daily challenge
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
            val categories = categoryDao.getAllCategories().first().map { it.name }
            val random = Random(today.hashCode().toLong()) // Seed with date for daily repeatability
            val challengeType = random.nextInt(3)
            val selectedCategory = if (categories.isNotEmpty()) categories[random.nextInt(categories.size)] else ""
            randomCategory = selectedCategory
            when (challengeType) {
                0 -> {
                    dailyChallenge = "Log all your expenses before 8pm today."
                    // Check if all expenses for today were logged before 8pm
                    val expenses = expenseDao.getAllSorted().first().filter { it.date == today }
                    val allBefore8pm = expenses.all { it.endTime < "20:00" }
                    challengeCompleted = expenses.isNotEmpty() && allBefore8pm
                    challengeProgress = if (expenses.isNotEmpty() && allBefore8pm) 1f else 0f
                }
                1 -> {
                    dailyChallenge = "Stay under R300 in total spending today."
                    val expenses = expenseDao.getAllSorted().first().filter { it.date == today }
                    val total = expenses.sumOf { it.amount }
                    challengeCompleted = total < 300.0 && expenses.isNotEmpty()
                    challengeProgress = if (expenses.isNotEmpty()) (1f - (total / 300f).toFloat()).coerceIn(0f, 1f) else 0f
                }
                2 -> {
                    dailyChallenge = if (selectedCategory.isNotEmpty()) "No spending in $selectedCategory today." else "No spending in a random category today."
                    val expenses = expenseDao.getAllSorted().first().filter { it.date == today && it.category == selectedCategory }
                    challengeCompleted = expenses.isEmpty() && selectedCategory.isNotEmpty()
                    challengeProgress = if (selectedCategory.isNotEmpty()) if (expenses.isEmpty()) 1f else 0f else 0f
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Challenge Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Daily Challenge",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dailyChallenge,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = challengeProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                    if (challengeCompleted) {
                        Text(
                            text = "Challenge Completed! +50 XP",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Level and XP Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Level $currentLevel",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = currentXP,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(currentXP * 100).toInt()}% to next level",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Streak Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Current Streak",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    todayStats?.let { stats ->
                        Text(
                            text = "${stats.streakDays} days",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        if (stats.streakDays > 0) {
                            Text(
                                text = "Keep it up!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Weekly Progress
        item {
            Text(
                text = "Weekly Progress",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(weeklyStats) { stats ->
            WeeklyProgressItem(stats)
        }

        // Monthly Achievements
        item {
            Text(
                text = "Monthly Achievements",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(monthlyStats.filter { it.underBudget || it.noUnnecessaryExpenses || it.loggedAllExpenses }) { stats ->
            AchievementItem(stats)
        }
    }
}

@Composable
fun WeeklyProgressItem(stats: DailyStats) {
    val date = LocalDate.parse(stats.date)
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Spent: R${String.format("%.2f", stats.totalSpent)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (stats.dailyChallengeCompleted) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Challenge completed",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AchievementItem(stats: DailyStats) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (stats.underBudget) {
                AchievementRow(
                    icon = Icons.Default.AccountBalanceWallet,
                    text = "Stayed under budget"
                )
            }
            if (stats.noUnnecessaryExpenses) {
                AchievementRow(
                    icon = Icons.Default.CheckCircle,
                    text = "No unnecessary expenses"
                )
            }
            if (stats.loggedAllExpenses) {
                AchievementRow(
                    icon = Icons.Default.Receipt,
                    text = "Logged all expenses"
                )
            }
        }
    }
}

@Composable
fun AchievementRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 