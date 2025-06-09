package com.example.budgetbuddy_prog7313.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy_prog7313.data.AppDatabase
import com.example.budgetbuddy_prog7313.data.CategoryTotal
import com.example.budgetbuddy_prog7313.data.DailyStats
import com.example.budgetbuddy_prog7313.data.ExpenseEntity
import com.example.budgetbuddy_prog7313.data.MonthlyGoal
import com.example.budgetbuddy_prog7313.utils.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val expenseDao = db.expenseDao()
    private val monthlyGoalDao = db.monthlyGoalDao()
    private val dailyStatsDao = db.dailyStatsDao()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _categoryTotals = MutableStateFlow<List<CategoryTotal>>(emptyList())
    val categoryTotals: StateFlow<List<CategoryTotal>> = _categoryTotals.asStateFlow()

    private val _monthlyGoal = MutableStateFlow<MonthlyGoal?>(null)
    val monthlyGoal: StateFlow<MonthlyGoal?> = _monthlyGoal.asStateFlow()

    fun loadData(startDate: ZonedDateTime, endDate: ZonedDateTime) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _categoryTotals.value = expenseDao.getCategoryTotalsBetweenDates(
                    DateUtils.formatDate(startDate),
                    DateUtils.formatDate(endDate)
                ).first()

                val monthId = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM"))
                _monthlyGoal.value = monthlyGoalDao.getGoalForMonth(monthId).firstOrNull()

                updateDailyStats()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun updateDailyStats() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        val totalSpent = _categoryTotals.value.sumOf { it.total }
        val monthlyGoal = _monthlyGoal.value

        // Get or create today's stats
        val todayStats = dailyStatsDao.getStatsForDate(today).first() ?: DailyStats(date = today)

        // Update stats based on current data
        val updatedStats = todayStats.copy(
            totalSpent = totalSpent,
            totalSaved = monthlyGoal?.let { it.maxAmount - totalSpent } ?: 0.0,
            underBudget = monthlyGoal?.let { totalSpent <= it.maxAmount } ?: false,
            loggedAllExpenses = _categoryTotals.value.isNotEmpty(),
            noUnnecessaryExpenses = _categoryTotals.value.none { it.category == "Entertainment" || it.category == "Shopping" }
        )

        dailyStatsDao.update(updatedStats)
    }

    fun addExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            try {
                expenseDao.insert(expense)
                // Award XP for logging expenses
                val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                val stats = dailyStatsDao.getStatsForDate(today).first() ?: DailyStats(date = today)
                dailyStatsDao.update(stats.copy(xpEarned = stats.xpEarned + 10f))
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun setMonthlyGoal(goal: MonthlyGoal) {
        viewModelScope.launch {
            try {
                monthlyGoalDao.insert(goal)
                // Award XP for setting a goal
                val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                val stats = dailyStatsDao.getStatsForDate(today).first() ?: DailyStats(date = today)
                dailyStatsDao.update(stats.copy(xpEarned = stats.xpEarned + 20f))
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
} 